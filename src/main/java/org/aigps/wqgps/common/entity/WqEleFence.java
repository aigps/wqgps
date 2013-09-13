package org.aigps.wqgps.common.entity;

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
@Table(name = "WQ_ELE_FENCE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqEleFence implements java.io.Serializable {
	public static String TYPE_DIRECTORY = "0";//Ä¿Â¼
	public static String TYPE_ELE_FENCE = "1";//Î§À¸
	private static final long serialVersionUID = -15613433333332L;

	// Fields
	private String id;
	private String standby1;
	private String name;
	private String standby2;
	private String parentId;
	private String type;
	private String regionId;
	private String areaId;
	private String companyId;
	private Date createTime;
	private String remark;
	private String creater;
	private Boolean isEnable;
	private String standby3;
	private String hebBranchNo;
	private String hebBranchName;
	private String hebAreaNo;

	private String areaName;
	
	/** default constructor */
	public WqEleFence() {
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

	public String getStandby1() {
		return this.standby1;
	}

	public void setStandby1(String standby1) {
		this.standby1 = standby1;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStandby2() {
		return this.standby2;
	}

	public void setStandby2(String standby2) {
		this.standby2 = standby2;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRegionId() {
		return this.regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getCompanyId() {
		return this.companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
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

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	@Transient
	public String getAreaName() {
		return areaName;
	}

	public String getHebBranchNo() {
		return hebBranchNo;
	}

	public void setHebBranchNo(String hebBranchNo) {
		this.hebBranchNo = hebBranchNo;
	}

	public String getHebBranchName() {
		return hebBranchName;
	}

	public void setHebBranchName(String hebBranchName) {
		this.hebBranchName = hebBranchName;
	}

	public String getHebAreaNo() {
		return hebAreaNo;
	}

	public void setHebAreaNo(String hebAreaNo) {
		this.hebAreaNo = hebAreaNo;
	}

}