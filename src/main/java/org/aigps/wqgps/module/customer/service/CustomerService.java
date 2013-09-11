package org.sunleads.module.customer.service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.auth.AuthType;
import org.sunleads.auth.AuthUtil;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.DcChinaArea;
import org.sunleads.common.entity.WqClientInfo;
import org.sunleads.common.entity.WqDepInfo;
import org.sunleads.common.entity.WqMapRegion;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.entity.WqTravelPlan;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.entity.WqVisitPlan;
import org.sunleads.common.log.LogType;
import org.sunleads.common.log.LogUtil;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.RetrospectUtil;
import org.sunleads.common.util.SqlStringUtil;
import org.sunleads.module.region.service.RegionService;
import org.sunleads.timing.CacheClientRegionIdMap;
import org.sunleads.timing.CacheClientStaffIdMap;
import org.sunleads.timing.CacheRetrospect;

@Component
@Transactional
@SuppressWarnings({"unchecked"})
public class CustomerService {
	public final static Log log = LogFactory.getLog(CustomerService.class);
	
	private PublicDAO publicDAO;
	private RegionService regionService;
	
	@Autowired
	public void setRegionService(RegionService regionService) {
		this.regionService = regionService;
	}
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	
	public WqClientInfo updateClientRegion(WqClientInfo client,List<WqMapRegion> mapRegionList) throws Exception{
		try{
			String oldRegionIds = client.getRegionIds();
			if(mapRegionList != null && !mapRegionList.isEmpty()){
				StringBuilder ids = new StringBuilder();
				for (WqMapRegion wqMapRegion : mapRegionList) {
					regionService.saveMapRegion(wqMapRegion);
					ids.append(wqMapRegion.getId()).append(",");
				}
				if(ids.lastIndexOf(",") != -1){
					ids.deleteCharAt(ids.lastIndexOf(","));
				}
				client.setRegionIds(ids.toString());
			}else{
				client.setRegionIds(null);
			}
			if(StringUtils.isNotBlank(oldRegionIds)){
				String[] rids = oldRegionIds.split(",");
				for(int i=0; i<rids.length; i++){
					if(StringUtils.isBlank(client.getRegionIds()) || client.getRegionIds().indexOf(rids[i]) == -1){
						regionService.deleteMapRegion(rids[i]);
					}
				}
			}
			publicDAO.save(client);
			List<WqMapRegion> oldRegions = client.getRegions();
			client.setRegions(mapRegionList);
			
			//更新系统缓存中的客户和区域的对照关系
			CacheClientRegionIdMap.update(client.getId(), client.getRegionIds());
			
			//比较客户前后修改的区域是否一致，如果不一致，就要进行数据的追溯
			if(RetrospectUtil.isDeffRegions(oldRegions,mapRegionList)){
				CacheRetrospect.updateClient(client);
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
		return client;
	}
	
	/**
	 * 保存客户资源
	 * @param entity
	 * @param mapRegionList  ,List<WqMapRegion> mapRegionList
	 * @return
	 * @throws Exception
	 */
	public WqClientInfo saveWqClientInfo(WqClientInfo entity) throws Exception{
		try {
			if(entity.getId() == null){
				entity.setCreateTime(new Date());
				entity.setIsEnable("1");
				publicDAO.save(entity);
				LogUtil.saveLog(LogType.CREATE_CLIENT, entity.getName());
			}else{
				publicDAO.save(entity);
				LogUtil.saveLog(LogType.UPDATE_CLIENT, entity.getName());
			}
			return entity;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	/**
	 * 删除客户资源
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public WqClientInfo deleteWqClientInfo(WqClientInfo entity) throws Exception{
		try {
			
			//删除客户资源
			publicDAO.delete("id",entity.getId(),WqClientInfo.class);
			LogUtil.saveLog(LogType.DELETE_CLIENT, entity.getName());
			
			if(entity.getType().equals("0")){//分类,删除子分类和分类下的客户
				List<WqClientInfo> list = publicDAO.findBy("parentId", entity.getId(), WqClientInfo.class);
				for(WqClientInfo ci:list){
					deleteWqClientInfo(ci);
				}
				return entity;
			}
			//删除对应的区域
			if(entity.getRegionIds() != null){
				String[] ids = entity.getRegionIds().split(",");
				for (String id : ids) {
					regionService.deleteMapRegion(id);
				}
			}
			//删除对应的客户拜访计划
			publicDAO.delete("clientId", entity.getId(), WqVisitPlan.class);
			//删除用户权限
			AuthUtil.deleteResListByOwnerId(AuthType.STAFF_CLIENT, entity.getId());
			//删除员工权限
			AuthUtil.deleteResListByOwnerId(AuthType.USER_CLIENT, entity.getId());
			
			return entity;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	/**
	 * 查询客户资源
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public List<WqClientInfo> findWqClientInfo() throws Exception{
		WqUserInfo user = AppUtil.getUserInfo();
		try {
			String hql = "FROM WqClientInfo where companyId=? and isEnable=? order by name";
			List<WqClientInfo> list = publicDAO.find(hql,user.getCompanyId(),"1");
			
			for (WqClientInfo wqClientInfo : list) {
				setRegionData(wqClientInfo);
			}
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	public Boolean dealImportClient(byte[] byteArray,WqClientInfo parent){
		try {
			Workbook wb = Workbook.getWorkbook(new ByteArrayInputStream(byteArray));
			Sheet sheet = wb.getSheet(0);
			
			WqUserInfo user = AppUtil.getUserInfo();
			for(int i=3; i<997; i++){
				Cell[] cells=sheet.getRow(i);
				if(StringUtils.isBlank(cells[1].getContents())){
					break;
				}
				WqClientInfo client = new WqClientInfo();
				client.setName(cells[1].getContents());
				client.setLinkname(cells[2].getContents());
				client.setContactNumber(cells[3].getContents());
				client.setKind(cells[4].getContents());
				client.setVisitLong(new BigDecimal(cells[5].getContents()));
				client.setRemark(cells[6].getContents());
				client.setCompanyId(user.getCompanyId());
				client.setCreater(user.getCnName());
				client.setCreateTime(new Date());
				client.setIsEnable("1");
				client.setParentId(parent.getId());
				client.setType("1");
				
				publicDAO.save(client);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
	
	private void setRegionData(WqClientInfo wqClientInfo) {
		if(StringUtils.isNotBlank(wqClientInfo.getRegionIds())){
			String[] ids = wqClientInfo.getRegionIds().split(",");
			for (String key : ids) {//循环区域IDS
				WqMapRegion region = DataCache.mapRegionMap.get(key);
				if(region!=null){//如果区域在缓存中
					if(wqClientInfo.getRegions() == null){//如果客户资源的区域清单为空
						wqClientInfo.setRegions(new ArrayList<WqMapRegion>());
					}
					wqClientInfo.getRegions().add(region);
				}
			}
		}
		
	}
	/**
	 * 根据Id查询地图区域列表
	 * @param list
	 * @return
	 */
	public List<WqMapRegion> findMapRegoinByIds(List<String> list) throws Exception{
		try {
			return regionService.findMapRegoinBy(list);
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}
	/***客户拜访计划*******************************************************/
	/**
	 * 保存拜访计划
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public List<WqVisitPlan> saveWqVisitPlan(List<WqVisitPlan> entitys) throws Exception{
		try {
//			String nowDate = DateUtil.getNowDate();
			for (WqVisitPlan entity : entitys) {
				if(entity.getId() == null){
					entity.setCreateTime(new Date());
				}
				publicDAO.save(entity);
				
				String tempStr = "";
				if(entity.getClientInfo()!=null){
					tempStr = entity.getClientInfo().getName();
				}
				WqStaffInfo staff =	DataCache.staffMap.get(entity.getStaffId());
				LogUtil.saveLog(LogType.CREATE_VISIT_PLAN, entity.getVisitDate().concat(staff.getCnName()).concat("拜访").concat(tempStr));
				
				//如果录入的拜访日期小于今天之前的，需要重新追溯
//				if(nowDate.compareTo(entity.getVisitDate())>=0){
//					CacheRetrospect.addPlan(entity.getId(), entity.getStaffId(), entity.getClientInfo(), entity.getVisitDate()+" 00:00:00",  entity.getVisitDate()+" 23:59:59");
//				}
			}
			return entitys;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 批量删除拜访计划
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public List<WqVisitPlan> batchDeleteWqVisitPlan(List<WqVisitPlan> list) throws Exception{
		try {
			for (WqVisitPlan entity : list) {
				publicDAO.delete("id", entity.getId(), WqVisitPlan.class);
				
				try {
					String tempStr = "";
					if(entity.getClientInfo()!=null){
						tempStr = entity.getClientInfo().getName();
					}
					WqStaffInfo staff =	DataCache.staffMap.get(entity.getStaffId());
					LogUtil.saveLog(LogType.DELETE_VISIT_PLAN, entity.getVisitDate().concat(staff.getCnName()).concat("拜访").concat(tempStr));
				} catch (Exception e) {}
				
				//删除拜访计划，相应的要删除追溯的部分
//				CacheRetrospect.deletePlan(entity.getId());
			}
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 查询计划
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<WqVisitPlan> findWqVisitPlan(String startDate,String endDate,Object selectedNode) throws Exception{
		try {
			List<WqVisitPlan> list = new ArrayList<WqVisitPlan>();
			StringBuilder hql = new StringBuilder("FROM WqVisitPlan where 1=1 and companyId=?");
			if(startDate !=null && !startDate.equals("")){
				hql.append(" and visitDate >= '").append(startDate).append("'");
			}
			if(endDate !=null && !endDate.equals("")){
				hql.append(" and visitDate <= '").append(endDate).append("'");
			}
			
			WqUserInfo user = AppUtil.getUserInfo();
			if(selectedNode!=null && selectedNode instanceof WqStaffInfo){//选中员工，看该员工
				hql.append(" and staffId='").append(((WqStaffInfo)selectedNode).getId()).append("'");
			}else if(selectedNode!=null && selectedNode instanceof WqDepInfo){//选中部门，看该部门下的所有员工
				List<WqStaffInfo> staffs = AuthUtil.getStaffList((WqDepInfo)selectedNode);
				List<String> tempList = new ArrayList<String>();
				for (WqStaffInfo staff : staffs) {
					tempList.add(staff.getId());
				}
				hql.append(" and");
				hql.append(SqlStringUtil.formatListToSQLIn("staffId", tempList, true));
			}else if(selectedNode==null && !user.getIsAdmin()){//没选中结点,看当前用户有权限的所有员工
				//当前用户有权限的部门
				List<WqDepInfo> deps = AuthUtil.getResList(AuthType.USER_DEP);
				List<WqStaffInfo> staffs = AuthUtil.getStaffList(deps);
				List<String> tempList = new ArrayList<String>();
				for (WqStaffInfo staff : staffs) {
					tempList.add(staff.getId());
				}
				hql.append(" and");
				hql.append(SqlStringUtil.formatListToSQLIn("staffId", tempList, true));
			}
			hql.append(" order by visitDate");
			
			list = publicDAO.find(hql.toString(),user.getCompanyId());
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 根据员工查询对应的客户资源
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public List<WqClientInfo> findWqClientInfoByStaff(WqStaffInfo entity) throws Exception{
		try {
			Set<String> clientIds = CacheClientStaffIdMap.getClientIds(entity.getId());
			List<WqClientInfo> list = publicDAO.findBy("id", clientIds, WqClientInfo.class);
			for (WqClientInfo wqClientInfo : list) {
				setRegionData(wqClientInfo);
			}
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	/*******差旅计划**************************************************************/
	/**
	 * 保存差旅计划
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public List<WqTravelPlan> saveWqTravelPlan(List<WqTravelPlan> entitys) throws Exception{
		try {
			for (WqTravelPlan entity : entitys) {
				if(entity.getId() == null){
					entity.setCreateTime(new Date());
				}
				publicDAO.save(entity);
				setDcChinaAreaModel(entity);

				try {
					WqStaffInfo staff =	DataCache.staffMap.get(entity.getStaffId());
					String place = "";
					for (DcChinaArea temp : entity.getDcChinaArea()) {
						place+=temp.getProv().concat(temp.getCity()).concat(temp.getTown()).concat(",");
					}
					place = place.substring(0,place.lastIndexOf(","));
					LogUtil.saveLog(LogType.CREATE_TRAVEL_PLAN, staff.getCnName()
							.concat(entity.getStartTime())
							.concat("-")
							.concat(entity.getEndTime())
							.concat("出差去")
							.concat(place));
				} catch (Exception e) {}
			}
			return entitys;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 批量删除差旅计划
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public List<WqTravelPlan> batchDeleteWqTravelPlan(List<WqTravelPlan> list) throws Exception{
		try {
			for (WqTravelPlan entity : list) {
				publicDAO.delete("id", entity.getId(), WqTravelPlan.class);
			
				try {
					WqStaffInfo staff =	DataCache.staffMap.get(entity.getStaffId());
					String place = "";
					for (DcChinaArea temp : entity.getDcChinaArea()) {
						place+=temp.getProv().concat(temp.getCity()).concat(temp.getTown()).concat(",");
					}
					place = place.substring(0,place.lastIndexOf(","));
					LogUtil.saveLog(LogType.DELETE_TRAVEL_PLAN, staff.getCnName()
							.concat(entity.getStartTime())
							.concat("-")
							.concat(entity.getEndTime())
							.concat("出差去")
							.concat(place));
				} catch (Exception e) {}
			}
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 查询计划
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<WqTravelPlan> findWqTravelPlan(String travelReason,String startDate,String endDate,List<String> staffIds) throws Exception{
		try {
			List<WqTravelPlan> list = new ArrayList<WqTravelPlan>();
			StringBuilder hql = new StringBuilder("FROM WqTravelPlan where 1=1 and companyId=?");
			if(travelReason!=null && !travelReason.equals("")){
				hql.append(" and reason like '%"+travelReason+"%'");
			}
			if(startDate !=null && !startDate.equals("")){
				hql.append(" and startTime >= '").append(startDate).append("'");
			}
			if(endDate !=null && !endDate.equals("")){
				hql.append(" and startTime <= '").append(endDate).append("'");
			}

			WqUserInfo user = AppUtil.getUserInfo();
			if(staffIds!=null && !staffIds.isEmpty()){
				hql.append(" and ").append(SqlStringUtil.formatListToSQLIn("staffId", staffIds, true));
			}else if(!user.getIsAdmin()){
				//当前用户有权限的部门
				List<WqDepInfo> deps = AuthUtil.getResList(AuthType.USER_DEP);
				List<WqStaffInfo> staffs = AuthUtil.getStaffList(deps);
				List<String> tempList = new ArrayList<String>();
				for (WqStaffInfo staff : staffs) {
					tempList.add(staff.getId());
				}
				hql.append(" and ").append(SqlStringUtil.formatListToSQLIn("staffId", tempList, true));
			}
			hql.append(" order by staffId,startTime");
			list = publicDAO.find(hql.toString(),user.getCompanyId());
			for (WqTravelPlan wqTravelPlan : list) {
				setDcChinaAreaModel(wqTravelPlan);
			}
			
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	private void setDcChinaAreaModel(WqTravelPlan entity){
		entity.setDcChinaArea(null);
		String[] array = entity.getAreaIds().split(",");
		for (String id : array) {
			if(entity.getDcChinaArea() == null){
				entity.setDcChinaArea(new ArrayList<DcChinaArea>());
			}
			DcChinaArea dcChinaArea = DataCache.dcChinaAreaMap.get(id);
			if(dcChinaArea != null){
				entity.getDcChinaArea().add(dcChinaArea);
			}
		}
	}
}
