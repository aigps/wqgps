
package org.sunleads.module.webservice;

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
import org.sunleads.common.entity.DcGpsReal;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqDepInfo;
import org.sunleads.common.entity.WqEleFence;
import org.sunleads.common.entity.WqMapRegion;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.DateUtil;
import org.sunleads.module.admin.service.AdminService;
import org.sunleads.module.heb.HebService;
import org.sunleads.module.login.service.LoginService;
import org.sunleads.socket.CmdUtil;
import org.sunleads.timing.CacheDepMap;
import org.sunleads.timing.CacheRetrospect;
import org.sunleads.timing.CacheStaffMap;

@WebService(endpointInterface = "org.sunleads.module.webservice.WqService")
@Component
@Transactional
public class WqServiceImpl implements WqService {

	public final static Log log = LogFactory.getLog(AdminService.class);

	//Ա���ͷ��ص�URL�Ĺ�ϵ
	private static Map<String,String> staffIdUrlMap = new ConcurrentHashMap<String,String>();
	
	private PublicDAO publicDAO;
	
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}

	/**
	 * ���������޸Ĺ�˾
	 * 
	 * @param adminName			��˾����Ա�û���
	 * @param adminPassword��	��˾����Ա����
	 * @param companyName��		��˾����
	 * @param companyLinkMan��	��˾��ϵ��
	 * @param companyPhone��		��˾�绰()
	 * 
	 * @return true:�������޸ĳɹ�;����:�������ʾ��Ϣ
	 */
	public String createOrUpdateCompany(String adminName,String adminPassword,String companyName,String companyLinkMan,String companyPhone){
		if(StringUtils.isBlank(adminName)){
			return "�û�������Ϊ�ա�";
		}
		if(StringUtils.isBlank(adminPassword)){
			return "���벻��Ϊ�ա�";
		}
		if(StringUtils.isBlank(companyName)){
			return "��˾���Ʋ���Ϊ�ա�";
		}
		if(StringUtils.isBlank(companyLinkMan)){
			return "��˾��ϵ�˲���Ϊ�ա�";
		}
		if(StringUtils.isBlank(companyPhone)){
			return "��˾�绰����Ϊ�ա�";
		}
		try{
			boolean isNewAdmin = !WqServiceDAO.existUserName(adminName,publicDAO);
			WqCompanyInfo company = null;
			WqUserInfo admin = null;
			if(isNewAdmin == false){
				admin = WqServiceDAO.getUser(adminName, adminPassword, publicDAO);
				if(admin == null){
					return "�û����Ѿ���ʹ�ã��뻻���������";
				}else if(!admin.getIsAdmin()){
					return "���û����ǹ�˾����Ա����Ȩ���޸Ĺ�˾��Ϣ��";
				}
				company = DataCache.companyInfoMap.get(admin.getCompanyId());
			}
			if(company == null){
				if(WqServiceDAO.existCompany(companyName, publicDAO)){
					return "��˾�����Ѿ���ʹ�ã��뻻��Ĺ�˾���ơ�";
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
				company.setState("1");//��ʽʹ��
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
	 * ��������Ա��
	 * 
	 * @param adminName 		��˾����Ա�û���
	 * @param adminPassword��	��˾����Ա����
	 * @param staffName��		Ա������
	 * @param staffPhone��		Ա���ֻ���
	 * @param startWorkTime��	�ϰ�ʱ��(09:00)
	 * @param endWorkTime��		�°�ʱ��(18:00)
	 * @param locateInterval��	��λ���(��)
	 * @param locateModel��		��λ��ʽ(0:MSA;1:GOOGLE;2:GPS;3:MSA->GPS;4:GPS->GOOGLE;5:GPS->MSA;6:GPS->MSA->GOOGLE;)
	 *
	 * @return true:�����ɹ�;����:�������ʾ��Ϣ
	 */
	public String createStaff(String adminName,String adminPassword,String staffName,String staffPhone,String startWorkTime,String endWorkTime,String locateInterval,String locateModel){
		if(StringUtils.isBlank(adminName)){
			return "�û�������Ϊ��";
		}
		if(StringUtils.isBlank(adminPassword)){
			return "���벻��Ϊ��";
		}
		if(StringUtils.isBlank(staffName)){
			return "Ա����������Ϊ��";
		}
		if(StringUtils.isBlank(staffPhone)){
			return "Ա���ֻ��Ų���Ϊ��";
		}
		if(StringUtils.isBlank(startWorkTime)){
			return "�ϰ�ʱ�䲻��Ϊ��";
		}
		if(!Pattern.matches("\\d{2}:\\d{2}", startWorkTime)){
			return "�ϰ�ʱ���ʽ����";
		}
		if(StringUtils.isBlank(endWorkTime)){
			return "�°�ʱ�䲻��Ϊ��";
		}
		if(!Pattern.matches("\\d{2}:\\d{2}", endWorkTime)){
			return "�°�ʱ���ʽ����";
		}
		if(StringUtils.isBlank(locateInterval)){
			return "��λ�������Ϊ��";
		}
		if(!Pattern.matches("[0-9]*", locateInterval)){
			return "��λ������������֣���λΪ�롣";
		}
		if(StringUtils.isBlank(locateModel)){
			return "��λ��ʽ����Ϊ��";
		}
		if(!Pattern.matches("[0-6]", locateModel)){
			return "��λ��ʽ��0��6";
		}
		try{
			String companyId = WqServiceDAO.getCompanyId(adminName, adminPassword, publicDAO);
			if(companyId == null){
				return "�����û��Ƿ��ǹ�˾����Ա�������Ƿ���ȷ��";
			}
			if(DataCache.phoneStaffIdMap.containsKey(staffPhone)){
				return "�ֻ����Ѿ���ʹ�á�";
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
	 * ɾ������Ա��
	 * @param adminName ��˾����Ա�û���
	 * @param adminPassword����˾����Ա����
	 * @param staffPhone ɾ����Ա���ֻ���
	 * @return true:�����ɹ�;����:�������ʾ��Ϣ
	 */
	public String deleteStaff(String adminName,String adminPassword,String staffPhone){
		if(StringUtils.isBlank(adminName)){
			return "�û�������Ϊ��";
		}
		if(StringUtils.isBlank(adminPassword)){
			return "���벻��Ϊ��";
		}
		if(StringUtils.isBlank(staffPhone)){
			return "Ա���ֻ��Ų���Ϊ��";
		}

		try{
			String companyId = WqServiceDAO.getCompanyId(adminName, adminPassword, publicDAO);
			if(companyId == null){
				return "�����û��Ƿ��ǹ�˾����Ա�������Ƿ���ȷ��";
			}
			WqStaffInfo staff = WqServiceDAO.getStaff(staffPhone, publicDAO);
			if(staff == null || !companyId.equals(staff.getCompanyId())){
				return "Ա�������ڡ�";
			}
			staff.setIsEnable(false);
			publicDAO.delete(staff);
			CacheStaffMap.deleteStaff(staff);
			
			//ɾ��Ա��Ȩ��
			AuthUtil.deleteResListByOwnerId(AuthType.STAFF_CLIENT, staff.getId());
			AuthUtil.deleteResListByOwnerId(AuthType.STAFF_RULE, staff.getId());
			
			//ɾ��Ա������Ҫ��Ա��׷�ݵ����ݽ���ȡ��
			CacheRetrospect.deleteStaff(staff.getId());
		}catch(Exception e){
			log.error(e.getMessage(), e);
			return e.getMessage();
		}
		return "true";
	}

	/**
	 * ��ȡ���Ա���Ķ�λ��Ϣ
	 * 
	 * @param adminName ��˾����Ա�û���
	 * @param adminPassword ��˾����Ա����
	 * @param staffPhones Ա���ֻ��ţ����Ա���Զ��ŷֿ�  13438764746,13438764748,13438764749
	 * @param second ��ȡ���second��Ķ�λ��û������м�ʱ��λ��secondΪ0ʱֱ�ӽ��м�ʱ��λ
	 * @param responseHttpUrl ��λ���ͨ��HTTP URL����
	 * ���ظ�ʽ: responseHttpUrl?time=ʱ����(20120514160059)&lng=������(121.600939)&lat=γ����(30.03622949)&precision=������(19)&height=������(54)&direction=������(90)&speed=�ٶ���(0)&addr=��ַ��(�����к�����)
	 *
	 * @return true:��λ�����ɹ�;����:�������ʾ��Ϣ
	 */
	public String getStaffsLocation(String adminName,String adminPassword,String staffPhones,long second,String responseHttpUrl){
		if(StringUtils.isBlank(adminName)){
			return "�û�������Ϊ��";
		}
		if(StringUtils.isBlank(adminPassword)){
			return "���벻��Ϊ��";
		}
		if(StringUtils.isBlank(staffPhones)){
			return "Ա���ֻ��Ų���Ϊ��";
		}
		if(StringUtils.isBlank(responseHttpUrl)){
			return "HTTP URL����Ϊ��";
		}

		try{
			String companyId = WqServiceDAO.getCompanyId(adminName, adminPassword, publicDAO);
			if(companyId == null){
				return "�����û��Ƿ��ǹ�˾����Ա�������Ƿ���ȷ��";
			}
			String[] phones = staffPhones.split(",");
			for(String phone : phones){
				String staffId = DataCache.phoneStaffIdMap.get(phone);
				WqStaffInfo staff = DataCache.staffMap.get(staffId);
				if(staff == null || !companyId.equals(staff.getCompanyId())){
					return "Ա�������ڡ�";
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
				//���Ͷ�λ����
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
			//responseHttpUrl?time=ʱ��&lng=����&lat=γ��&precision=����&height=����&direction=����&speed=�ٶ�&addr=��ַ
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
	 * ͨ����ַ����ѯ�����õ�ַλ�õĵ���Χ����Ϣ
	 * @param userName ��¼ϵͳ���û���
	 * @param password ��¼ϵͳ������
	 * @param addr ��ѯ�ĵ�ַ
	 * @param customer ��ѯ�Ŀͻ�����
	 * @return	��ѯ�쳣��-1
	 * 			�û���¼ʧ�ܣ�0
	 * 			��ѯ�����ݣ�1
	 * 			�����ݣ���������|������|������|��������|Χ������|��γ��|�뾶
	 * 			Χ������:1���Σ�2����Σ�3Բ��
	 * 			��γ��:lng1,lat1;lng2,lat2...
	 * 			�뾶:��Χ��������Բ����ֵ������Ϊ0
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

