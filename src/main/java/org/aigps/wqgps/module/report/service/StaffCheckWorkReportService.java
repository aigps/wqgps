package org.aigps.wqgps.module.report.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.WqClientInfo;
import org.aigps.wqgps.common.entity.WqCompanyInfo;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.entity.WqTravelPlan;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.common.util.GvphUtil;
import org.aigps.wqgps.module.report.dao.CheckWorkReportDAO;
import org.aigps.wqgps.module.report.dao.RegionVisitDAO;
import org.aigps.wqgps.module.report.model.CheckWorkReportModel;
import org.aigps.wqgps.timing.CacheClientRegionIdMap;
import org.aigps.wqgps.timing.CacheClientStaffIdMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.emory.mathcs.backport.java.util.Collections;
/**
 * ���ദ��Ա�����ļƻ��ݷã�����ȡԱ���ƻ��ݷÿͻ�����ϸ���
 */
@Component
@Transactional
@SuppressWarnings({"unchecked"})
public class StaffCheckWorkReportService {
	public final static Log log = LogFactory.getLog(StaffCheckWorkReportService.class);
	
	private CheckWorkReportDAO checkWorkReportDAO;
	private RegionVisitDAO regionVisitDAO;
	private PublicDAO publicDAO;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	@Autowired
	public void setCheckWorkReportDAO(CheckWorkReportDAO checkWorkReportDAO) {
		this.checkWorkReportDAO = checkWorkReportDAO;
	}
	@Autowired
	public void setRegionVisitDAO(RegionVisitDAO regionVisitDAO) {
		this.regionVisitDAO = regionVisitDAO;
	}
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	
	//��Ա�����ڱ���
	public List<CheckWorkReportModel> queryStaffsCheckWorkReport(List<String> staffIds,String startDate,String endDate) throws Exception{
		Map<String,CheckWorkReportModel> modelMap= new LinkedHashMap<String,CheckWorkReportModel>();
		
		try{
			WqCompanyInfo company = AppUtil.getSessionData().getCompany();
			
			Set<String> companyRegionIds = new HashSet<String>();
			if(StringUtils.isNotBlank(company.getRegionIds())){
				companyRegionIds.addAll(Arrays.asList(StringUtils.split(company.getRegionIds(), ",")));
			}
			
			//��˾��ʱ��ε����г���ƻ�
			List<WqTravelPlan> travelPlans = checkWorkReportDAO.findCompanyTravelPlan(company.getId(), startDate, endDate);
			//��˾��ʱ��ε��������������¼
			List<Map<String,Object>> regionVisitList = regionVisitDAO.findRegionVisitByComapny(company.getId(), startDate, endDate);
			//Ա���ڸ�ʱ��ε���ʷ�켣
			List<Map<String,Object>> gvphList = GvphUtil.findAllGvphList(staffIds, startDate+" 00:00:00", endDate+" 23:59:59", jdbcTemplate);
	
			//�����˾��ʹ����Ч��λ������Ч��λ���˵�
			if(company.getUseInvalidLoc()!=true){
				for(Iterator<Map<String,Object>> it=gvphList.iterator(); it.hasNext(); ){
					if("0".equals(it.next().get("IS_VALID_GPS"))){
						it.remove();
					}
				}
			}
			
			Calendar calendar = Calendar.getInstance();
			
			String nowDate = DateUtil.getCurDate();
			
			//һ����Ա������ͳ��
			for(String staffId : staffIds){
				WqStaffInfo staff = DataCache.staffMap.get(staffId);
				CheckWorkReportModel model = new CheckWorkReportModel(staffId,staff.getCnName());
				model.needSignIn = staff.getIsClientSignIn() || staff.getIsCompanySignIn();//��Ҫǩ��
				model.needSignOut = staff.getIsClientSignOut() || staff.getIsCompanySignOut();//��Ҫǩ��
				modelMap.put(staffId, model);
				
				//��ȡ��Ա�������пͻ�����IDS
				Set<String> clientRegionIds = new HashSet<String>();
				if(staff.getIsClientSignIn() || staff.getIsClientSignOut()){//�пͻ�����ǩ��ǩ�ˣ���ȡ�ͻ�����
					Set<String> clientIds = CacheClientStaffIdMap.getClientIds(staffId);
					clientRegionIds.addAll(CacheClientRegionIdMap.getRegionIds(clientIds));
				}
				calendar.setTime(DateUtil.dateFormat.parse(startDate));
				
				do{
					String date = DateUtil.dateFormat.format(calendar.getTime());
					String createDate = DateUtil.dateFormat.format(staff.getCreateTime());
					
					//Ա����������(��������)֮�У������ղ�ͳ��
					if(date.compareTo(createDate)>=0 && isWorkDay(staff,calendar)){
						if(isTravelDate(travelPlans,staffId,date)){//������
							model.travel ++;
							model.addDateState(date, "travel");
						}else if(isOffWork(gvphList,staffId,date)){//�Ѹ�
							model.offwork ++;
							model.addDateState(date, "offwork");
						}else{//���������գ����ϱ���λ
							boolean notLateFlag = true, notEarlyFlag = true;
							String notNormalState = "";
							if(model.needSignIn){//��Ҫǩ��
								notLateFlag = false;
								if(staff.getIsCompanySignIn() && !companyRegionIds.isEmpty()){//���Թ�˾����ǩ��
									notLateFlag = isInArea(regionVisitList, staff, date, companyRegionIds, true);
								}
								if(notLateFlag == false && staff.getIsClientSignIn()){//���Կͻ�����ǩ��
									notLateFlag = isInArea(regionVisitList, staff, date, clientRegionIds, true);
								}
								if(!notLateFlag){
									String endTime = date + " " + staff.getSignInEndTime() + ":00";
									//����ǽ��죬��ǩ��ʱ�仹û����
									if(nowDate.startsWith(date) && nowDate.compareTo(endTime)<0){
										notNormalState = "late***";
									}else{
										model.late ++;
										notNormalState = "late";
									}
								}
							}
							if(model.needSignOut){//��Ҫǩ��
								notEarlyFlag = false;
								if(staff.getIsCompanySignOut() && !companyRegionIds.isEmpty()){//���Թ�˾����ǩ��
									notEarlyFlag = isInArea(regionVisitList, staff, date, companyRegionIds, false);
								}
								if(notEarlyFlag == false && staff.getIsClientSignOut()){//���Կͻ�����ǩ��
									notEarlyFlag = isInArea(regionVisitList, staff, date, clientRegionIds, false);
								}
								if(!notEarlyFlag){
									String endTime = date + " " + staff.getSignOutEndTime() + ":00";
									//����ǽ��죬��ǩ��ʱ�仹û����
									if(nowDate.startsWith(date) && nowDate.compareTo(endTime)<0){
										notNormalState = notNormalState+"early***";
									}else{
										model.early ++;
										notNormalState = notNormalState+"early";
									}
								}
							}
							if(notLateFlag && notEarlyFlag){//û�ٵ����ˣ�����������
								model.normal ++;
								model.addDateState(date, "normal");
							}else{
								model.addDateState(date, notNormalState);
							}
						}
					}
					if(date.equals(endDate)){
						break;
					}
					calendar.add(Calendar.DATE, 1);
				}while(true);
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		
		return new ArrayList<CheckWorkReportModel>(modelMap.values());
	}
	
	//�ж�Ա����ָ����ʱ����û������λ����
	private boolean isOffWork(List<Map<String,Object>> gvphList, String staffId, String date){
		date = date.replaceAll("-", "");
		for(Map<String,Object> map : gvphList){
			if(staffId.equals(map.get("TMN_ALIAS"))){
				String reportTime = map.get("REPORT_TIME").toString();
				if(reportTime.startsWith(date)){
					return false;
				}
			}
		}
		return true;
	}
	
	//�ж������Ƿ��Ǹ�Ա���ĳ�����
	private boolean isTravelDate(List<WqTravelPlan> travelPlans, String staffId, String date){
		for(WqTravelPlan travel : travelPlans){
			if(travel.getStaffId().equals(staffId)){
				String startDate = travel.getStartTime().substring(0,10);
				String endDate = travel.getEndTime().substring(0,10);
				if(startDate.compareTo(date)<=0 && endDate.compareTo(date)>=0){
					return true;
				}
			}
		}
		return false;
	}
	
	//�ж������Ƿ��Ǹ�Ա���Ĺ�����
	private boolean isWorkDay(WqStaffInfo staff,Calendar calendar){
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
	    if(dayOfWeek<=0){
	    	dayOfWeek = 7;
	    }
	    String workWeekDays = staff.getWorkWeekDays();
	    if(StringUtils.isNotBlank(workWeekDays) && workWeekDays.indexOf(dayOfWeek+"")!=-1){
	    	return true;
	    }
	    return false;
	}
	
	//�ж�Ա���Ƿ��ڿ���ʱ����ָ����������
	private boolean isInArea(List<Map<String,Object>> regionVisitList, WqStaffInfo staff, String date, Set<String> regionIds, boolean moning){
		try{
			String startTime = date + " " + (moning ? staff.getSignInStartTime() : staff.getSignOutStartTime())+":00";
			String endTime = date + " " + (moning ? staff.getSignInEndTime() : staff.getSignOutEndTime())+":00";
			for(Map<String,Object> map : regionVisitList){
				String staffId = (String)map.get("STAFF_ID");
				String regionId = (String)map.get("REGION_ID");
				if(staffId.equals(staff.getId()) && regionIds.contains(regionId)){
					String enterTime = (String)map.get("ENTER_TIME");
					String leaveTime = (String)map.get("LEAVE_TIME");
					if(startTime.compareTo(leaveTime)<=0 && endTime.compareTo(enterTime)>=0){
						return true;
					}
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
		return false;
	}
	
	
	public List<Map<String,Object>> queryStaffDateDetail(String staffId,String date,String staffName){
		WqCompanyInfo company = AppUtil.getSessionData().getCompany();
		
		Set<String> companyRegionIds = new HashSet<String>();
		if(StringUtils.isNotBlank(company.getRegionIds())){
			companyRegionIds.addAll(Arrays.asList(StringUtils.split(company.getRegionIds(), ",")));
		}
		
		Map<String,WqClientInfo> regionClientMap = new HashMap<String,WqClientInfo>();
		Set<String> clientIds = CacheClientStaffIdMap.getClientIds(staffId);
		List<WqClientInfo> clientList = publicDAO.findBy("id", clientIds, WqClientInfo.class);
		for(WqClientInfo client : clientList){
			if(StringUtils.isBlank(client.getRegionIds())){
				continue;
			}
			String[] rids = StringUtils.split(client.getRegionIds(), ",");
			for(String rid : rids){
				regionClientMap.put(rid, client);
			}
		}
		
		//Ա��������������������¼
		List<Map<String,Object>> regionVisitList = regionVisitDAO.findRegionVisitHisByStaffId(staffId, date+" 00:00:00", date+" 23:59:59");
		
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map : regionVisitList){
			String regionId = (String)map.get("REGION_ID");
			if(!companyRegionIds.contains(regionId) && !regionClientMap.containsKey(regionId)){
				continue;
			}
			map.put("STAFF_NAME", staffName);
			map.put("DATE", date);
			if(companyRegionIds.contains(regionId)){
				map.put("ADDR_NAME", "��˾");
			}else{
				map.put("ADDR_NAME", regionClientMap.get(regionId).getName());
			}
			
			String enterTime = (String)map.get("ENTER_TIME");
			String leaveTime = (String)map.get("LEAVE_TIME");
			String maxEnterTime = enterTime.compareTo(date+" 00:00:00")>0?enterTime:(date+" 00:00:00");
			String minLeaveTime = leaveTime.compareTo(date+" 23:59:59")<0?leaveTime:(date+" 23:59:59");
			Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
			map.put("STAY_LONG", stayLong);
			map.put("ENTER_TIME", maxEnterTime);
			map.put("LEAVE_TIME", minLeaveTime);
			
			map.put("REGION", DataCache.mapRegionMap.get(regionId));
			
			returnList.add(map);
		}

		//������ʱ���������
		Collections.sort(returnList, new Comparator<Map<String,Object>>() {
			public int compare(Map<String,Object> o1, Map<String,Object> o2) {
				String s1 = (String)o1.get("ENTER_TIME");
				String s2 = (String)o2.get("ENTER_TIME");
				if(s1 == null) s1 = "";
				if(s2 == null) s2 = "";
				return s1.compareTo(s2);
			}
		});
		
		return returnList;
	}
}

