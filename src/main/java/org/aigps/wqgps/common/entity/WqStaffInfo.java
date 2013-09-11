package org.sunleads.common.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "WQ_STAFF_INFO")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqStaffInfo implements java.io.Serializable {
	private static final long serialVersionUID = 15857452L;

	// Fields
	private String id;
	private String staffNo;
	private String depId;
	private String cnName;
	private String enName;
	private String standby1;
	private String msid;
	private String mobileNumber;
	private String mobileType;
	private String standby2;
	private String contactNumber;
	private String startWorkTime;
	private String activateState;
	private String endWorkTime;
	private Boolean isEnable;
	private String creater;
	private String workWeekDays;
	private String remark;
	private String companyId;
	private String standby3;
	private String homeAddress;
	private BigDecimal locateInterval;
	private String intervalUnit;
	private Date createTime;
	private String signInStartTime;
	private String signInEndTime;
	private String signOutStartTime;
	private String signOutEndTime;
	private Boolean isCompanySignIn;
	private Boolean isCompanySignOut;
	private Boolean isClientSignIn;
	private Boolean isClientSignOut;
	private String fixModel;
	
	/** default constructor */
	public WqStaffInfo() {
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="hibernate-uuid")
	@GenericGenerator(name = "hibernate-uuid", strategy = "uuid")
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStaffNo() {
		return this.staffNo;
	}

	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}

	public String getDepId() {
		return this.depId;
	}

	public void setDepId(String depId) {
		this.depId = depId;
	}

	public String getCnName() {
		return this.cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getEnName() {
		return this.enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getStandby1() {
		return this.standby1;
	}

	public void setStandby1(String standby1) {
		this.standby1 = standby1;
	}

	public String getMsid() {
		return this.msid;
	}

	public void setMsid(String msid) {
		this.msid = msid;
	}

	public String getMobileNumber() {
		return this.mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public void setMobileType(String mobileType) {
		this.mobileType = mobileType;
	}

	public String getMobileType() {
		return mobileType;
	}
	
	public String getStandby2() {
		return this.standby2;
	}

	public void setStandby2(String standby2) {
		this.standby2 = standby2;
	}

	public String getContactNumber() {
		return this.contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getStartWorkTime() {
		return this.startWorkTime;
	}

	public void setStartWorkTime(String startWorkTime) {
		this.startWorkTime = startWorkTime;
	}

	public String getActivateState() {
		return activateState;
	}

	public void setActivateState(String activateState) {
		this.activateState = activateState;
	}

	public String getEndWorkTime() {
		return this.endWorkTime;
	}

	public void setEndWorkTime(String endWorkTime) {
		this.endWorkTime = endWorkTime;
	}

	public Boolean getIsEnable() {
		return this.isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	public String getCreater() {
		return this.creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public String getWorkWeekDays() {
		return this.workWeekDays;
	}

	public void setWorkWeekDays(String workWeekDays) {
		this.workWeekDays = workWeekDays;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStandby3() {
		return this.standby3;
	}

	public void setStandby3(String standby3) {
		this.standby3 = standby3;
	}

	public String getHomeAddress() {
		return this.homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public BigDecimal getLocateInterval() {
		return this.locateInterval;
	}

	public void setLocateInterval(BigDecimal locateInterval) {
		this.locateInterval = locateInterval;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getSignInStartTime() {
		return this.signInStartTime;
	}

	public void setSignInStartTime(String signInStartTime) {
		this.signInStartTime = signInStartTime;
	}

	public String getSignInEndTime() {
		return this.signInEndTime;
	}

	public void setSignInEndTime(String signInEndTime) {
		this.signInEndTime = signInEndTime;
	}

	public String getSignOutStartTime() {
		return this.signOutStartTime;
	}

	public void setSignOutStartTime(String signOutStartTime) {
		this.signOutStartTime = signOutStartTime;
	}

	public String getSignOutEndTime() {
		return this.signOutEndTime;
	}

	public void setSignOutEndTime(String signOutEndTime) {
		this.signOutEndTime = signOutEndTime;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public Boolean getIsCompanySignIn() {
		return isCompanySignIn==null?false:isCompanySignIn;
	}

	public void setIsCompanySignIn(Boolean isCompanySignIn) {
		this.isCompanySignIn = isCompanySignIn;
	}

	public Boolean getIsCompanySignOut() {
		return isCompanySignOut==null?false:isCompanySignOut;
	}

	public void setIsCompanySignOut(Boolean isCompanySignOut) {
		this.isCompanySignOut = isCompanySignOut;
	}

	public Boolean getIsClientSignIn() {
		return isClientSignIn==null?false:isClientSignIn;
	}

	public void setIsClientSignIn(Boolean isClientSignIn) {
		this.isClientSignIn = isClientSignIn;
	}

	public Boolean getIsClientSignOut() {
		return isClientSignOut==null?false:isClientSignOut;
	}

	public void setIsClientSignOut(Boolean isClientSignOut) {
		this.isClientSignOut = isClientSignOut;
	}

	public void setFixModel(String fixModel) {
		this.fixModel = fixModel;
	}

	public String getFixModel() {
		return fixModel;
	}

	public void setIntervalUnit(String intervalUnit) {
		this.intervalUnit = intervalUnit;
	}

	public String getIntervalUnit() {
		return intervalUnit;
	}

	@Transient
	public long getGpsInterval(){
		if(locateInterval==null){
			return 600l;//ƒ¨»œ10∑÷÷”
		}
		if("1".equals(intervalUnit)){//√Î
			return locateInterval.longValue();
		}else{//∑÷
			return locateInterval.longValue() * 60;
		}
	}

}