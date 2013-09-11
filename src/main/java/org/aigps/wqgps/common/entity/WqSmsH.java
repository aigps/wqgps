package org.sunleads.common.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;


//CREATE TABLE WQ_SMS_H (
//       id                   VARCHAR2(32) NOT NULL,
//       staffId              VARCHAR2(32) NULL,
//       SendTime             VARCHAR2(20) NULL,
//       sms_content          NVARCHAR2(500) NULL,
//       sender               VARCHAR2(32) NULL,
//       state                VARCHAR2(2) NULL,
//       phone                VARCHAR2(32) NULL,
//       PRIMARY KEY (id)
//);
@Entity
@Table(name = "WQ_SMS_H")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqSmsH  implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 96532L;
	public static final String WAIT_SEND = "02";
	public static final String SEND_SUCCESS = "01";
	public static final String SEND_FAIL = "00";
	private String id;
	private String staffid;
	private String sendtime;
	private String smsContent;
	private String sender;
	private String state;
	private String phone;
	private String companyId;
	private String type;
	private String badging;
	
	@Id
	@GeneratedValue(generator="hibernate-uuid")
	@GenericGenerator(name = "hibernate-uuid", strategy = "uuid")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getStaffid() {
		return staffid;
	}
	public void setStaffid(String staffid) {
		this.staffid = staffid;
	}
	public String getSendtime() {
		return sendtime;
	}
	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}
	public String getSmsContent() {
		return smsContent;
	}
	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBadging() {
		return badging;
	}
	public void setBadging(String badging) {
		this.badging = badging;
	}
	
}
