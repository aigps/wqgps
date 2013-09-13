package org.aigps.wqgps.common.cache;

import java.util.HashMap;
import java.util.Map;

import org.aigps.wqgps.common.entity.WqCompanyInfo;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("session")
@Component
public class SessionData implements java.io.Serializable {
	
	private static final long serialVersionUID = -3438434348L;

	//�û�����
	private WqUserInfo userInfo;
	
	//�û����ڵĹ�˾����
	private WqCompanyInfo company;
	
	//�û���¼��IP��ַ
	private String loginIp;

	//��¼�Ĺ�˾
	private String companyFlag;

	//�����ⲿ���Ӵ����������еĲ���
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
