package org.sunleads.common.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqUserInfo;

@Scope("session")
@Component
public class SessionData implements java.io.Serializable {
	
	private static final long serialVersionUID = -3438434348L;

	//用户对象
	private WqUserInfo userInfo;
	
	//用户所在的公司对象
	private WqCompanyInfo company;
	
	//用户登录的IP地址
	private String loginIp;

	//登录的公司
	private String companyFlag;

	//保存外部链接传进来的所有的参数
	private Map<String,Object> outerParamMap = new HashMap<String,Object>();
	
//===============================================================================================

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setUserInfo(WqUserInfo wqUserInfo) {
		this.userInfo = wqUserInfo;
	}

	public WqUserInfo getUserInfo() {
		return userInfo;
	}

	public void setCompany(WqCompanyInfo company) {
		this.company = company;
	}

	public WqCompanyInfo getCompany() {
		return company;
	}

	public String getCompanyFlag() {
		return companyFlag;
	}

	public void setCompanyFlag(String companyFlag) {
		this.companyFlag = companyFlag;
	}

	public Map<String,Object> getOuterParamMap() {
		return outerParamMap;
	}

	public void setOuterParamMap(Map<String,Object> outerParamMap) {
		this.outerParamMap = outerParamMap;
	}

}
