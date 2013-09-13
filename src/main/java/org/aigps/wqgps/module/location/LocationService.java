package org.aigps.wqgps.module.location;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aigps.wqgps.common.cache.CmdStateCache;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.DcGpsHis;
import org.aigps.wqgps.common.entity.DcGpsReal;
import org.aigps.wqgps.common.entity.WqClientInfo;
import org.aigps.wqgps.common.entity.WqCompanyInfo;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.common.entity.WqPlanLocate;
import org.aigps.wqgps.common.entity.WqPlanLocateDetail;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.enums.StaffStateEnum;
import org.aigps.wqgps.common.log.LogType;
import org.aigps.wqgps.common.log.LogUtil;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.module.location.dao.LocationDAO;
import org.aigps.wqgps.socket.CmdTypeEnum;
import org.aigps.wqgps.timing.CacheClientRegionIdMap;
import org.aigps.wqgps.timing.CacheClientStaffIdMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.Arrays;
/**
 * ��λ����
 * @author admin
 *
 */
@Component
@Transactional
@SuppressWarnings("unchecked")
public class LocationService {
	public final static Log log = LogFactory.getLog(LocationService.class);
	private LocationDAO locationDAO;
	private PublicDAO publicDAO;
	@Autowired
	public void setLocationDAO(LocationDAO locationDAO) {
		this.locationDAO = locationDAO;
	}
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	
	public List<DcGpsReal> findPositionByPhones(List<String> phones) throws Exception{
		List<String> staffIdList = new ArrayList<String>();
		WqCompanyInfo company = AppUtil.getSessionData().getCompany();
		for(String phone : phones){
			String staffId = DataCache.phoneStaffIdMap.get(phone);
			if(staffId != null){
				WqStaffInfo staff = DataCache.staffMap.get(staffId);
				if(staff != null && staff.getCompanyId().equals(company.getId())){
					staffIdList.add(staffId);
				}
			}
		}
		if(!staffIdList.isEmpty()){
			return findRealTimePositionByStaffIdList(staffIdList);
		}
		return null;
	}
		
	
//ʵʱ��λ
	/**
	 * ��ʵʱ��λ������Ա��id�б��ȡʵʱ��λ��Ϣ
	 * @param staffIdList Ա��id�б�
	 * @return
	 */
	public List<DcGpsReal> findRealTimePositionByStaffIdList(List<String> staffIdList) throws Exception{
		try {
			String nowDateTime = DateUtil.getNowDate().concat(" 00:00:00");
			Set<String> regionVisitSet = locationDAO.findRegionVisitByStaffIdList(staffIdList, nowDateTime);
			List<DcGpsReal> resultList = new ArrayList<DcGpsReal>();
			if(staffIdList!=null){
				WqCompanyInfo company = AppUtil.getSessionData().getCompany();
				
				for(String staffId:staffIdList){
					DcGpsReal dcGpsReal = DataCache.staffPostionMap.get(staffId);
					if(dcGpsReal!=null){
						//�����˾��ʹ����Ч��λ������Ч��λ���˵����ĳ���һ����Ч��λ
						if(company.getUseInvalidLoc()!=true && dcGpsReal.getIsValidGps()!=true){
							dcGpsReal = (DcGpsReal)BeanUtils.cloneBean(dcGpsReal);
							dcGpsReal.setIsValidGps(true);
							dcGpsReal.setLocDesc("[�ϴζ�λ] "+dcGpsReal.getLocDesc());
						}
						String cmdState = CmdStateCache.getCmdStateCode(CmdTypeEnum.LCSNow.getValue(), staffId);
						dcGpsReal.setCmdState(cmdState);
						String[] state = DataCache.phoneStateMap.get(staffId);
						dcGpsReal.setPhoneState(state==null?"":state[0]);
						resultList.add(dcGpsReal);
						setDcGpsRealState(regionVisitSet,dcGpsReal,nowDateTime);
					}
				}
			}
			return resultList;
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}
	//���ö�λ״̬
	private void setDcGpsRealState(Set<String> regionVisitSet,DcGpsReal dcGpsReal,String nowDateTime){
		String currReportTime = dcGpsReal.getReportTime();
		if(StringUtils.isNotBlank(currReportTime) && currReportTime.compareTo(nowDateTime)>=0){
			if(regionVisitSet.contains(dcGpsReal.getTmnAlias())){//�ڸڲ��ڷ��������ڵ�
				dcGpsReal.setState(StaffStateEnum.ON_WORK_AREA.getValue());
			}else{//��;��
				dcGpsReal.setState(StaffStateEnum.ON_PASSAGE.getValue());
			}
		}else{//�Ѹ�
			dcGpsReal.setState(StaffStateEnum.OFF_SITE.getValue());
		}
	}

//��ʷ��λ	
	/**
	 * ����ʷ��λ������Ա��ID�����Լ�ʱ���ȡ����ʱ������ӽ���ʱ���Ա����ʷ����
	 * �����߼�����ȡѡ�е��������ʱ����㵽������֮�����м�¼��Ȼ��Ƚϻ�ȡ��ӽ���ʱ��ļ�¼
	 * @param staffIdList
	 * @param dateTime
	 * @return
	 * @throws Exception
	 */
	public List<DcGpsHis> findNearDataGpsHisByDateTime(List<String> staffIdList,String dateTime) throws Exception{
		try {
			return locationDAO.findNearDataGpsHisByDateTime(staffIdList, dateTime);
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}
//�ƻ���λ	
	/**
	 * (�ƻ���λ)���ݵ�¼�û���ȡ�ƻ���λ����
	 * @return
	 * @throws Exception
	 */
	public List<WqPlanLocate> findWqPlanLocate()throws Exception{
		try {
			String userId = AppUtil.getUserInfo().getId();
			return locationDAO.findWqPlanLocateByUserId(userId);
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}
	
	/**
	 * ������޸ļƻ���λ
	 * @param wqPlanLocate
	 * @param wqPlanLocateDetails
	 * @return
	 * @throws Exception
	 */
	public List<WqPlanLocate> saveOrUpdateWqPlanLocate(WqPlanLocate wqPlanLocate,List<WqPlanLocateDetail> wqPlanLocateDetails) throws Exception{
		try {
			String id = wqPlanLocate.getId();
			boolean createFlag = id==null?true:false;
			WqUserInfo wqUserInfo = AppUtil.getUserInfo();
			String userId = wqUserInfo.getId();
			String companyId = wqUserInfo.getCompanyId();
			if(createFlag){
				wqPlanLocate.setUserId(userId);
				wqPlanLocate.setCompanyId(companyId);
				wqPlanLocate.setProcessFlag(false);
				wqPlanLocate.setCreateTime(new Date());
			}
			locationDAO.save(wqPlanLocate);
			locationDAO.batchExecute("delete from WqPlanLocateDetail wpld where wpld.planLocateId=?", wqPlanLocate.getId());
			for(WqPlanLocateDetail wqPlanLocateDetail:wqPlanLocateDetails){
				wqPlanLocateDetail.setPlanLocateId(wqPlanLocate.getId());
				wqPlanLocateDetail.setCompanyId(companyId);
				locationDAO.save(wqPlanLocateDetail);
			}
			if(createFlag){
				LogUtil.saveLog(LogType.CREATE_PLAN_LOCATE, wqPlanLocate.getName());
			}else{
				LogUtil.saveLog(LogType.UPDATE_PLAN_LOCATE, wqPlanLocate.getName());
			}
			return findWqPlanLocate();
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}
	
	/**
	 * ɾ���ƻ���λ
	 * @param wqPlanLocate
	 * @return
	 * @throws Exception
	 */
	public List<WqPlanLocate> deleteWqPlanLocate(WqPlanLocate wqPlanLocate) throws Exception{
		try {
			locationDAO.batchExecute("delete from WqPlanLocateDetail wpld where wpld.planLocateId=?", wqPlanLocate.getId());
			locationDAO.delete(wqPlanLocate);
			LogUtil.saveLog(LogType.DELETE_PLAN_LOCATE, wqPlanLocate.getName());
			return findWqPlanLocate();
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}
	
	/**
	 * (�ƻ���λ)���ݼƻ���λID��ȡ��ϸ��λ��Ϣ
	 * @param planId
	 * @return
	 */
	public List<WqPlanLocateDetail> findWqPlanLocateDetailByPlanId(String planId)throws Exception{
		try {
			return locationDAO.findWqPlanLocateDetailByPlanId(planId);
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}

//��ʷ�켣
	/**
	 * ����ʷ�켣������Ա��ID��ȡ��ʷ�켣
	 * @param staffId
	 * @param beginDateTime
	 * @param endDateTime
	 * @return
	 */
	public List<DcGpsHis> findDataGpsHisByCondition(String staffId,String beginDateTime,String endDateTime)throws Exception {
		try {
			List<DcGpsHis> list = locationDAO.findDataGpsHisByCondition(staffId, beginDateTime, endDateTime);
			
			WqCompanyInfo company = AppUtil.getSessionData().getCompany();
			if(company.getUseInvalidLoc() != true){//�����˾��ʹ����Ч��λ������Ч��λ���˵�
				for(Iterator<DcGpsHis> it=list.iterator(); it.hasNext(); ){
					DcGpsHis g = it.next();
					if(g.getIsValidGps() == false){
						it.remove();
					}
				}
			}
			return list;
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}
	
	/**
	 * ��ѯԱ������ʷ�ֻ�״̬
	 * @param staffId
	 * @param beginDateTime
	 * @param endDateTime
	 * @return
	 */
	public List<Map<String,Object>> findPhoneStateHis(String staffId,String beginDateTime,String endDateTime)throws Exception {
		try {
			beginDateTime = beginDateTime+":00";
			endDateTime = endDateTime+":59";
			
			List<Map<String,Object>> list = locationDAO.findPhoneStateHis(staffId, beginDateTime, endDateTime);
			
//			//���״̬����ʱ��
//			for(int i=0; i<list.size()-1; i++){
//				list.get(i).put("END_TIME", list.get(i+1).get("RPT_TIME"));
//			}
//			list.get(list.size()-1).put("END_TIME", endDateTime);
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ��ѯָ��Ա���Ŀͻ�����͹�˾����
	 * @param staffId
	 * @param isClient
	 * @param isCompany
	 * @return
	 * @throws Exception
	 */
	public List<WqMapRegion> findRegionsByStaffId(String staffId,boolean isClient,boolean isCompany)throws Exception {
		Set<String> regionIds = new HashSet<String>();

		WqCompanyInfo company = null;
		if(isCompany){
			company = DataCache.companyInfoMap.get(AppUtil.getUserInfo().getCompanyId());
			if(company != null && StringUtils.isNotBlank(company.getRegionIds())){
				regionIds.addAll(Arrays.asList(company.getRegionIds().split(",")));
			}
		}
		
		List<WqClientInfo> clientList = null;
		if(isClient){
			Set<String> clientIds = CacheClientStaffIdMap.getClientIds(staffId);
			Set<String> rids = CacheClientRegionIdMap.getRegionIds(clientIds);
			regionIds.addAll(rids);
			clientList = publicDAO.findBy("id", clientIds, WqClientInfo.class);
		}
		
		List<WqMapRegion> regions = new ArrayList<WqMapRegion>();
		if(!regionIds.isEmpty()){
			for(String regionId : regionIds){
				WqMapRegion region = DataCache.mapRegionMap.get(regionId);
				if(region != null){
					region = (WqMapRegion)BeanUtils.cloneBean(region);
					regions.add(region);
					if(isClient){
						for(WqClientInfo client:clientList){
							if(client.getRegionIds()!=null && client.getRegionIds().indexOf(regionId)!=-1){
								region.setName(client.getName()+"["+region.getName()+"]");
								break;
							}
						}
					}
					if(isCompany && company != null){
						if(StringUtils.isNotBlank(company.getRegionIds()) && company.getRegionIds().indexOf(regionId)!=-1){
							region.setName("��˾["+region.getName()+"]");
						}
					}
				}
			}
		}
		return regions;
	}
}
