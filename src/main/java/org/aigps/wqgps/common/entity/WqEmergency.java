package org.aigps.wqgps.common.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "WQ_EMERGENCY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqEmergency implements java.io.Serializable {
	private static final long serialVersionUID = 1199993472L;

	// Fields
	private String id;
	private String staffId;
	private String message;
	private String msgTime;
	private String sendTo;
	private String sendState;
	private String companyId;
	private String remark;
	
	/** default constructor */
	public WqEmergency() {
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
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(String msgTime) {
		this.msgTime = msgTime;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public String getSendTo() {
		return sendTo;
	}

	public void setSendState(String sendState) {
		this.sendState = sendState;
	}

	public String getSendState() {
		return sendState;
	}

}