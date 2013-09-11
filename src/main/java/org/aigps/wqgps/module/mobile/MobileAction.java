
package org.sunleads.module.mobile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.auth.AuthType;
import org.sunleads.auth.AuthUtil;
import org.sunleads.common.cache.CmdStateCache;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.cache.SessionData;
import org.sunleads.common.entity.DcGpsReal;
import org.sunleads.common.entity.WqDepInfo;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.log.LogType;
import org.sunleads.common.log.LogUtil;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.DateUtil;
import org.sunleads.common.util.StrUtil;
import org.sunleads.module.location.dao.LocationDAO;
import org.sunleads.module.login.service.LoginService;
import org.sunleads.socket.CmdTypeEnum;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2012-1-13下午01:52:32
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
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
			return "用户不存在!";
		}
		
		WqUserInfo user = null;
		for(WqUserInfo u : users){
			if(u.getPassword().equals(password)){
				user = u;
				break;
			}
		}
		if(user == null){
			return "密码错误!";
		}
		if (!user.getIsEnable()) {
			return "此用户已被限制使用，请联系管理员!";
		}
		//1和2都是超级管理员，空和0是系统公司用户
		if("1".equals(user.getSuperAdmin()) || "2".equals(user.getSuperAdmin())){
			return "手机系统暂时不支持超级管理员功能!";
		}
		
		SessionData sessionData = AppUtil.getSessionData();
		sessionData.setUserInfo(user);//保存用户对象到session中
		sessionData.setCompany(DataCache.companyInfoMap.get(user.getCompanyId()));//保存公司
		
		//记录用户登录
		LogUtil.saveLog(LogType.USER_LOGIN, "手机登录");

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
			"30:开启飞行模式",
			"31:关闭飞行模式",
			"32:启用数据网络",
			"33:禁用数据网络",
			"34:启用GPS定位",
			"35:禁用GPS定位",
			"36:开机",
			"37:关机",
			"38:重新启动",
			"39:电池低",
			"40:更换SIM卡"
	};
	
	private String getCmdState(String staffId){
		String state = CmdStateCache.getCmdStateCode(CmdTypeEnum.LCSNow.getValue(), staffId);
		if("0".equals(state)){
			return "等待回复";
		}else if("1".equals(state)){
			return "成功";
		}else if("2".equals(state)){
			return "失败";
		}else if("2".equals(state)){
				return "超时";
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
			return regionSet.contains(gvp.getTmnAlias()) ? "工作中" : "在途中";
		}else{
			return "脱岗";
		}
	}
}

