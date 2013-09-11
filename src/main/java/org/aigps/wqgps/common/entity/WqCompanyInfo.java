package org.sunleads.common.entity;

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
@Table(name = "WQ_COMPANY_INFO")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqCompanyInfo implements java.io.Serializable {
	private static final long serialVersionUID = -3444444444833692L;
	
	// Fields
	private String id;
	private String name;
	private String addr;
	private String linkman;
	private String standby1;
	private String contactNumber;
	private String regionIds;
	private BigDecimal clientRange;
	private BigDecimal clientVisitLong;
	private String companyUrl;
	private String copyright;
	private String remark;
	private String contactInfo;
	private String urlFlag;
	private Boolean isEnable;
	private BigDecimal centerLng;
	private Date createTime;
	private BigDecimal centerLat;
	private String creater;
	private BigDecimal centerZoom;
	private String standby2;
	private String standby3;
	private Boolean isShowAllStaff;
	private String location;
	private String trade;
	private String tradeRoleId;
	private BigDecimal personNumber;
	private String fixModels;
	private Boolean useInvalidLoc;
	private Long retrospectLong;
	private String state;
	private Boolean isSmsNotice;
	

	/** default constructor */
	public WqCompanyInfo() {
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

	public String getAddr() {
		return this.addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getLinkman() {
		return this.linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
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

	public String getRegionIds() {
		return this.regionIds;
	}

	public void setRegionIds(String regionIds) {
		this.regionIds = regionIds;
	}

	public BigDecimal getClientRange() {
		return this.clientRange;
	}

	public void setClientRange(BigDecimal clientRange) {
		this.clientRange = clientRange;
	}

	public BigDecimal getClientVisitLong() {
		return this.clientVisitLong;
	}

	public void setClientVisitLong(BigDecimal clientVisitLong) {
		this.clientVisitLong = clientVisitLong;
	}

	public String getCompanyUrl() {
		return this.companyUrl;
	}

	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
	}

	public String getCopyright() {
		return this.copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getContactInfo() {
		return this.contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public Boolean getIsEnable() {
		return this.isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	public BigDecimal getCenterLng() {
		return this.centerLng;
	}

	public void setCenterLng(BigDecimal centerLng) {
		this.centerLng = centerLng;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public BigDecimal getCenterLat() {
		return this.centerLat;
	}

	public void setCenterLat(BigDecimal centerLat) {
		this.centerLat = centerLat;
	}

	public String getCreater() {
		return this.creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public BigDecimal getCenterZoom() {
		return this.centerZoom;
	}

	public void setCenterZoom(BigDecimal centerZoom) {
		this.centerZoom = centerZoom;
	}

	public String getStandby2() {
		return this.standby2;
	}

	public void setStandby2(String standby2) {
		this.standby2 = standby2;
	}

	public String getStandby3() {
		return this.standby3;
	}

	public void setStandby3(String standby3) {
		this.standby3 = standby3;
	}

	public void setUrlFlag(String urlFlag) {
		this.urlFlag = urlFlag;
	}

	public String getUrlFlag() {
		return urlFlag;
	}

	public void setIsShowAllStaff(Boolean isShowAllStaff) {
		this.isShowAllStaff = isShowAllStaff;
	}

	public Boolean getIsShowAllStaff() {
		return isShowAllStaff;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTrade() {
		return trade;
	}

	public void setTrade(String trade) {
		this.trade = trade;
	}

	public BigDecimal getPersonNumber() {
		return personNumber;
	}

	public void setPersonNumber(BigDecimal personNumber) {
		this.personNumber = personNumber;
	}

	public String getFixModels() {
		return fixModels;
	}

	public void setFixModels(String fixModels) {
		this.fixModels = fixModels;
	}

	public void setUseInvalidLoc(Boolean useInvalidLoc) {
		this.useInvalidLoc = useInvalidLoc;
	}

	public Boolean getUseInvalidLoc() {
		return useInvalidLoc;
	}

	public void setRetrospectLong(Long retrospectLong) {
		this.retrospectLong = retrospectLong;
	}

	public Long getRetrospectLong() {
		return retrospectLong;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public void setIsSmsNotice(Boolean isSmsNotice) {
		this.isSmsNotice = isSmsNotice;
	}

	public Boolean getIsSmsNotice() {
		return isSmsNotice;
	}

	public void setTradeRoleId(String tradeRoleId) {
		this.tradeRoleId = tradeRoleId;
	}

	public String getTradeRoleId() {
		return tradeRoleId;
	}


}