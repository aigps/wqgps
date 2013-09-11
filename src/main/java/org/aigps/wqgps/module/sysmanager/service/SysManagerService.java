package org.sunleads.module.sysmanager.service;

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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.auth.AuthType;
import org.sunleads.auth.AuthUtil;
import org.sunleads.common.cache.CmdStateCache;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.cache.SessionData;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqDepInfo;
import org.sunleads.common.entity.WqLogInfo;
import org.sunleads.common.entity.WqLogType;
import org.sunleads.common.entity.WqMapRegion;
import org.sunleads.common.entity.WqMenuInfo;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.log.LogType;
import org.sunleads.common.log.LogUtil;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.DateUtil;
import org.sunleads.common.util.RetrospectUtil;
import org.sunleads.module.region.service.RegionService;
import org.sunleads.module.report.dao.RegionVisitDAO;
import org.sunleads.socket.CmdUtil;
import org.sunleads.timing.CacheClientStaffIdMap;
import org.sunleads.timing.CacheDepMap;
import org.sunleads.timing.CacheRetrospect;
import org.sunleads.timing.CacheStaffMap;

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
	 * 查询当前用户的下级所有用户
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
	 * 新增或修改用户
	 * @param entiry
	 */
	public WqUserInfo addOrUpdateUser(WqUserInfo subUser) throws Exception{
		try {
			WqUserInfo user = AppUtil.getUserInfo();
			if(subUser.getId()==null){
				LogUtil.saveLog(LogType.CREATE_USER, "父用户:"+user.getCnName()+"创建了子用户:"+subUser.getCnName());
			}else{
				LogUtil.saveLog(LogType.UPDATE_USER, "父用户:"+user.getCnName()+"修改了子用户:"+subUser.getCnName());
			}
			publicDAO.save(subUser);
			return subUser;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 删除用户
	 * @param entiry
	 */
	public void delUser(WqUserInfo subUser) throws Exception{
		try {
			publicDAO.delete(subUser);
			
			//删除用户的权限
			AuthUtil.deleteResListByOwnerId(AuthType.USER_CLIENT, subUser.getId());
			AuthUtil.deleteResListByOwnerId(AuthType.USER_DEP, subUser.getId());
			AuthUtil.deleteResListByOwnerId(AuthType.USER_MENU, subUser.getId());
			
			WqUserInfo user = AppUtil.getUserInfo();
			LogUtil.saveLog(LogType.CREATE_USER, "父用户:"+user.getCnName()+"删除了子用户:"+subUser.getCnName());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	/**
	 * 得到指定用户的菜单权限
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
	 * 保存用户对用的菜单
	 * @param menuList
	 * @param userId
	 */
	public void saveUserMenu(List<String> menuList,WqUserInfo subUser) throws Exception{
		try {
			AuthUtil.saveResListByOwnerId(AuthType.USER_MENU, menuList, subUser.getId());

			WqUserInfo user = AppUtil.getUserInfo();
			LogUtil.saveLog(LogType.USER_MENU_RIGHT, "父用户:"+user.getCnName()+"修改了子用户:"+subUser.getCnName()+"的菜单权限");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 根据Id查询地图区域列表
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
	 * 保存公司信息
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
			//删除旧的区域
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
			LogUtil.saveLog(LogType.UPDATE_COMPANY, "用户:"+user.getCnName()+"修改了公司信息");
			DataCache.companyInfoMap.put(company.getId(), company);
			
			//比较前后修改的区域是否一致，如果不一致，就要进行数据的追溯
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
	 * 保存部门信息
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
			if(isAdd){//新增部门
				WqUserInfo user = AppUtil.getUserInfo();
				if(!user.getIsAdmin()){//如果当前用户不是管理员，将部门权限赋给当前用户
					List<String> depList = new ArrayList<String>();
					depList.add(dep.getId());
					List<String> userIds = new ArrayList<String>();
					userIds.add(user.getId());
					AuthUtil.addResList(AuthType.USER_DEP,userIds,depList);
					
//					AuthUtil.saveResListByOwnerId(AuthType.USER_DEP, depList, user.getId());
				}
				CacheDepMap.addDep(dep);
				LogUtil.saveLog(LogType.CREATE_USER, "用户:"+AppUtil.getUserInfo().getCnName()+"新增部门信息"+dep.getName());
			}else{//修改部门
				CacheDepMap.updateDep(dep);
				LogUtil.saveLog(LogType.CREATE_USER, "用户:"+AppUtil.getUserInfo().getCnName()+"修改部门信息"+dep.getName());
			}
			return dep;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 删除部门
	 * @param depId
	 */
	public void stopDepUse(WqDepInfo dep) throws Exception{
		try {
			//删除部门
			dep.setIsEnable(false);
			publicDAO.save(dep);
			CacheDepMap.deleteDep(dep);

			//删除这个部门所赋给用户的权限
			AuthUtil.deleteResListByResId(AuthType.USER_DEP,dep.getId());
			
			//删除部门下的所有员工
			List<WqStaffInfo> staffs = DataCache.depStaffMap.get(dep.getId());
			if(staffs != null){
				stopStaffUse(staffs);
			}
			
			LogUtil.saveLog(LogType.DELETE_DEP, "用户:"+AppUtil.getUserInfo().getCnName()+"删除了部门:"+dep.getName());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	/**
	 * 根据部门ID查找员工
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
	 * 保存员工
	 * @param entiry
	 * @return
	 */
	public WqStaffInfo saveStaff(WqStaffInfo staff) throws Exception{
		boolean isAdd = staff.getId()==null;
		String staffId = DataCache.phoneStaffIdMap.get(staff.getMobileNumber());
		if(isAdd){//新增员工
			if(StringUtils.isNotBlank(staffId)){//手机号已被其它员工使用
				return null;
			}
		}else{//修改员工
			if(StringUtils.isNotBlank(staffId) && !staff.getId().equals(staffId)){////手机号已被其它员工使用
				return null;
			}
		}
		try {
			publicDAO.save(staff);
			
			if(isAdd){
				CacheStaffMap.addStaff(staff);
				LogUtil.saveLog(LogType.CREATE_STAFF, "新增员工:"+staff.getCnName());
			}else{
				CacheStaffMap.updateStaff(staff);
				LogUtil.saveLog(LogType.UPDATE_STAFF, "修改员工:"+staff.getCnName());
			}
			return staff;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 删除员工
	 */
	public List<WqStaffInfo> stopStaffUse(List<WqStaffInfo> staffList) throws Exception{
		try {
			for(WqStaffInfo staff:staffList){	
				staff.setIsEnable(false);
				publicDAO.save(staff);
				CacheStaffMap.deleteStaff(staff);
				LogUtil.saveLog(LogType.DELETE_STAFF, "用户:"+AppUtil.getUserInfo().getCnName()+"删除了员工:"+staff.getCnName());
				
				//删除员工权限
				AuthUtil.deleteResListByOwnerId(AuthType.STAFF_CLIENT, staff.getId());
				AuthUtil.deleteResListByOwnerId(AuthType.STAFF_RULE, staff.getId());
				
				//删除员工，需要对员工追溯的数据进行取消
				CacheRetrospect.deleteStaff(staff.getId());
			}
			return staffList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 根据员工集合发出激活请求
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
					//发送激活，保存激活次数
					String startWorkTime = staff.getStartWorkTime().replaceAll(":", "").concat("00");
					String endWorkTime = staff.getEndWorkTime().replaceAll(":", "").concat("59");
					
					CmdUtil.sendActiveCmd(staff.getMobileType(),staff.getMobileNumber(),fixModel,staff.getGpsInterval(),startWorkTime,endWorkTime,staff.getWorkWeekDays());
					CmdStateCache.activeTmnNumMap.put(staff.getMobileNumber(),new String[]{"1",DateUtil.getCurDate()});
				}else{//电信粗定位
					staff.setActivateState("2");
				}
				names.append(staff.getCnName()).append(" ");
			}
			LogUtil.saveLog(LogType.UPDATE_STAFF, "激活员工:"+names);
			return staffIdList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 失活员工
	 * @param entiryList
	 * @return
	 */
	public List<String> cancelActivateStaffState(List<String> staffIdList) throws Exception{
		StringBuilder names = new StringBuilder();
		try {
			for(String staffId:staffIdList){
				WqStaffInfo staff = DataCache.staffMap.get(staffId);
				staff.setActivateState("4");//失活中
				publicDAO.save(staff);
				
				//发送激活，保存激活次数
				CmdUtil.sendCancelActiveCmd(staff.getMobileType(),staff.getMobileNumber());
				CmdStateCache.cancelActiveTmnNumMap.put(staff.getMobileNumber(),new String[]{"1",DateUtil.getCurDate()});
				
				names.append(staff.getCnName()).append(" ");
			}
			LogUtil.saveLog(LogType.UPDATE_STAFF, "失活员工:"+names);
			return staffIdList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 根据员工ID成功或失败激活
	 * @param tmnCode
	 * @param stateNum 0:未激活;1:激活中;2:已激活;3:激活失败;4:失活中;5:已失活;6:失活失败
	 */
	public void updateActiveState(String phone,String actState) throws Exception{
		try {
			String staffId = DataCache.phoneStaffIdMap.get(phone);
			if(StringUtils.isNotBlank(staffId)){
				WqStaffInfo staff = DataCache.staffMap.get(staffId);
				
				log.info("将员工:"+staff.getCnName()+",状态从:"+staff.getActivateState()+"变成："+actState);
				
				staff.setActivateState(actState);
				publicDAO.save(staff);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 得到归属员工的客户
	 * @param entiry
	 * @return
	 */
	public Set<String> findStaffRoleClient(WqStaffInfo staff) throws Exception{
		return CacheClientStaffIdMap.getClientIds(staff.getId());
	}
	
	/**
	 * 保存员工对应的客户
	 * @param entiry
	 * @param clientList
	 */
	public void saveStaffRoleClient(List<WqStaffInfo> staffList,List<String> clientIdList,String type) throws Exception{
		try {
			List<String> staffIdList = new ArrayList<String>();
			for(WqStaffInfo staff : staffList){
				staffIdList.add(staff.getId());
			}
			if("set".equals(type)){//全新分配客户
				for(String staffId : staffIdList){
					AuthUtil.saveResListByOwnerId(AuthType.STAFF_CLIENT,clientIdList,staffId);
				}
			}else if("add".equals(type)){//只添加指定客户，原有客户保留
				AuthUtil.addResList(AuthType.STAFF_CLIENT, staffIdList, clientIdList);
			}else if("remove".equals(type)){//只删除指定客户，原有客户保留
				AuthUtil.deleteResList(AuthType.STAFF_CLIENT, staffIdList, clientIdList);
			}
			
			for(WqStaffInfo staff : staffList){
				LogUtil.saveLog(LogType.STAFF_CLIENT_RIGHT, "修改员工:"+staff.getCnName()+"所属的客户");
			}
			
			//修改和员工和客户的对照关系，要进行数据的追溯
			//同时返回删除了哪些员工的哪些区域
			List<String[]> deleteStaffRegionList = RetrospectUtil.updateStaffClient(staffIdList, clientIdList, type);
			
			//删除之前员工和区域的进出记录
			for(String[] deleteStaffRegion : deleteStaffRegionList){
				regionVisitDAO.deleteRegionVisit(deleteStaffRegion[0], deleteStaffRegion[1]);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 查询所有日志的类型
	 * @return
	 */
	public List<WqLogType> findLogType() throws Exception{
		List<WqLogType> logType = new ArrayList<WqLogType>(DataCache.logTypeMap.values());
		WqLogType allType = new WqLogType();
		allType.setId(null);
		allType.setName("所有类型");
		allType.setLogType("ALL_TYPE");
		logType.add(0, allType);
		return logType;
	}
	
	/**
	 * 查询日志信息
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
	 * 删除日志信息
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
			LogUtil.saveLog(LogType.DELETE_LOG, "用户:"+user.getCnName()+"删除了从:"+startTime+"到"+endTime+",日志类型ID为"+logTypeId+"的日志信息");
		} catch (DataAccessException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 保存用户对应的部门
	 * @param menuList
	 * @param userId
	 */
	public void saveUserDep(List<String> depList,WqUserInfo subUser) throws Exception{
		try {
			AuthUtil.deleteResListByOwnerId(AuthType.USER_DEP, subUser.getId());
			AuthUtil.saveResListByOwnerId(AuthType.USER_DEP, depList, subUser.getId());

			WqUserInfo user = AppUtil.getUserInfo();
			LogUtil.saveLog(LogType.USER_DEP_RIGHT, "父用户:"+user.getCnName()+"修改了子用户:"+subUser.getCnName()+"的部门权限");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 查询指定用户的部门权限
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
				
				//验证手机号
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
