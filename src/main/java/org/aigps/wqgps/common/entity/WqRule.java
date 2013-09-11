package org.sunleads.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "WQ_RULE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqRule implements java.io.Serializable {
	private static final long serialVersionUID = 98833272L;

	// Fields
	private String id;
	private String eleFenceId;
	private String name;
	private String standby1;
	private String startDate;
	private String endDate;
	private String standby2;
	private String startTime;
	private String endTime;
	private String weekDays;
	private String type;
	private Date createTime;
	private String remark;
	private String creater;
	private String monitorPhone2;
	private String monitorPhone1;
	private String companyId;
	private Boolean isEnable;
	private String standby3;

	/** default constructor */
	public WqRule() {
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

	public String getEleFenceId() {
		return this.eleFenceId;
	}

	public void setEleFenceId(String eleFenceId) {
		this.eleFenceId = eleFenceId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStandby1() {
		return this.standby1;
	}

	public void setStandby1(String standby1) {
		this.standby1 = standby1;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStandby2() {
		return this.standby2;
	}

	public void setStandby2(String standby2) {
		this.standby2 = standby2;
	}

	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return this.endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getWeekDays() {
		return this.weekDays;
	}

	public void setWeekDays(String weekDays) {
		this.weekDays = weekDays;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreater() {
		return this.creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public String getMonitorPhone2() {
		return this.monitorPhone2;
	}

	public void setMonitorPhone2(String monitorPhone2) {
		this.monitorPhone2 = monitorPhone2;
	}

	public String getMonitorPhone1() {
		return this.monitorPhone1;
	}

	public void setMonitorPhone1(String monitorPhone1) {
		this.monitorPhone1 = monitorPhone1;
	}

	public Boolean getIsEnable() {
		return this.isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	public String getStandby3() {
		return this.standby3;
	}

	public void setStandby3(String standby3) {
		this.standby3 = standby3;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyId() {
		return companyId;
	}

}