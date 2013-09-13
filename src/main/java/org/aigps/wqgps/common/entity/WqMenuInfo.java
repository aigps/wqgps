package org.aigps.wqgps.common.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "WQ_MENU_INFO")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqMenuInfo implements java.io.Serializable {
	private static final long serialVersionUID = 78833672L;

	// Fields
	private String id;
	private String name;
	private String link;
	private String icon;
	private String standby1;
	private BigDecimal sort;
	private String parentId;
	private String standby2;
	private String remark;
	private String standby3;

	/** default constructor */
	public WqMenuInfo() {
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

	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getStandby1() {
		return this.standby1;
	}

	public void setStandby1(String standby1) {
		this.standby1 = standby1;
	}

	public BigDecimal getSort() {
		return this.sort;
	}

	public void setSort(BigDecimal sort) {
		this.sort = sort;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
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

}