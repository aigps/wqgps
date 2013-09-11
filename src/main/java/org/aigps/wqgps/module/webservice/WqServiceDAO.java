
package org.sunleads.module.webservice;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.entity.WqUserInfo;

public class WqServiceDAO{

	public final static Log log = LogFactory.getLog(WqServiceDAO.class);
	
	public static boolean existUserName(String adminName,PublicDAO publicDAO){
		List<WqUserInfo> users = publicDAO.find("from WqUserInfo where userName=? ", adminName);
		if(users!=null && !users.isEmpty()){
			return true;
		}
		return false;
	}
	
	public static WqUserInfo getUser(String adminName,String password,PublicDAO publicDAO){
		List<WqUserInfo> users = publicDAO.find("from WqUserInfo where userName=? and password=?", adminName, password);
		if(users!=null && !users.isEmpty()){
			return users.get(0);
		}
		return null;
	}
	
	public static boolean existCompany(String companyName,PublicDAO publicDAO){
		List<WqCompanyInfo> companys = publicDAO.find("from WqCompanyInfo where name=? ", companyName);
		if(companys!=null && !companys.isEmpty()){
			return true;
		}
		return false;
	}
	
	public static String getCompanyId(String adminName,String password,PublicDAO publicDAO){
		List<WqUserInfo> users = publicDAO.find("from WqUserInfo where userName=? and password=?", adminName, password);
		if(users!=null && !users.isEmpty()){
			WqUserInfo admin = users.get(0);
			if(admin.getIsAdmin()){
				return admin.getCompanyId();
			}
		}
		return null;
	}
	
	public static WqStaffInfo getStaff(String staffPhone,PublicDAO publicDAO){
		List<WqStaffInfo> staffs = publicDAO.find("from WqStaffInfo where mobileNumber=? and isEnable=?", staffPhone, true);
		if(staffs!=null && !staffs.isEmpty()){
			return staffs.get(0);
		}
		return null;
	}
}

