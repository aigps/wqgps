package org.aigps.wqgps.module.sysmanager.service;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.aigps.wqgps.auth.AuthType;
import org.aigps.wqgps.auth.AuthUtil;
import org.aigps.wqgps.common.cache.CmdStateCache;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.cache.SessionData;
import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.WqCompanyInfo;
import org.aigps.wqgps.common.entity.WqDepInfo;
import org.aigps.wqgps.common.entity.WqLogInfo;
import org.aigps.wqgps.common.entity.WqLogType;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.common.entity.WqMenuInfo;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.log.LogType;
import org.aigps.wqgps.common.log.LogUtil;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.common.util.RetrospectUtil;
import org.aigps.wqgps.module.region.service.RegionService;
import org.aigps.wqgps.module.report.dao.RegionVisitDAO;
import org.aigps.wqgps.socket.CmdUtil;
import org.aigps.wqgps.timing.CacheClientStaffIdMap;
import org.aigps.wqgps.timing.CacheDepMap;
import org.aigps.wqgps.timing.CacheRetrospect;
import org.aigps.wqgps.timing.CacheStaffMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@SuppressWarnings({"unchecked"})
public class SysManagerService {
	public final static Log log = LogFactory.getLog(SysManagerService.class);
	
	private static Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$"); 
	
	private PublicDAO publicDAO;
	private RegionService regionService;
	private RegionVisitDAO regionVisitDAO;
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	@Autowired
	public void setRegionService(RegionService regionService) {
		this.regionService = regionService;
	}
	@Autowired
	public void setRegionVisitDAO(RegionVisitDAO regionVisitDAO) {
		this.regionVisitDAO = regionVisitDAO;
	}

