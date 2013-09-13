package org.aigps.wqgps.common.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "DC_CHINA_AREA")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DcChinaArea  implements java.io.Serializable {

	private static final long serialVersionUID = -2222434348L;;

	private String zcode;
	private String prov;
	private String city;
	private String town;
	private BigDecimal longitude;
	private BigDecimal latitude;
	private BigDecimal sort;//ÅÅÐò×Ö¶Î
	
	private List<DcChinaArea> children;
	private DcChinaArea parent;
	private String name;
	@Id
	public String getZcode() {
		return zcode;
	}
	public void setZcode(String zcode) {
		this.zcode = zcode;
	}
	public String getProv() {
		return prov;
	}
	public void setProv(String prov) {
		this.prov = prov;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public BigDecimal getLongitude() {
		return longitude;
	}
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	public BigDecimal getLatitude() {
		return latitude;
	}
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
	
	public BigDecimal getSort() {
		return sort;
	}
	public void setSort(BigDecimal sort) {
		this.sort = sort;
	}
	
	@Transient
	public List<DcChinaArea> getChildren() {
		return children;
	}
	public void setChildren(List<DcChinaArea> children) {
		this.children = children;
	}
	@Transient
	public DcChinaArea getParent() {
		return parent;
	}
	public void setParent(DcChinaArea parent) {
		this.parent = parent;
	}
	@Transient
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Transient
	public String getFullName() {
		String fullName = prov;
		if(city!=null){
			fullName = fullName+city;
		}
		if(town!=null){
			fullName = fullName+town;
		}
		return fullName;
	}
}
