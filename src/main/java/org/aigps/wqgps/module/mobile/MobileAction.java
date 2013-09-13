
package org.aigps.wqgps.module.mobile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aigps.wqgps.auth.AuthType;
import org.aigps.wqgps.auth.AuthUtil;
import org.aigps.wqgps.common.cache.CmdStateCache;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.cache.SessionData;
import org.aigps.wqgps.common.entity.DcGpsReal;
import org.aigps.wqgps.common.entity.WqDepInfo;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.log.LogType;
import org.aigps.wqgps.common.log.LogUtil;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.common.util.StrUtil;
import org.aigps.wqgps.module.location.dao.LocationDAO;
import org.aigps.wqgps.module.login.service.LoginService;
import org.aigps.wqgps.socket.CmdTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2012-1-13����01:52:32
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
 */
@Component
@Transactional
public class MobileAction {

	public final static Log log = LogFactory.getLog(MobileAction.class);
	private LocationDAO locationDAO;
	@Autowired
	public void setLocationDAO(LocationDAO locationDAO) {
		this.locationDAO = locationDAO;
	}
	
	public String login(String userName,String password,String companyFlag) throws Exception{
		LoginService loginService = (LoginService) AppUtil.getBean("loginService");
		List<WqUserInfo> users = loginService.findUserByName(userName,companyFlag);
		
		if(users==null || users.isEmpty()){
			return "�û�������!";
		}
		
		WqUserInfo user = null;
		for(WqUserInfo u : users){
			if(u.getPassword().equals(password)){
				user = u;
				break;
			}
		}
		if(user == null){
			return "�������!";
		}
		if (!user.getIsEnable()) {
			return "���û��ѱ�����ʹ�ã�����ϵ����Ա!";
		}
		//1��2���ǳ�������Ա���պ�0��ϵͳ��˾�û�
		if("1".equals(user.getSuperAdmin()) || "2".equals(user.getSuperAdmin())){
			return "�ֻ�ϵͳ��ʱ��֧�ֳ�������Ա����!";
		}
		
		SessionData sessionData = AppUtil.getSessionData();
		sessionData.setUserInfo(user);//�����û�����session��
		sessionData.setCompany(DataCache.companyInfoMap.get(user.getCompanyId()));//���湫˾
		
		//��¼�û���¼
		LogUtil.saveLog(LogType.USER_LOGIN, "�ֻ���¼");

		return "true";
	}
	
	public String getAllStaff() throws Exception{
		try{
			List<WqDepInfo> deps = AuthUtil.getResList(AuthType.USER_DEP);
			List<WqStaffInfo> staffs = AuthUtil.getStaffList(deps);
			
			StringBuilder ss = new StringBuilder("[");
			
			for(WqStaffInfo staff : staffs){
				ss.append("['").append(staff.getCnName()).append("','").append(staff.getMobileNumber()).append("'],");
			}
			if(ss.length()>0){
				ss.deleteCharAt(ss.length()-1);
			}
			ss.append("]");
			return ss.toString();
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	public String getStaffPosition(String phone) throws Exception{
		String staffId = DataCache.phoneStaffIdMap.get(phone);
		DcGpsReal gvp = DataCache.staffPostionMap.get(staffId);
		if(gvp == null){
			return null;
		}
		String nowTime = DateUtil.getNowDate().concat(" 00:00:00");
		
		List<String> l = new ArrayList<String>();
		l.add(staffId);
		Set<String> regionSet = locationDAO.findRegionVisitByStaffIdList(l, nowTime);
		String workState = getWorkState(regionSet,gvp,nowTime);
		String cmdState = getCmdState(staffId);
		String phoneState = getPhoneState(staffId);
		
		BigDecimal lat = gvp.getLatOffset(), lng = gvp.getLogitOffset();
		if(lat == null || lng == null){
			lat = gvp.getLat();
			lng = gvp.getLogit();
		}
		
		String addr = gvp.getLocDesc();
		if(StringUtils.isBlank(addr)){
			addr = StrUtil.geocodeAddr(lat+"",lng+"");
		}
		
		return "['"+gvp.getReportTime()+"',"+lat+","+lng+",'"+addr+"','"+workState+"','"+cmdState+"','"+phoneState+"']";
	}

	private String[] phoneStateList = new String[]{
			"30:��������ģʽ",
			"31:�رշ���ģʽ",
			"32:������������",
			"33:������������",
			"34:����GPS��λ",
			"35:����GPS��λ",
			"36:����",
			"37:�ػ�",
			"38:��������",
			"39:��ص�",
			"40:����SIM��"
	};
	
	private String getCmdState(String staffId){
		String state = CmdStateCache.getCmdStateCode(CmdTypeEnum.LCSNow.getValue(), staffId);
		if("0".equals(state)){
			return "�ȴ��ظ�";
		}else if("1".equals(state)){
			return "�ɹ�";
		}else if("2".equals(state)){
			return "ʧ��";
		}else if("2".equals(state)){
				return "��ʱ";
		}
		return "";
	}
	private String getPhoneState(String staffId){
		String[] ps = DataCache.phoneStateMap.get(staffId);
		if(ps == null || ps.length==0 || StringUtils.isBlank(ps[0])){
			return "";
		}
		for(int i=0; i<phoneStateList.length; i++){
			if(phoneStateList[i].startsWith(ps[0])){
				return phoneStateList[i].split(":")[1];
			}
		}
		return "";
	}
	
	private String getWorkState(Set<String> regionSet,DcGpsReal gvp,String nowTime){
		String rt = gvp.getReportTime();
		if(StringUtils.isNotBlank(rt) && rt.compareTo(nowTime)>=0){
			return regionSet.contains(gvp.getTmnAlias()) ? "������" : "��;��";
		}else{
			return "�Ѹ�";
		}
	}
}

