package org.sunleads.module.login.service;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.auth.AuthType;
import org.sunleads.auth.AuthUtil;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.cache.SessionData;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqDepInfo;
import org.sunleads.common.entity.WqMenuInfo;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.log.LogType;
import org.sunleads.common.log.LogUtil;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.DataCompressUtil;

import flex.messaging.FlexContext;

@Component
@Transactional
@SuppressWarnings("unchecked")
public class LoginService {
	public final static Log log = LogFactory.getLog(LoginService.class);
	
	private PublicDAO publicDAO;

	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	
	//调试时登录
	public boolean debugLogin(String userName) throws Exception{
		List<WqUserInfo> user = publicDAO.find("from WqUserInfo where userName=? ", userName);
		if(user==null || user.isEmpty()){
			return false;
		}
		AppUtil.getSessionData().setUserInfo(user.get(0));
		return true;
	}

	//外部接口定位时登录
	public int locateLogin(String userName,String password) throws Exception{
		WqUserInfo user = findUser(userName,password,null);
		if(user==null){
			return 0;
		}
		if (!user.getIsEnable()) {
			return -1;
		}
		SessionData sessionData = AppUtil.getSessionData();
		HttpServletRequest request = FlexContext.getHttpRequest();

		//将当前服务器的IP地址保存到SESSION中,用于获取MAPKEY
		String host = request.getHeader("host");
		sessionData.setLoginIp(host.split(":")[0]);
		
		sessionData.setUserInfo(user);
		return 1;
	}
	
