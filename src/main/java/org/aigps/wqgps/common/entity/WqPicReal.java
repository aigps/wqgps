package org.sunleads.common.entity;

import java.io.Serializable;
import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
/**
 * 
* @Title����Ƭʵʱ��
* @Description��<������>
*
* @author qixianping
* @version 1.0
*
* Create Date��  2012-5-16����10:13:31
* Modified By��  <�޸�����������ƴ����д>
* Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
*
* Copyright��Copyright(C),1995-2011 ��IPC��09004804��
* Company������Ԫ��Ƽ����޹�˾
 */
@Entity
@Table(name = "WQ_PIC_REAL")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WqPicReal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 54654651123L;
	
	private String staffId;
	private String phone;
	private long picTime;
	private String picDesc;
	private String picName;
	private Blob picData;
	private String standby1;
	private String standby2;
	
	// Property accessors
	@Id
	@GeneratedValue(generator="hibernate-uuid")
	@GenericGenerator(name = "hibernate-uuid", strategy = "uuid")
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public long getPicTime() {
		return picTime;
	}
	public void setPicTime(long picTime) {
		this.picTime = picTime;
	}
	public String getPicDesc() {
		return picDesc;
	}
	public void setPicDesc(String picDesc) {
		this.picDesc = picDesc;
	}
	public String getPicName() {
		return picName;
	}
	public void setPicName(String picName) {
		this.picName = picName;
	}
	public Blob getPicData() {
		return picData;
	}
	public void setPicData(Blob picData) {
		this.picData = picData;
	}
	public String getStandby1() {
		return standby1;
	}
	public void setStandby1(String standby1) {
		this.standby1 = standby1;
	}
	public String getStandby2() {
		return standby2;
	}
	public void setStandby2(String standby2) {
		this.standby2 = standby2;
	}
	
	

}
