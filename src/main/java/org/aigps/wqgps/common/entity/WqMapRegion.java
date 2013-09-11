package org.sunleads.common.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "WQ_MAP_REGION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqMapRegion implements java.io.Serializable {
	private static final long serialVersionUID = 1563888833682L;

	// Fields
	private String id;
	private String name;
	private String standby1;
	private String points;
	private String standby2;
	private BigDecimal radius;
	private BigDecimal zoom;
	private String type;
	private String standby3;
	private String companyId;
	private String creater;
	private Date createTime;
	
	/** default constructor */
	public WqMapRegion() {
	}

	// Property accessors
	@Id
//	@GeneratedValue(generator="hibernate-uuid")
//	@GenericGenerator(name = "hibernate-uuid", strategy = "uuid")
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

	public String getStandby1() {
		return this.standby1;
	}

	public void setStandby1(String standby1) {
		this.standby1 = standby1;
	}

	public String getPoints() {
		return this.points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public String getStandby2() {
		return this.standby2;
	}

	public void setStandby2(String standby2) {
		this.standby2 = standby2;
	}

	public BigDecimal getRadius() {
		return this.radius;
	}

	public void setRadius(BigDecimal radius) {
		this.radius = radius;
	}

	public BigDecimal getZoom() {
		return this.zoom;
	}

	public void setZoom(BigDecimal zoom) {
		this.zoom = zoom;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStandby3() {
		return this.standby3;
	}

	public void setStandby3(String standby3) {
		this.standby3 = standby3;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}