	public boolean checkSession(String userName,String password,String companyId,List<String> clickMenus) {
		SessionData sd = AppUtil.getSessionData();
		try{
			if(sd.getUserInfo() == null){
				log.info("============Null UserInfo");
				//刚重启时，公司还没加载完，直接返回true,等下次再来
				if(DataCache.companyInfoMap.isEmpty()){
					return true;
				}
				WqUserInfo user = findUser(userName,password,companyId);
				if(user != null){
					sd.setUserInfo(user);
					sd.setCompany(DataCache.companyInfoMap.get(user.getCompanyId()));
				}
				return (user != null);
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return false;
		}finally{
			if(clickMenus!=null && !clickMenus.isEmpty()){
				for(String m : clickMenus){
					LogUtil.saveLog(LogType.USER_CLICK_MENU, m);
				}
			}
		}
		return true;
	}
	
	private String getCompanyId(String userName,String companyFlag){
		if("yd_admin".equals(userName) || "lt_admin".equals(userName) || "dx_admin".equals(userName) || StringUtils.isBlank(companyFlag)){
			return null;
		}
		if(companyFlag!=null){
			companyFlag = companyFlag.replaceAll("/", "");
		}
		for(WqCompanyInfo info:DataCache.companyInfoMap.values()){
			if(info.getUrlFlag()!=null && info.getUrlFlag().equals(companyFlag)){
				return info.getId();
			}
		}
		return null;
	}
	
	/**
	 * 根据用户名,查找用户
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public List<WqUserInfo> findUserByName(String userName,String companyFlag){
//		String companyId = getCompanyId(userName,companyFlag);
		
//		if(StringUtils.isBlank(companyId)){
			return publicDAO.find("from WqUserInfo where userName=?", userName);
//		}
//		return publicDAO.find("from WqUserInfo where userName=? and companyId=?", userName, companyId);
	}
	
	public WqUserInfo findUser(String userName,String password,String companyId){
		List<WqUserInfo> users = null;
		users = publicDAO.find("from WqUserInfo where userName=? and password=?", userName, password);
//		
//		if(StringUtils.isBlank(companyId)){
//			users = publicDAO.find("from WqUserInfo where userName=? and password=?", userName, password);
//		}else{
//			users = publicDAO.find("from WqUserInfo where userName=? and password=? and companyId=?", userName, password, companyId);
//		}
		if(users!=null && !users.isEmpty()){
			return users.get(0);
		}
		return null;
	}
	/**
	 * 获取当前用户有权限看到的一些信息,如车队车辆,MAPkey
	 * 是在登录成功之后,进入到flex页面后,再来调用获取,减短登录时间
	 * @return
	 * @throws Exception
	 */
	public Object getLoginInfo() throws Exception{
		SessionData sd = AppUtil.getSessionData();
		WqUserInfo user = sd.getUserInfo();
		if(user == null){
			return null;
		}
		
		WqCompanyInfo company = DataCache.companyInfoMap.get(user.getCompanyId());
		sd.setCompany(company);
		
		try{
			//当前用户有权限的菜单
			List<WqMenuInfo> menus = AuthUtil.getResList(AuthType.USER_MENU);
			List<WqDepInfo> deps = AuthUtil.getResList(AuthType.USER_DEP);
			List<WqStaffInfo> staffs = AuthUtil.getStaffList(deps);

			List<WqMenuInfo> ms = new ArrayList<WqMenuInfo>();
			if("yzwq/".equals(sd.getCompanyFlag())){
				for(WqMenuInfo m : menus){
					if("客户拜访计划".equals(m.getName())){
						m = (WqMenuInfo)BeanUtils.cloneBean(m);
						m.setName("投递计划");
					}else if("客户拜访报表".equals(m.getName())){
						m = (WqMenuInfo)BeanUtils.cloneBean(m);
						m.setName("邮递员报表");
					}else if("客户被拜访报表".equals(m.getName())){
						m = (WqMenuInfo)BeanUtils.cloneBean(m);
						m.setName("投递区域报表");
					}else if("客户管理".equals(m.getName())){
						m = (WqMenuInfo)BeanUtils.cloneBean(m);
						m.setName("投递区域管理");
					}else if("员工管理".equals(m.getName())){
						m = (WqMenuInfo)BeanUtils.cloneBean(m);
						m.setName("投递员管理");
					}
					ms.add(m);
				}
			}else{
				ms = menus;
			}
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("user", user);
			map.put("gmapKey", getMapKey("gmapkey",sd.getLoginIp()));
			map.put("mapabcKey", getMapKey("mapabckey",sd.getLoginIp()));
			map.put("menus", ms);
			map.put("hasSendSmsMenu", hasSendSmsMenu(ms));
			map.put("deps", deps);
			map.put("staffs", staffs);
			map.put("company", company);
			map.put("needToAlarm", AppUtil.getBean("needToAlarm"));
			map.put("isHebWq", sd.getCompanyFlag()!=null && sd.getCompanyFlag().indexOf("hebwq")!=-1);
			map.put("outerParamMap", sd.getOuterParamMap());
			
			return DataCompressUtil.compress(map);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	//判断有没有发送短信的菜单
	private Boolean hasSendSmsMenu(List<WqMenuInfo> menus){
		for(WqMenuInfo menu : menus){
			if(menu.getName().equals("短信发送") || menu.getId().equals("301")){
				return true;
			}
		}
		return false;
	}

	private String getMapKey(String mapType,String ip){
		String mapKey = DataCache.systemParamsMap.get(mapType+":"+ip);
		if(StringUtils.isBlank(mapKey)){
			mapKey = DataCache.systemParamsMap.get(mapType+":default");
		}
		return mapKey;
	}
	
	//行政区域数据
	public Object getChinaAreaList(){
		return DataCompressUtil.compress(DataCache.dcChinaAreaList);
	}
	public Object getTradeList(){
		return DataCompressUtil.compress(DataCache.tradeList);
	}
	
	public Object refreshDeps() throws Exception{
		return DataCompressUtil.compress(AuthUtil.getResList(AuthType.USER_DEP));
	}

	public Object refreshDepsStaffs() throws Exception{
		List<WqDepInfo> deps = AuthUtil.getResList(AuthType.USER_DEP);
		List<WqStaffInfo> staffs = AuthUtil.getStaffList(deps);
		return DataCompressUtil.compress(new Object[]{deps,staffs});
	}
	
}
