
package org.aigps.wqgps.module.webservice;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.jws.WebService;

import org.aigps.wqgps.auth.AuthType;
import org.aigps.wqgps.auth.AuthUtil;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.DcGpsReal;
import org.aigps.wqgps.common.entity.WqCompanyInfo;
import org.aigps.wqgps.common.entity.WqDepInfo;
import org.aigps.wqgps.common.entity.WqEleFence;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.module.admin.service.AdminService;
import org.aigps.wqgps.module.heb.HebService;
import org.aigps.wqgps.module.login.service.LoginService;
import org.aigps.wqgps.socket.CmdUtil;
import org.aigps.wqgps.timing.CacheDepMap;
import org.aigps.wqgps.timing.CacheRetrospect;
import org.aigps.wqgps.timing.CacheStaffMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@WebService(endpointInterface = "org.sunleads.module.webservice.WqService")
@Component
@Transactional
public class WqServiceImpl implements WqService {

	public final static Log log = LogFactory.getLog(AdminService.class);

	//员工和返回的URL的关系
	private static Map<String,String> staffIdUrlMap = new ConcurrentHashMap<String,String>();
	
	private PublicDAO publicDAO;
	
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}

	/**
	 * 创建或者修改公司
	 * 
	 * @param adminName			公司管理员用户名
	 * @param adminPassword　	公司管理员密码
	 * @param companyName　		公司名称
	 * @param companyLinkMan　	公司联系人
	 * @param companyPhone　		公司电话()
	 * 
	 * @return true:创建或修改成功;其它:错误的提示信息
	 */
	public String createOrUpdateCompany(String adminName,String adminPassword,String companyName,String companyLinkMan,String companyPhone){
		if(StringUtils.isBlank(adminName)){
			return "用户名不能为空。";
		}
		if(StringUtils.isBlank(adminPassword)){
			return "密码不能为空。";
		}
		if(StringUtils.isBlank(companyName)){
			return "公司名称不能为空。";
		}
		if(StringUtils.isBlank(companyLinkMan)){
			return "公司联系人不能为空。";
		}
		if(StringUtils.isBlank(companyPhone)){
			return "公司电话不能为空。";
		}
		try{
			boolean isNewAdmin = !WqServiceDAO.existUserName(adminName,publicDAO);
			WqCompanyInfo company = null;
			WqUserInfo admin = null;
			if(isNewAdmin == false){
				admin = WqServiceDAO.getUser(adminName, adminPassword, publicDAO);
				if(admin == null){
					return "用户名已经被使用，请换别的用名。";
				}else if(!admin.getIsAdmin()){
					return "该用户不是公司管理员，无权限修改公司信息。";
				}
				company = DataCache.companyInfoMap.get(admin.getCompanyId());
			}
			if(company == null){
				if(WqServiceDAO.existCompany(companyName, publicDAO)){
					return "公司名称已经被使用，请换别的公司名称。";
				}
				company = new WqCompanyInfo();
				company.setName(companyName);
				company.setLinkman(companyLinkMan);
				company.setContactNumber(companyPhone);
				company.setUseInvalidLoc(true);
				company.setTrade("0");
				company.setPersonNumber(new BigDecimal(1000));
				company.setFixModels("0123456");
				company.setRetrospectLong(31l);
				company.setIsSmsNotice(false);
				company.setState("1");//正式使用
				company.setIsEnable(true);
				company.setCreater("webservice");
				company.setCreateTime(new Date());
				publicDAO.save(company);
				
				WqDepInfo dep = new WqDepInfo();
				dep.setName(companyName);
				dep.setCompanyId(company.getId());
				dep.setCreater("webservice");
				dep.setCreateTime(new Date());
				dep.setParentId("0");
				dep.setIsEnable(true);
				publicDAO.save(dep);
	
				DataCache.companyInfoMap.put(company.getId(),company);
				CacheDepMap.addDep(dep);
			}else{
				company.setName(companyName);
				company.setLinkman(companyLinkMan);
				company.setContactNumber(companyPhone);
				publicDAO.save(company);
			}
			
			if(admin == null){
				admin = new WqUserInfo();
				admin.setIsAdmin(true);
				admin.setCreater("webservice");
				admin.setCreateTime(new Date());
				admin.setUserName(adminName);
				admin.setCnName(adminName);
				admin.setPassword(adminPassword);
				admin.setParentId("webservice");
				admin.setIsEnable(true);
			}
			admin.setCompanyId(company.getId());
			publicDAO.save(admin);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "true";
	}
	
	/**
	 * 创建单个员工
	 * 
	 * @param adminName 		公司管理员用户名
	 * @param adminPassword　	公司管理员密码
	 * @param staffName　		员工姓名
	 * @param staffPhone　		员工手机号
	 * @param startWorkTime　	上班时间(09:00)
	 * @param endWorkTime　		下班时间(18:00)
	 * @param locateInterval　	定位间隔(秒)
	 * @param locateModel　		定位方式(0:MSA;1:GOOGLE;2:GPS;3:MSA->GPS;4:GPS->GOOGLE;5:GPS->MSA;6:GPS->MSA->GOOGLE;)
	 *
	 * @return true:创建成功;其它:错误的提示信息
	 */
	public String createStaff(String adminName,String adminPassword,String staffName,String staffPhone,String startWorkTime,String endWorkTime,String locateInterval,String locateModel){
		if(StringUtils.isBlank(adminName)){
			return "用户名不能为空";
		}
		if(StringUtils.isBlank(adminPassword)){
			return "密码不能为空";
		}
		if(StringUtils.isBlank(staffName)){
			return "员工姓名不能为空";
		}
		if(StringUtils.isBlank(staffPhone)){
			return "员工手机号不能为空";
		}
		if(StringUtils.isBlank(startWorkTime)){
			return "上班时间不能为空";
		}
		if(!Pattern.matches("\\d{2}:\\d{2}", startWorkTime)){
			return "上班时间格式不对";
		}
		if(StringUtils.isBlank(endWorkTime)){
			return "下班时间不能为空";
		}
		if(!Pattern.matches("\\d{2}:\\d{2}", endWorkTime)){
			return "下班时间格式不对";
		}
		if(StringUtils.isBlank(locateInterval)){
			return "定位间隔不能为空";
		}
		if(!Pattern.matches("[0-9]*", locateInterval)){
			return "定位间隔必须是数字，单位为秒。";
		}
		if(StringUtils.isBlank(locateModel)){
			return "定位方式不能为空";
		}
		if(!Pattern.matches("[0-6]", locateModel)){
			return "定位方式从0至6";
		}
		try{
			String companyId = WqServiceDAO.getCompanyId(adminName, adminPassword, publicDAO);
			if(companyId == null){
				return "请检查用户是否是公司管理员，密码是否正确。";
			}
			if(DataCache.phoneStaffIdMap.containsKey(staffPhone)){
				return "手机号已经被使用。";
			}
			WqStaffInfo staff = new WqStaffInfo();
			staff.setCompanyId(companyId);
			staff.setCnName(staffName);
			staff.setMobileNumber(staffPhone);
			staff.setStartWorkTime(startWorkTime);
			staff.setEndWorkTime(endWorkTime);
			staff.setLocateInterval(new BigDecimal(locateInterval));
			staff.setIntervalUnit("1");
			staff.setFixModel(locateModel);
			staff.setWorkWeekDays("1,2,3,4,5");
			staff.setCreater("webservice");
			staff.setCreateTime(new Date());
			staff.setIsEnable(true);
			List<WqDepInfo> depList= DataCache.comDepMap.get(companyId);
			if(depList!=null && !depList.isEmpty()){
				staff.setDepId(depList.get(0).getId());
			}
			publicDAO.save(staff);
			CacheStaffMap.addStaff(staff);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "true";
	}
	
	/**
	 * 删除单个员工
	 * @param adminName 公司管理员用户名
	 * @param adminPassword　公司管理员密码
	 * @param staffPhone 删除的员工手机号
	 * @return true:创建成功;其它:错误的提示信息
	 */
	public String deleteStaff(String adminName,String adminPassword,String staffPhone){
		if(StringUtils.isBlank(adminName)){
			return "用户名不能为空";
		}
		if(StringUtils.isBlank(adminPassword)){
			return "密码不能为空";
		}
		if(StringUtils.isBlank(staffPhone)){
			return "员工手机号不能为空";
		}

		try{
			String companyId = WqServiceDAO.getCompanyId(adminName, adminPassword, publicDAO);
			if(companyId == null){
				return "请检查用户是否是公司管理员，密码是否正确。";
			}
			WqStaffInfo staff = WqServiceDAO.getStaff(staffPhone, publicDAO);
			if(staff == null || !companyId.equals(staff.getCompanyId())){
				return "员工不存在。";
			}
			staff.setIsEnable(false);
			publicDAO.delete(staff);
			CacheStaffMap.deleteStaff(staff);
			
			//删除员工权限
			AuthUtil.deleteResListByOwnerId(AuthType.STAFF_CLIENT, staff.getId());
			AuthUtil.deleteResListByOwnerId(AuthType.STAFF_RULE, staff.getId());
			
			//删除员工，需要对员工追溯的数据进行取消
			CacheRetrospect.deleteStaff(staff.getId());
		}catch(Exception e){
			log.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "true";
	}

	/**
	 * 获取多个员工的定位信息
	 * 
	 * @param adminName 公司管理员用户名
	 * @param adminPassword 公司管理员密码
	 * @param staffPhones 员工手机号，多个员工以逗号分开  13438764746,13438764748,13438764749
	 * @param second 获取最近second秒的定位，没有则进行即时定位；second为0时直接进行即时定位
	 * @param responseHttpUrl 定位结果通过HTTP URL返回
	 * 返回格式: responseHttpUrl?time=时间如(20120514160059)&lng=经度如(121.600939)&lat=纬度如(30.03622949)&precision=精度如(19)&height=海拔如(54)&direction=方向如(90)&speed=速度如(0)&addr=地址如(北京市海淀区)
	 *
	 * @return true:定位操作成功;其它:错误的提示信息
	 */
	public String getStaffsLocation(String adminName,String adminPassword,String staffPhones,long second,String responseHttpUrl){
		if(StringUtils.isBlank(adminName)){
			return "用户名不能为空";
		}
		if(StringUtils.isBlank(adminPassword)){
			return "密码不能为空";
		}
		if(StringUtils.isBlank(staffPhones)){
			return "员工手机号不能为空";
		}
		if(StringUtils.isBlank(responseHttpUrl)){
			return "HTTP URL不能为空";
		}

		try{
			String companyId = WqServiceDAO.getCompanyId(adminName, adminPassword, publicDAO);
			if(companyId == null){
				return "请检查用户是否是公司管理员，密码是否正确。";
			}
			String[] phones = staffPhones.split(",");
			for(String phone : phones){
				String staffId = DataCache.phoneStaffIdMap.get(phone);
				WqStaffInfo staff = DataCache.staffMap.get(staffId);
				if(staff == null || !companyId.equals(staff.getCompanyId())){
					return "员工不存在。";
				}
				if(second > 0){
					DcGpsReal location = DataCache.staffPostionMap.get(staffId);
					long s = DateUtil.getBetweenSecond(location.getReportTime(), DateUtil.getCurDate());
					if(s <= second){
						sendLocationToHttp(location,responseHttpUrl);
						return "true";
					}
				}
				staffIdUrlMap.put(staffId, responseHttpUrl);
				//发送定位请求
				CmdUtil.sendLcsNowCmd(staff.getMobileType(),staff.getMobileNumber(),staff.getFixModel());
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "true";
	}
	
	public static void sendLocationToHttp(String staffId,DcGpsReal location){
		String url = staffIdUrlMap.remove(staffId);
		if(url != null){
			sendLocationToHttp(location,url);
		}
	}
	
	private static void sendLocationToHttp(DcGpsReal location,String responseHttpUrl){
		HttpURLConnection httpConnection = null;
		try {
			//responseHttpUrl?time=时间&lng=经度&lat=纬度&precision=精度&height=海拔&direction=方向&speed=速度&addr=地址
			StringBuilder sb = new StringBuilder(responseHttpUrl);
			sb.append("?time=").append(location.getReportTime());
			sb.append("&lng=").append(location.getLogit());
			sb.append("&lat=").append(location.getLat());
			sb.append("&precision=").append(location.getPrecision());
			sb.append("&height=").append(location.getHeight());
			sb.append("&direction=").append(location.getAngle());
			sb.append("&speed=").append(location.getSpeed());
			sb.append("&addr=").append(location.getLocDesc());
			
			URL url = new URL(sb.toString());
			httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setDoInput(true);
			httpConnection.setReadTimeout(10000);
			httpConnection.setDoOutput(true);
			httpConnection.connect();
			
			OutputStream os = httpConnection.getOutputStream();
			os.flush();
			os.close();
			
//			InputStream in = httpConnection.getInputStream();
//			in.close();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			if(httpConnection!=null){
				httpConnection.disconnect();
			}
		}
	}

	/**
	 * 通过地址，查询包括该地址位置的电子围栏信息
	 * @param userName 登录系统的用户名
	 * @param password 登录系统的密码
	 * @param addr 查询的地址
	 * @param customer 查询的客户名称
	 * @return	查询异常：-1
	 * 			用户登录失败：0
	 * 			查询无数据：1
	 * 			有数据：区域名称|区域编号|网点编号|网点名称|围栏类型|经纬度|半径
	 * 			围栏类型:1矩形；2多边形；3圆形
	 * 			经纬度:lng1,lat1;lng2,lat2...
	 * 			半径:当围栏类型是圆形有值，否则为0
	 */
	public String getRegionByAddr(String userName, String password, String addr, String customer){
		try {
			LoginService ls = (LoginService)AppUtil.getBean("loginService");
			WqUserInfo user = ls.findUser(userName, password, null);
			if(user == null) {
				return "0";
			}
			HebService service = (HebService)AppUtil.getBean("hebService");
			Object[] info = service.findEleFence2(addr, customer, user);
			if(info == null) {
				return "1";
			}
			WqEleFence ele = (WqEleFence) info[0];
			WqMapRegion region = (WqMapRegion) info[1];
			
			StringBuilder sb = new StringBuilder();
			sb.append(ele.getName()).append("|").append(ele.getHebAreaNo()).append("|").append(ele.getHebBranchNo()).append("|").append(ele.getHebBranchName()).append("|");
			sb.append(region.getType()).append("|").append(region.getPoints()).append("|").append(region.getRadius()==null?0:region.getRadius());
			
			return sb.toString();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return "-1";
		}
	}
}