	/**
	 * ��ѯ��ǰ�û����¼������û�
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<WqUserInfo> findUserById(String userId,String userName,String cnName) throws Exception{
		try {
			return publicDAO.findBy("parentId", "userName","cnName",userId,userName, cnName, WqUserInfo.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * �������޸��û�
	 * @param entiry
	 */
	public WqUserInfo addOrUpdateUser(WqUserInfo subUser) throws Exception{
		try {
			WqUserInfo user = AppUtil.getUserInfo();
			if(subUser.getId()==null){
				LogUtil.saveLog(LogType.CREATE_USER, "���û�:"+user.getCnName()+"���������û�:"+subUser.getCnName());
			}else{
				LogUtil.saveLog(LogType.UPDATE_USER, "���û�:"+user.getCnName()+"�޸������û�:"+subUser.getCnName());
			}
			publicDAO.save(subUser);
			return subUser;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ɾ���û�
	 * @param entiry
	 */
	public void delUser(WqUserInfo subUser) throws Exception{
		try {
			publicDAO.delete(subUser);
			
			//ɾ���û���Ȩ��
			AuthUtil.deleteResListByOwnerId(AuthType.USER_CLIENT, subUser.getId());
			AuthUtil.deleteResListByOwnerId(AuthType.USER_DEP, subUser.getId());
			AuthUtil.deleteResListByOwnerId(AuthType.USER_MENU, subUser.getId());
			
			WqUserInfo user = AppUtil.getUserInfo();
			LogUtil.saveLog(LogType.CREATE_USER, "���û�:"+user.getCnName()+"ɾ�������û�:"+subUser.getCnName());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	/**
	 * �õ�ָ���û��Ĳ˵�Ȩ��
	 * @param userId
	 * @return
	 */
	public List<WqMenuInfo> userMenuList(String userId) throws Exception{
		try {
			return AuthUtil.getResListByOwnerId(AuthType.USER_MENU,userId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * �����û����õĲ˵�
	 * @param menuList
	 * @param userId
	 */
	public void saveUserMenu(List<String> menuList,WqUserInfo subUser) throws Exception{
		try {
			AuthUtil.saveResListByOwnerId(AuthType.USER_MENU, menuList, subUser.getId());

			WqUserInfo user = AppUtil.getUserInfo();
			LogUtil.saveLog(LogType.USER_MENU_RIGHT, "���û�:"+user.getCnName()+"�޸������û�:"+subUser.getCnName()+"�Ĳ˵�Ȩ��");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ����Id��ѯ��ͼ�����б�
	 * @param list
	 * @return
	 */
	public List<WqMapRegion> findMapRegoinBy(List<String> list) throws Exception{
		try {
			return regionService.findMapRegoinBy(list);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ���湫˾��Ϣ
	 * @param entiry
	 * @return
	 */
	public WqCompanyInfo saveCompnayInfo(WqCompanyInfo company,List<WqMapRegion> newRegionList,List<WqMapRegion> oldRegionList) throws Exception{
		try {
			StringBuffer newRegionIds = new StringBuffer();
			if(newRegionList!=null && !newRegionList.isEmpty()){
				for(WqMapRegion region : newRegionList){
					regionService.saveMapRegion(region);
					newRegionIds.append(region.getId()).append(",");
				}
				newRegionIds = newRegionIds.deleteCharAt(newRegionIds.length()-1);
			}
			//ɾ���ɵ�����
			String oldRegionIds = company.getRegionIds();
			if(StringUtils.isNotBlank(oldRegionIds)){
				String[] rids = oldRegionIds.split(",");
				for(int i=0; i<rids.length; i++){
					if(newRegionIds.indexOf(rids[i]) == -1){
						regionService.deleteMapRegion(rids[i]);
					}
				}
			}
			
			company.setRegionIds(newRegionIds.toString());
			publicDAO.save(company);
			
			SessionData sessionData = AppUtil.getSessionData();
			WqUserInfo user = sessionData.getUserInfo();
			LogUtil.saveLog(LogType.UPDATE_COMPANY, "�û�:"+user.getCnName()+"�޸��˹�˾��Ϣ");
			DataCache.companyInfoMap.put(company.getId(), company);
			
			//�Ƚ�ǰ���޸ĵ������Ƿ�һ�£������һ�£���Ҫ�������ݵ�׷��
			if(RetrospectUtil.isDeffRegions(oldRegionList,newRegionList)){
				CacheRetrospect.updateCompany(company);
			}
			
			return company;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ���沿����Ϣ
	 * @param entiry
	 * @return
	 */
	public WqDepInfo saveDepInfo(WqDepInfo dep) throws Exception{
		try {
			boolean isAdd=false;
			if(dep.getId()==null){
				isAdd=true;
			}
			publicDAO.save(dep);
			if(isAdd){//��������
				WqUserInfo user = AppUtil.getUserInfo();
				if(!user.getIsAdmin()){//�����ǰ�û����ǹ���Ա��������Ȩ�޸�����ǰ�û�
					List<String> depList = new ArrayList<String>();
					depList.add(dep.getId());
					List<String> userIds = new ArrayList<String>();
					userIds.add(user.getId());
					AuthUtil.addResList(AuthType.USER_DEP,userIds,depList);
					
//					AuthUtil.saveResListByOwnerId(AuthType.USER_DEP, depList, user.getId());
				}
				CacheDepMap.addDep(dep);
				LogUtil.saveLog(LogType.CREATE_USER, "�û�:"+AppUtil.getUserInfo().getCnName()+"����������Ϣ"+dep.getName());
			}else{//�޸Ĳ���
				CacheDepMap.updateDep(dep);
				LogUtil.saveLog(LogType.CREATE_USER, "�û�:"+AppUtil.getUserInfo().getCnName()+"�޸Ĳ�����Ϣ"+dep.getName());
			}
			return dep;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ɾ������
	 * @param depId
	 */
	public void stopDepUse(WqDepInfo dep) throws Exception{
		try {
			//ɾ������
			dep.setIsEnable(false);
			publicDAO.save(dep);
			CacheDepMap.deleteDep(dep);

			//ɾ����������������û���Ȩ��
			AuthUtil.deleteResListByResId(AuthType.USER_DEP,dep.getId());
			
			//ɾ�������µ�����Ա��
			List<WqStaffInfo> staffs = DataCache.depStaffMap.get(dep.getId());
			if(staffs != null){
				stopStaffUse(staffs);
			}
			
			LogUtil.saveLog(LogType.DELETE_DEP, "�û�:"+AppUtil.getUserInfo().getCnName()+"ɾ���˲���:"+dep.getName());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	/**
	 * ���ݲ���ID����Ա��
	 * @param depId
	 * @return
	 */
	public List<WqStaffInfo> findStaffBy(List<String> depIdList) throws Exception{
		List<WqStaffInfo> staffList = new ArrayList<WqStaffInfo>();
		for(String depId : depIdList){
			List<WqStaffInfo> staffs = DataCache.depStaffMap.get(depId);
			if(staffs != null){
				staffList.addAll(staffs);
			}
		}
		return staffList;
	}
	
	/**
	 * ����Ա��
	 * @param entiry
	 * @return
	 */
	public WqStaffInfo saveStaff(WqStaffInfo staff) throws Exception{
		boolean isAdd = staff.getId()==null;
		String staffId = DataCache.phoneStaffIdMap.get(staff.getMobileNumber());
		if(isAdd){//����Ա��
			if(StringUtils.isNotBlank(staffId)){//�ֻ����ѱ�����Ա��ʹ��
				return null;
			}
		}else{//�޸�Ա��
			if(StringUtils.isNotBlank(staffId) && !staff.getId().equals(staffId)){////�ֻ����ѱ�����Ա��ʹ��
				return null;
			}
		}
		try {
			publicDAO.save(staff);
			
			if(isAdd){
				CacheStaffMap.addStaff(staff);
				LogUtil.saveLog(LogType.CREATE_STAFF, "����Ա��:"+staff.getCnName());
			}else{
				CacheStaffMap.updateStaff(staff);
				LogUtil.saveLog(LogType.UPDATE_STAFF, "�޸�Ա��:"+staff.getCnName());
			}
			return staff;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ɾ��Ա��
	 */
	public List<WqStaffInfo> stopStaffUse(List<WqStaffInfo> staffList) throws Exception{
		try {
			for(WqStaffInfo staff:staffList){	
				staff.setIsEnable(false);
				publicDAO.save(staff);
				CacheStaffMap.deleteStaff(staff);
				LogUtil.saveLog(LogType.DELETE_STAFF, "�û�:"+AppUtil.getUserInfo().getCnName()+"ɾ����Ա��:"+staff.getCnName());
				
				//ɾ��Ա��Ȩ��
				AuthUtil.deleteResListByOwnerId(AuthType.STAFF_CLIENT, staff.getId());
				AuthUtil.deleteResListByOwnerId(AuthType.STAFF_RULE, staff.getId());
				
				//ɾ��Ա������Ҫ��Ա��׷�ݵ����ݽ���ȡ��
				CacheRetrospect.deleteStaff(staff.getId());
			}
			return staffList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ����Ա�����Ϸ�����������
	 * @param entiry
	 */
	public List<String> activateStaffState(List<String> staffIdList,String fixModel) throws Exception{
		StringBuilder names = new StringBuilder();
		try {
			for(String staffId:staffIdList){
				WqStaffInfo staff = DataCache.staffMap.get(staffId);
				staff.setActivateState("1");
				staff.setFixModel(fixModel);
				publicDAO.save(staff);
				
				if(!"10".equals(fixModel)){
					//���ͼ�����漤�����
					String startWorkTime = staff.getStartWorkTime().replaceAll(":", "").concat("00");
					String endWorkTime = staff.getEndWorkTime().replaceAll(":", "").concat("59");
					
					CmdUtil.sendActiveCmd(staff.getMobileType(),staff.getMobileNumber(),fixModel,staff.getGpsInterval(),startWorkTime,endWorkTime,staff.getWorkWeekDays());
					CmdStateCache.activeTmnNumMap.put(staff.getMobileNumber(),new String[]{"1",DateUtil.getCurDate()});
				}else{//���Ŵֶ�λ
					staff.setActivateState("2");
				}
				names.append(staff.getCnName()).append(" ");
			}
			LogUtil.saveLog(LogType.UPDATE_STAFF, "����Ա��:"+names);
			return staffIdList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ʧ��Ա��
	 * @param entiryList
	 * @return
	 */
	public List<String> cancelActivateStaffState(List<String> staffIdList) throws Exception{
		StringBuilder names = new StringBuilder();
		try {
			for(String staffId:staffIdList){
				WqStaffInfo staff = DataCache.staffMap.get(staffId);
				staff.setActivateState("4");//ʧ����
				publicDAO.save(staff);
				
				//���ͼ�����漤�����
				CmdUtil.sendCancelActiveCmd(staff.getMobileType(),staff.getMobileNumber());
				CmdStateCache.cancelActiveTmnNumMap.put(staff.getMobileNumber(),new String[]{"1",DateUtil.getCurDate()});
				
				names.append(staff.getCnName()).append(" ");
			}
			LogUtil.saveLog(LogType.UPDATE_STAFF, "ʧ��Ա��:"+names);
			return staffIdList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ����Ա��ID�ɹ���ʧ�ܼ���
	 * @param tmnCode
	 * @param stateNum 0:δ����;1:������;2:�Ѽ���;3:����ʧ��;4:ʧ����;5:��ʧ��;6:ʧ��ʧ��
	 */
	public void updateActiveState(String phone,String actState) throws Exception{
		try {
			String staffId = DataCache.phoneStaffIdMap.get(phone);
			if(StringUtils.isNotBlank(staffId)){
				WqStaffInfo staff = DataCache.staffMap.get(staffId);
				
				log.info("��Ա��:"+staff.getCnName()+",״̬��:"+staff.getActivateState()+"��ɣ�"+actState);
				
				staff.setActivateState(actState);
				publicDAO.save(staff);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * �õ�����Ա���Ŀͻ�
	 * @param entiry
	 * @return
	 */
	public Set<String> findStaffRoleClient(WqStaffInfo staff) throws Exception{
		return CacheClientStaffIdMap.getClientIds(staff.getId());
	}
	
	/**
	 * ����Ա����Ӧ�Ŀͻ�
	 * @param entiry
	 * @param clientList
	 */
	public void saveStaffRoleClient(List<WqStaffInfo> staffList,List<String> clientIdList,String type) throws Exception{
		try {
			List<String> staffIdList = new ArrayList<String>();
			for(WqStaffInfo staff : staffList){
				staffIdList.add(staff.getId());
			}
			if("set".equals(type)){//ȫ�·���ͻ�
				for(String staffId : staffIdList){
					AuthUtil.saveResListByOwnerId(AuthType.STAFF_CLIENT,clientIdList,staffId);
				}
			}else if("add".equals(type)){//ֻ���ָ���ͻ���ԭ�пͻ�����
				AuthUtil.addResList(AuthType.STAFF_CLIENT, staffIdList, clientIdList);
			}else if("remove".equals(type)){//ֻɾ��ָ���ͻ���ԭ�пͻ�����
				AuthUtil.deleteResList(AuthType.STAFF_CLIENT, staffIdList, clientIdList);
			}
			
			for(WqStaffInfo staff : staffList){
				LogUtil.saveLog(LogType.STAFF_CLIENT_RIGHT, "�޸�Ա��:"+staff.getCnName()+"�����Ŀͻ�");
			}
			
			//�޸ĺ�Ա���Ϳͻ��Ķ��չ�ϵ��Ҫ�������ݵ�׷��
			//ͬʱ����ɾ������ЩԱ������Щ����
			List<String[]> deleteStaffRegionList = RetrospectUtil.updateStaffClient(staffIdList, clientIdList, type);
			
			//ɾ��֮ǰԱ��������Ľ�����¼
			for(String[] deleteStaffRegion : deleteStaffRegionList){
				regionVisitDAO.deleteRegionVisit(deleteStaffRegion[0], deleteStaffRegion[1]);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ��ѯ������־������
	 * @return
	 */
	public List<WqLogType> findLogType() throws Exception{
		List<WqLogType> logType = new ArrayList<WqLogType>(DataCache.logTypeMap.values());
		WqLogType allType = new WqLogType();
		allType.setId(null);
		allType.setName("��������");
		allType.setLogType("ALL_TYPE");
		logType.add(0, allType);
		return logType;
	}
	
	/**
	 * ��ѯ��־��Ϣ
	 * @param startTime
	 * @param endTime
	 * @param userName
	 * @return
	 */
	public List<WqLogInfo> findLogBy(String logTypeId,String startTime,String endTime,String userName,String companyId) throws Exception{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<WqLogInfo> modelList = new ArrayList<WqLogInfo>();
		StringBuilder sql = new StringBuilder("SELECT * FROM WQ_LOG_INFO ");
		sql.append("where to_char(create_time,'yyyy-mm-dd hh24:mi:ss') between '").append(startTime).append(" 00:00:00' and '").append(endTime).append(" 23:59:59' ");
		if(StringUtils.isNotBlank(userName)){
			sql.append("and creater like '%").append(userName).append("%' ");
		}
		if(StringUtils.isNotBlank(logTypeId) && !"null".equals(logTypeId)){
			sql.append("and type_id='").append(logTypeId).append("' ");
		}
		if(StringUtils.isNotBlank(companyId) && !"null".equals(companyId)){
			sql.append("and company_Id='").append(companyId).append("' ");
		}
		sql.append("order by create_time desc");
		
		Map<String,String> typeMap = new HashMap<String,String>();
		for(WqLogType type : DataCache.logTypeMap.values()){
			typeMap.put(type.getId(), type.getName());
		}
		try {
			List<Map<String,Object>> list = jdbcTemplate.queryForList(sql.toString());
			for(Map<String,Object> map: list){
				WqLogInfo model=new WqLogInfo();
				model.setCompanyId(map.get("COMPANY_ID")==null?"":map.get("COMPANY_ID").toString());
				model.setContent(map.get("CONTENT")==null?"":map.get("CONTENT").toString());
				model.setCreater(map.get("CREATER")==null?"":map.get("CREATER").toString());
				model.setCreateTime(map.get("CREATE_TIME")==null?null:df.parse(map.get("CREATE_TIME").toString()));
				model.setTypeId(map.get("TYPE_ID").toString());
				model.setStandby1(typeMap.get(model.getTypeId()));
				model.setStandby2(map.get("STANDBY2")==null?"":map.get("STANDBY2").toString());
				model.setStandby3(map.get("STANDBY3")==null?"":map.get("STANDBY3").toString());
				modelList.add(model);
			}
			return modelList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ɾ����־��Ϣ
	 * @param entiryList
	 */
	public void delLogBy(String logTypeId,String startTime,String endTime,String userName,String companyId) throws Exception{
		WqUserInfo user = AppUtil.getUserInfo();
		StringBuilder sql = new StringBuilder();
		sql.append("delete WQ_LOG_INFO where to_char(create_time,'yyyy-mm-dd hh24:mi:ss') between '"+startTime+" 00:00:00' and '"+endTime+" 23:59:59"+"'  and creater like '%"+userName+"%' and company_id='"+companyId+"'");
		if(StringUtils.isNotBlank(logTypeId) && !"null".equals(logTypeId)){
			sql.append(" and type_id='").append(logTypeId).append("' ");
		}
		try {
			jdbcTemplate.execute(sql.toString());
			LogUtil.saveLog(LogType.DELETE_LOG, "�û�:"+user.getCnName()+"ɾ���˴�:"+startTime+"��"+endTime+",��־����IDΪ"+logTypeId+"����־��Ϣ");
		} catch (DataAccessException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * �����û���Ӧ�Ĳ���
	 * @param menuList
	 * @param userId
	 */
	public void saveUserDep(List<String> depList,WqUserInfo subUser) throws Exception{
		try {
			AuthUtil.deleteResListByOwnerId(AuthType.USER_DEP, subUser.getId());
			AuthUtil.saveResListByOwnerId(AuthType.USER_DEP, depList, subUser.getId());

			WqUserInfo user = AppUtil.getUserInfo();
			LogUtil.saveLog(LogType.USER_DEP_RIGHT, "���û�:"+user.getCnName()+"�޸������û�:"+subUser.getCnName()+"�Ĳ���Ȩ��");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * ��ѯָ���û��Ĳ���Ȩ��
	 * @param entiry
	 * @return
	 */
	public List<WqDepInfo> findDepBy(WqUserInfo subUser) throws Exception{
		try {
			return AuthUtil.getResListByOwnerId(AuthType.USER_DEP, subUser.getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public List<WqStaffInfo> dealImportStaff(byte[] byteArray,WqDepInfo parentDep,String username) throws Exception{
		List<WqStaffInfo> staffList=new ArrayList<WqStaffInfo>();
		try {
			Workbook wb = Workbook.getWorkbook(new ByteArrayInputStream(byteArray));
			Sheet sheet=wb.getSheet(0);

			for(int tempIndex=3;tempIndex<997;tempIndex++){
				Cell[] cells=sheet.getRow(tempIndex);
				WqStaffInfo staff=new WqStaffInfo();
				staff.setStaffNo(cells[1].getContents());
				staff.setCnName(cells[2].getContents());
				staff.setMobileNumber(cells[3].getContents());
				staff.setMsid(cells[4].getContents());
				staff.setContactNumber(cells[5].getContents());
				staff.setHomeAddress(cells[6].getContents());
				staff.setRemark(cells[7].getContents());
				staff.setDepId(parentDep.getId());
				staff.setCompanyId(parentDep.getCompanyId());
				staff.setStartWorkTime(parentDep.getStartWorkTime());
				staff.setActivateState("0");
				staff.setEndWorkTime(parentDep.getEndWorkTime());
				staff.setIsEnable(true);
				staff.setCreater(username);
				staff.setWorkWeekDays(parentDep.getWorkWeekDays());
				staff.setLocateInterval(parentDep.getLocateInterveal());
				staff.setCreateTime(new Date());
				staff.setSignInStartTime(parentDep.getSignInStartTime());
				staff.setSignInEndTime(parentDep.getSignInEndTime());
				staff.setSignOutStartTime(parentDep.getSignOutStartTime());
				staff.setSignOutEndTime(parentDep.getSignOutEndTime());
				staff.setIsCompanySignIn(parentDep.getIsCompanySignIn());
				staff.setIsClientSignIn(parentDep.getIsClientSignIn());
				staff.setIsCompanySignOut(parentDep.getIsCompanySignOut());
				staff.setIsClientSignOut(parentDep.getIsClientSignOut());
				
				//��֤�ֻ���
				boolean isAdd=true;
				if(!pattern.matcher(staff.getMobileNumber()).matches()){
					staff.setStandby1("0");
					isAdd=false;
				}
				if(DataCache.phoneStaffIdMap.get(staff.getMobileNumber())!=null){
					staff.setStandby1("1");
					isAdd=false;
				}
				staffList.add(staff);
				
				if(isAdd){
					publicDAO.save(staff);
					CacheStaffMap.addStaff(staff);
				}else{
					break;
				}
			}
			return staffList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
}
