package org.aigps.wqgps.module.customer.service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.aigps.wqgps.auth.AuthType;
import org.aigps.wqgps.auth.AuthUtil;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.DcChinaArea;
import org.aigps.wqgps.common.entity.WqClientInfo;
import org.aigps.wqgps.common.entity.WqDepInfo;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.entity.WqTravelPlan;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.entity.WqVisitPlan;
import org.aigps.wqgps.common.log.LogType;
import org.aigps.wqgps.common.log.LogUtil;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.RetrospectUtil;
import org.aigps.wqgps.common.util.SqlStringUtil;
import org.aigps.wqgps.module.region.service.RegionService;
import org.aigps.wqgps.timing.CacheClientRegionIdMap;
import org.aigps.wqgps.timing.CacheClientStaffIdMap;
import org.aigps.wqgps.timing.CacheRetrospect;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
			
			//����ϵͳ�����еĿͻ�������Ķ��չ�ϵ
			CacheClientRegionIdMap.update(client.getId(), client.getRegionIds());
			
			//�ȽϿͻ�ǰ���޸ĵ������Ƿ�һ�£������һ�£���Ҫ�������ݵ�׷��
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
	 * ����ͻ���Դ
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
	 * ɾ���ͻ���Դ
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public WqClientInfo deleteWqClientInfo(WqClientInfo entity) throws Exception{
		try {
			
			//ɾ���ͻ���Դ
			publicDAO.delete("id",entity.getId(),WqClientInfo.class);
			LogUtil.saveLog(LogType.DELETE_CLIENT, entity.getName());
			
			if(entity.getType().equals("0")){//����,ɾ���ӷ���ͷ����µĿͻ�
				List<WqClientInfo> list = publicDAO.findBy("parentId", entity.getId(), WqClientInfo.class);
				for(WqClientInfo ci:list){
					deleteWqClientInfo(ci);
				}
				return entity;
			}
			//ɾ����Ӧ������
			if(entity.getRegionIds() != null){
				String[] ids = entity.getRegionIds().split(",");
				for (String id : ids) {
					regionService.deleteMapRegion(id);
				}
			}
			//ɾ����Ӧ�Ŀͻ��ݷüƻ�
			publicDAO.delete("clientId", entity.getId(), WqVisitPlan.class);
			//ɾ���û�Ȩ��
			AuthUtil.deleteResListByOwnerId(AuthType.STAFF_CLIENT, entity.getId());
			//ɾ��Ա��Ȩ��
			AuthUtil.deleteResListByOwnerId(AuthType.USER_CLIENT, entity.getId());
			
			return entity;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	/**
	 * ��ѯ�ͻ���Դ
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
			for (String key : ids) {//ѭ������IDS
				WqMapRegion region = DataCache.mapRegionMap.get(key);
				if(region!=null){//��������ڻ�����
					if(wqClientInfo.getRegions() == null){//����ͻ���Դ�������嵥Ϊ��
						wqClientInfo.setRegions(new ArrayList<WqMapRegion>());
					}
					wqClientInfo.getRegions().add(region);
				}
			}
		}
		
	}
	/**
	 * ����Id��ѯ��ͼ�����б�
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
	/***�ͻ��ݷüƻ�*******************************************************/
	/**
	 * ����ݷüƻ�
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
				LogUtil.saveLog(LogType.CREATE_VISIT_PLAN, entity.getVisitDate().concat(staff.getCnName()).concat("�ݷ�").concat(tempStr));
				
				//���¼��İݷ�����С�ڽ���֮ǰ�ģ���Ҫ����׷��
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
	 * ����ɾ���ݷüƻ�
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
					LogUtil.saveLog(LogType.DELETE_VISIT_PLAN, entity.getVisitDate().concat(staff.getCnName()).concat("�ݷ�").concat(tempStr));
				} catch (Exception e) {}
				
				//ɾ���ݷüƻ�����Ӧ��Ҫɾ��׷�ݵĲ���
//				CacheRetrospect.deletePlan(entity.getId());
			}
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ��ѯ�ƻ�
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
			if(selectedNode!=null && selectedNode instanceof WqStaffInfo){//ѡ��Ա��������Ա��
				hql.append(" and staffId='").append(((WqStaffInfo)selectedNode).getId()).append("'");
			}else if(selectedNode!=null && selectedNode instanceof WqDepInfo){//ѡ�в��ţ����ò����µ�����Ա��
				List<WqStaffInfo> staffs = AuthUtil.getStaffList((WqDepInfo)selectedNode);
				List<String> tempList = new ArrayList<String>();
				for (WqStaffInfo staff : staffs) {
					tempList.add(staff.getId());
				}
				hql.append(" and");
				hql.append(SqlStringUtil.formatListToSQLIn("staffId", tempList, true));
			}else if(selectedNode==null && !user.getIsAdmin()){//ûѡ�н��,����ǰ�û���Ȩ�޵�����Ա��
				//��ǰ�û���Ȩ�޵Ĳ���
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
	 * ����Ա����ѯ��Ӧ�Ŀͻ���Դ
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
	/*******���üƻ�**************************************************************/
	/**
	 * ������üƻ�
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
							.concat("����ȥ")
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
	 * ����ɾ�����üƻ�
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
							.concat("����ȥ")
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
	 * ��ѯ�ƻ�
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
				//��ǰ�û���Ȩ�޵Ĳ���
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
