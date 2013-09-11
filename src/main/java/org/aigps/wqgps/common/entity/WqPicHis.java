package org.sunleads.common.entity;

import java.io.Serializable;

/**
 * 
* @Title：照片历史表
* @Description：<类描述>
*
* @author qixianping
* @version 1.0
*
* Create Date：  2012-5-16上午10:12:07
* Modified By：  <修改人中文名或拼音缩写>
* Modified Date：<修改日期，格式:YYYY-MM-DD>
*
* Copyright：Copyright(C),1995-2011 浙IPC备09004804号
* Company：杭州元码科技有限公司
 */
public class WqPicHis implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5465465646L;
	
	private String staffId;
	private String phone;
	private String picTime;
	private String picDesc;
	private String picName;
	private Object picData;
	private String standby1;
	private String standby2;
	
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
	public String getPicTime() {
		return picTime;
	}
	public void setPicTime(String picTime) {
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
	public Object getPicData() {
		return picData;
	}
	public void setPicData(Object picData) {
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
