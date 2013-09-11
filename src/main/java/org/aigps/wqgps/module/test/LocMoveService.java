package org.sunleads.module.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.CmdStateCache;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.DcGpsHis;
import org.sunleads.common.entity.DcGpsReal;
import org.sunleads.common.entity.WqClientInfo;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqMapRegion;
import org.sunleads.common.entity.WqPlanLocate;
import org.sunleads.common.entity.WqPlanLocateDetail;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.enums.StaffStateEnum;
import org.sunleads.common.filter.GpsFilter;
import org.sunleads.common.log.LogType;
import org.sunleads.common.log.LogUtil;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.DateUtil;
import org.sunleads.module.location.dao.LocationDAO;
import org.sunleads.socket.CmdTypeEnum;
import org.sunleads.timing.CacheClientRegionIdMap;
import org.sunleads.timing.CacheClientStaffIdMap;

import edu.emory.mathcs.backport.java.util.Arrays;
/**
 * 定位服务
 * @author admin
 *
 */
@Component
@Transactional
@SuppressWarnings("unchecked")
public class LocMoveService {
	public final static Log log = LogFactory.getLog(LocMoveService.class);
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
//实时定位
	/**
	 * （实时定位）根据员工id列表获取实时定位信息
	 * @param staffIdList 员工id列表
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
						//如果公司不使用无效定位，把无效定位过滤掉，改成上一条有效定位
						if(company.getUseInvalidLoc()!=true && dcGpsReal.getIsValidGps()!=true){
							dcGpsReal = (DcGpsReal)BeanUtils.cloneBean(dcGpsReal);
							dcGpsReal.setIsValidGps(true);
							dcGpsReal.setLocDesc("[上次定位] "+dcGpsReal.getLocDesc());
						}
						String cmdState = CmdStateCache.getCmdStateCode(CmdTypeEnum.LCSNow.getValue(), staffId);
						dcGpsReal.setCmdState(cmdState);
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
	//设置定位状态
	private void setDcGpsRealState(Set<String> regionVisitSet,DcGpsReal dcGpsReal,String nowDateTime){
		String currReportTime = dcGpsReal.getReportTime();
		if(StringUtils.isNotBlank(currReportTime) && currReportTime.compareTo(nowDateTime)>=0){
			if(regionVisitSet.contains(dcGpsReal.getTmnAlias())){//在岗并在访问区域内的
				dcGpsReal.setState(StaffStateEnum.ON_WORK_AREA.getValue());
			}else{//在途中
				dcGpsReal.setState(StaffStateEnum.ON_PASSAGE.getValue());
			}
		}else{//脱岗
			dcGpsReal.setState(StaffStateEnum.OFF_SITE.getValue());
		}
	}

//历史定位	
	/**
	 * （历史定位）根据员工ID集合以及时间获取当天时间内最接近该时间的员工历史数据
	 * 处理逻辑：获取选中的这个日期时间零点到结束点之间所有记录，然后比较获取最接近该时间的记录
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
//计划定位	
	/**
	 * (计划定位)根据登录用户获取计划定位集合
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
	 * 保存或修改计划定位
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
	 * 删除计划定位
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
	 * (计划定位)根据计划定位ID获取详细定位信息
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

//历史轨迹
	/**
	 * （历史轨迹）根据员工ID获取历史轨迹
	 * @param staffId
	 * @param beginDateTime
	 * @param endDateTime
	 * @return
	 */
	public List<DcGpsHis> findDataGpsHisByCondition(String staffId,String beginDateTime,String endDateTime,String countType,int meter)throws Exception {
		try {
			List<DcGpsHis> list = locationDAO.findDataGpsHisByCondition(staffId, beginDateTime, endDateTime);
			
			for(Iterator<DcGpsHis> it=list.iterator(); it.hasNext(); ){
				DcGpsHis g = it.next();
				if(g.getIsValidGps() == false){
					it.remove();
				}
			}
			if(!list.isEmpty()){
				if(countType.equals("r1")){
					GpsFilter.filterBy1Or2(list,meter,false);
				}else if(countType.equals("r2")){
					GpsFilter.filterBy1Or2(list,meter,true);
				}else if(countType.equals("r3")){
					GpsFilter.filterBy3(list,meter);
				}else if(countType.equals("r4")){
					GpsFilter.filterBy4(list,meter);
				}else if(countType.equals("r5")){
					GpsFilter.filterBy5(list,meter);
				}
			}
			return list;
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}
	
	/**
	 * 查询指定员工的客户区域和公司区域
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
							region.setName("公司["+region.getName()+"]");
						}
					}
				}
			}
		}
		return regions;
	}
}
