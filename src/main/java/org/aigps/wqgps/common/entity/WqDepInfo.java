package org.aigps.wqgps.common.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "WQ_DEP_INFO")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqDepInfo implements java.io.Serializable {
	private static final long serialVersionUID = -2563811111111133692L;

	// Fields
	private String id;
	private String name;
	private String parentId;
	private String companyId;
	private String manager;
	private String standby1;
	private String contactNumber;
	private String startWorkTime;
	private String endWorkTime;
	private BigDecimal locateInterveal;
	private Date createTime;
	private String workWeekDays;
	private String signInStartTime;
	private String signInEndTime;
	private String creater;
	private Boolean isEnable;
	private String standby2;
	private String remark;
	private String standby3;
	private String signOutStartTime;
	private String signOutEndTime;
	private Boolean isCompanySignIn;
	private Boolean isCompanySignOut;
	private Boolean isClientSignIn;
	private Boolean isClientSignOut;
	

	/** default constructor */
	public WqDepInfo() {
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getManager() {
		return this.manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getStandby1() {
		return this.standby1;
	}

	public void setStandby1(String standby1) {
		this.standby1 = standby1;
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

	public String getEndWorkTime() {
		return this.endWorkTime;
	}

	public void setEndWorkTime(String endWorkTime) {
		this.endWorkTime = endWorkTime;
	}

	public BigDecimal getLocateInterveal() {
		return this.locateInterveal;
	}

	public void setLocateInterveal(BigDecimal locateInterveal) {
		this.locateInterveal = locateInterveal;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getWorkWeekDays() {
		return this.workWeekDays;
	}

	public void setWorkWeekDays(String workWeekDays) {
		this.workWeekDays = workWeekDays;
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

	public String getCreater() {
		return this.creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Boolean getIsEnable() {
		return this.isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	public String getStandby2() {
		return this.standby2;
	}

	public void setStandby2(String standby2) {
		this.standby2 = standby2;
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

	public Boolean getIsCompanySignIn() {
		return isCompanySignIn;
	}

	public void setIsCompanySignIn(Boolean isCompanySignIn) {
		this.isCompanySignIn = isCompanySignIn;
	}

	public Boolean getIsCompanySignOut() {
		return isCompanySignOut;
	}

	public void setIsCompanySignOut(Boolean isCompanySignOut) {
		this.isCompanySignOut = isCompanySignOut;
	}

	public Boolean getIsClientSignIn() {
		return isClientSignIn;
	}

	public void setIsClientSignIn(Boolean isClientSignIn) {
		this.isClientSignIn = isClientSignIn;
	}

	public Boolean getIsClientSignOut() {
		return isClientSignOut;
	}

	public void setIsClientSignOut(Boolean isClientSignOut) {
		this.isClientSignOut = isClientSignOut;
	}

}