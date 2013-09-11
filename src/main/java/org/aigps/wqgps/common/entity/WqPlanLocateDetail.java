package org.sunleads.common.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.sunleads.common.util.StrUtil;

@Entity
@Table(name = "WQ_PLAN_LOCATE_DETAIL")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqPlanLocateDetail implements java.io.Serializable {
	private static final long serialVersionUID = 1563812322L;

	// Fields
	private String id;
	private String planLocateId;
	private String staffId;
	private String reportTime;
	private BigDecimal logit;
	private BigDecimal lat;
	private BigDecimal logitOffset;
	private BigDecimal latOffset;
	private String speed;
	private String angle;
	private String height;
	private String locDesc;
	private String companyId;
	private String standby1;
	private String standby2;
	private String standby3;
	
	private String direction;//中文方向

	/** default constructor */
	public WqPlanLocateDetail() {
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

	public String getPlanLocateId() {
		return this.planLocateId;
	}

	public void setPlanLocateId(String planLocateId) {
		this.planLocateId = planLocateId;
	}

	public String getStaffId() {
		return this.staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	

	public String getReportTime() {
		return reportTime;
	}

	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}

	public String getStandby1() {
		return this.standby1;
	}

	public void setStandby1(String standby1) {
		this.standby1 = standby1;
	}



	public String getStandby2() {
		return this.standby2;
	}

	public void setStandby2(String standby2) {
		this.standby2 = standby2;
	}

	public BigDecimal getLat() {
		return this.lat;
	}

	public void setLat(BigDecimal lat) {
		this.lat = lat;
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

	public void setLocDesc(String locDesc) {
		this.locDesc = locDesc;
	}

	public String getLocDesc() {
		return locDesc;
	}

	public BigDecimal getLogit() {
		return logit;
	}

	public void setLogit(BigDecimal logit) {
		this.logit = logit;
	}

	public BigDecimal getLogitOffset() {
		return logitOffset;
	}

	public void setLogitOffset(BigDecimal logitOffset) {
		this.logitOffset = logitOffset;
	}

	public BigDecimal getLatOffset() {
		return latOffset;
	}

	public void setLatOffset(BigDecimal latOffset) {
		this.latOffset = latOffset;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getAngle() {
		return angle;
	}

	public void setAngle(String angle) {
		this.angle = angle;
		if(StringUtils.isNotBlank(this.angle)){
			direction = StrUtil.getDrivePos(this.angle);
		}
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	@Transient
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	
	
}