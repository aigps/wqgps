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
 * 该类处理员工做的计划拜访，来获取员工计划拜访客户的详细情况
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
	
	//多员工考勤报表
	public List<CheckWorkReportModel> queryStaffsCheckWorkReport(List<String> staffIds,String startDate,String endDate) throws Exception{
		Map<String,CheckWorkReportModel> modelMap= new LinkedHashMap<String,CheckWorkReportModel>();
		
		try{
			WqCompanyInfo company = AppUtil.getSessionData().getCompany();
			
			Set<String> companyRegionIds = new HashSet<String>();
			if(StringUtils.isNotBlank(company.getRegionIds())){
				companyRegionIds.addAll(Arrays.asList(StringUtils.split(company.getRegionIds(), ",")));
			}
			
			//公司该时间段的所有出差计划
			List<WqTravelPlan> travelPlans = checkWorkReportDAO.findCompanyTravelPlan(company.getId(), startDate, endDate);
			//公司该时间段的所有区域进出记录
			List<Map<String,Object>> regionVisitList = regionVisitDAO.findRegionVisitByComapny(company.getId(), startDate, endDate);
			//员工在该时间段的历史轨迹
			List<Map<String,Object>> gvphList = GvphUtil.findAllGvphList(staffIds, startDate+" 00:00:00", endDate+" 23:59:59", jdbcTemplate);
	
			//如果公司不使用无效定位，把无效定位过滤掉
			if(company.getUseInvalidLoc()!=true){
				for(Iterator<Map<String,Object>> it=gvphList.iterator(); it.hasNext(); ){
					if("0".equals(it.next().get("IS_VALID_GPS"))){
						it.remove();
					}
				}
			}
			
			Calendar calendar = Calendar.getInstance();
			
			String nowDate = DateUtil.getCurDate();
			
			//一个个员工进行统计
			for(String staffId : staffIds){
				WqStaffInfo staff = DataCache.staffMap.get(staffId);
				CheckWorkReportModel model = new CheckWorkReportModel(staffId,staff.getCnName());
				model.needSignIn = staff.getIsClientSignIn() || staff.getIsCompanySignIn();//需要签到
				model.needSignOut = staff.getIsClientSignOut() || staff.getIsCompanySignOut();//需要签退
				modelMap.put(staffId, model);
				
				//获取该员工的所有客户区域IDS
				Set<String> clientRegionIds = new HashSet<String>();
				if(staff.getIsClientSignIn() || staff.getIsClientSignOut()){//有客户区域签到签退，就取客户区域
					Set<String> clientIds = CacheClientStaffIdMap.getClientIds(staffId);
					clientRegionIds.addAll(CacheClientRegionIdMap.getRegionIds(clientIds));
				}
				calendar.setTime(DateUtil.dateFormat.parse(startDate));
				
				do{
					String date = DateUtil.dateFormat.format(calendar.getTime());
					String createDate = DateUtil.dateFormat.format(staff.getCreateTime());
					
					//员工创建日期(激活日期)之中，工作日才统计
					if(date.compareTo(createDate)>=0 && isWorkDay(staff,calendar)){
						if(isTravelDate(travelPlans,staffId,date)){//出差日
							model.travel ++;
							model.addDateState(date, "travel");
						}else if(isOffWork(gvphList,staffId,date)){//脱岗
							model.offwork ++;
							model.addDateState(date, "offwork");
						}else{//正常工作日，有上报定位
							boolean notLateFlag = true, notEarlyFlag = true;
							String notNormalState = "";
							if(model.needSignIn){//需要签到
								notLateFlag = false;
								if(staff.getIsCompanySignIn() && !companyRegionIds.isEmpty()){//可以公司区域签到
									notLateFlag = isInArea(regionVisitList, staff, date, companyRegionIds, true);
								}
								if(notLateFlag == false && staff.getIsClientSignIn()){//可以客户区域签到
									notLateFlag = isInArea(regionVisitList, staff, date, clientRegionIds, true);
								}
								if(!notLateFlag){
									String endTime = date + " " + staff.getSignInEndTime() + ":00";
									//如果是今天，但签到时间还没结束
									if(nowDate.startsWith(date) && nowDate.compareTo(endTime)<0){
										notNormalState = "late***";
									}else{
										model.late ++;
										notNormalState = "late";
									}
								}
							}
							if(model.needSignOut){//需要签退
								notEarlyFlag = false;
								if(staff.getIsCompanySignOut() && !companyRegionIds.isEmpty()){//可以公司区域签退
									notEarlyFlag = isInArea(regionVisitList, staff, date, companyRegionIds, false);
								}
								if(notEarlyFlag == false && staff.getIsClientSignOut()){//可以客户区域签退
									notEarlyFlag = isInArea(regionVisitList, staff, date, clientRegionIds, false);
								}
								if(!notEarlyFlag){
									String endTime = date + " " + staff.getSignOutEndTime() + ":00";
									//如果是今天，但签退时间还没结束
									if(nowDate.startsWith(date) && nowDate.compareTo(endTime)<0){
										notNormalState = notNormalState+"early***";
									}else{
										model.early ++;
										notNormalState = notNormalState+"early";
									}
								}
							}
							if(notLateFlag && notEarlyFlag){//没迟到早退，则正常出勤
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
	
	//判断员工在指定天时，有没报过定位数据
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
	
	//判断日期是否是该员工的出差日
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
	
	//判断日期是否是该员工的工作日
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
	
	//判断员工是否在考勤时间在指定区域里面
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
		
		//员工当天的所有区域进出记录
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
				map.put("ADDR_NAME", "公司");
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

		//按进入时间进行排序
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

