package org.aigps.wqgps.common.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "WQ_REGION_VISIT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqRegionVisit implements java.io.Serializable {
	private static final long serialVersionUID = 198572L;

	// Fields
	private String staffId;
	private String regionId;
	private String enterTime;
	private String leaveTime;
	private Long stayLong;
	private String companyId;
	private String standby1;
	private String standby2;
	private String standby3;

	/** default constructor */
	public WqRegionVisit() {
	}

	// Property accessors
	@Id
	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(String enterTime) {
		this.enterTime = enterTime;
	}

	public String getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(String leaveTime) {
		this.leaveTime = leaveTime;
	}

	public Long getStayLong() {
		return stayLong;
	}

	public void setStayLong(Long stayLong) {
		this.stayLong = stayLong;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getStandby1() {
		return standby1;
	}

	public void setStandby1(String standby1) {
		this.standby1 = standby1;
	}

	public String getStandby2() {
		return standby2;
	}

	public void setStandby2(String standby2) {
		this.standby2 = standby2;
	}

	public String getStandby3() {
		return standby3;
	}

	public void setStandby3(String standby3) {
		this.standby3 = standby3;
	}

}