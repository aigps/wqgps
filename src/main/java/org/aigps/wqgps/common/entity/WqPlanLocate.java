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
@Table(name = "WQ_PLAN_LOCATE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqPlanLocate implements java.io.Serializable {
	private static final long serialVersionUID = 1117466188833472L;

	// Fields
	private String id;
	private String name;
	private String planLocateTime;
	private Date createTime;
	private String companyId;
	private String userId;
	private Boolean processFlag;
	private String standby1;
	private String standby2;
	private String standby3;
	
	/** default constructor */
	public WqPlanLocate() {
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

	public String getPlanLocateTime() {
		return this.planLocateTime;
	}

	public void setPlanLocateTime(String planLocateTime) {
		this.planLocateTime = planLocateTime;
	}

	public String getStandby1() {
		return this.standby1;
	}

	public void setStandby1(String standby1) {
		this.standby1 = standby1;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getStandby2() {
		return this.standby2;
	}

	public void setStandby2(String standby2) {
		this.standby2 = standby2;
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

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyId() {
		return companyId;
	}


	public Boolean getProcessFlag() {
		return processFlag;
	}

	public void setProcessFlag(Boolean processFlag) {
		this.processFlag = processFlag;
	}
}