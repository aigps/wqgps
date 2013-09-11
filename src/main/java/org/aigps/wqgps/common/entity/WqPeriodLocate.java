package org.sunleads.common.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "WQ_PERIOD_LOCATE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqPeriodLocate implements java.io.Serializable {
	private static final long serialVersionUID = 335333999992L;

	// Fields
	private String id;
	private String staffId;
	private String standby1;
	private String endTime;
	private String startTime;
	private String execTime;
	private String state;
	private BigDecimal locateInterval;
	private String companyId;
	private String standby2;
	private BigDecimal durationLong;
	private String userId;
	private String standby3;

	/** default constructor */
	public WqPeriodLocate() {
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

	public String getStaffId() {
		return this.staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getStandby1() {
		return this.standby1;
	}

	public void setStandby1(String standby1) {
		this.standby1 = standby1;
	}

	public String getEndTime() {
		return this.endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public BigDecimal getLocateInterval() {
		return this.locateInterval;
	}

	public void setLocateInterval(BigDecimal locateInterval) {
		this.locateInterval = locateInterval;
	}

	public String getStandby2() {
		return this.standby2;
	}

	public void setStandby2(String standby2) {
		this.standby2 = standby2;
	}

	public BigDecimal getDurationLong() {
		return this.durationLong;
	}

	public void setDurationLong(BigDecimal durationLong) {
		this.durationLong = durationLong;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStandby3() {
		return this.standby3;
	}

	public void setStandby3(String standby3) {
		this.standby3 = standby3;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getExecTime() {
		return execTime;
	}

	public void setExecTime(String execTime) {
		this.execTime = execTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

}