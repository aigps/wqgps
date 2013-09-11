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
	
	//����ʱ��¼
	public boolean debugLogin(String userName) throws Exception{
		List<WqUserInfo> user = publicDAO.find("from WqUserInfo where userName=? ", userName);
		if(user==null || user.isEmpty()){
			return false;
		}
		AppUtil.getSessionData().setUserInfo(user.get(0));
		return true;
	}

	//�ⲿ�ӿڶ�λʱ��¼
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

		//����ǰ��������IP��ַ���浽SESSION��,���ڻ�ȡMAPKEY
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
				//������ʱ����˾��û�����ֱ꣬�ӷ���true,���´�����
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
	 * �����û���,�����û�
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
	 * ��ȡ��ǰ�û���Ȩ�޿�����һЩ��Ϣ,�糵�ӳ���,MAPkey
	 * ���ڵ�¼�ɹ�֮��,���뵽flexҳ���,�������û�ȡ,���̵�¼ʱ��
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
			//��ǰ�û���Ȩ�޵Ĳ˵�
			List<WqMenuInfo> menus = AuthUtil.getResList(AuthType.USER_MENU);
			List<WqDepInfo> deps = AuthUtil.getResList(AuthType.USER_DEP);
			List<WqStaffInfo> staffs = AuthUtil.getStaffList(deps);

			List<WqMenuInfo> ms = new ArrayList<WqMenuInfo>();
			if("yzwq/".equals(sd.getCompanyFlag())){
				for(WqMenuInfo m : menus){
					if("�ͻ��ݷüƻ�".equals(m.getName())){
						m = (WqMenuInfo)BeanUtils.cloneBean(m);
						m.setName("Ͷ�ݼƻ�");
					}else if("�ͻ��ݷñ���".equals(m.getName())){
						m = (WqMenuInfo)BeanUtils.cloneBean(m);
						m.setName("�ʵ�Ա����");
					}else if("�ͻ����ݷñ���".equals(m.getName())){
						m = (WqMenuInfo)BeanUtils.cloneBean(m);
						m.setName("Ͷ�����򱨱�");
					}else if("�ͻ�����".equals(m.getName())){
						m = (WqMenuInfo)BeanUtils.cloneBean(m);
						m.setName("Ͷ���������");
					}else if("Ա������".equals(m.getName())){
						m = (WqMenuInfo)BeanUtils.cloneBean(m);
						m.setName("Ͷ��Ա����");
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
	
	//�ж���û�з��Ͷ��ŵĲ˵�
	private Boolean hasSendSmsMenu(List<WqMenuInfo> menus){
		for(WqMenuInfo menu : menus){
			if(menu.getName().equals("���ŷ���") || menu.getId().equals("301")){
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
	
	//������������
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
