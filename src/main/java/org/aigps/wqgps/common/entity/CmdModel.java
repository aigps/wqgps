package org.aigps.wqgps.common.entity;

/**
 * 命令模型
 * @author admin
 *
 */
public class CmdModel implements java.io.Serializable {
	private static final long serialVersionUID = -438434348L;;
	
	/**
	 * 等待回复 0
	 */ 
	public static String CMD_PROCESS_CODE = "0";
	public static String CMD_PROCESS = "等待回复";
	/**
	 * 成功 1
	 */
	public static String CMD_SUCCESS_CODE = "1";
	public static String CMD_SUCCESS = "成功";
	/**
	 * 失败 2
	 */ 
	public static String CMD_FAIL_CODE = "2";
	public static String CMD_FAIL = "失败";
	
	/**
	 * 超时 3
	 */ 
	public static String CMD_TIMEOUT_CODE = "3";
	public static String CMD_TIMEOUT = "超时";
	
	private String staffId;//员工编号
	private String cmdTypeCode;//指令类型代码
	private String cmdStateCode;//指令状态代码
	private String sendTime;//发送时间
	private String userName;//发送者
	
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getCmdTypeCode() {
		return cmdTypeCode;
	}
	public void setCmdTypeCode(String cmdTypeCode) {
		this.cmdTypeCode = cmdTypeCode;
	}
	public String getCmdStateCode() {
		return cmdStateCode;
	}
	public void setCmdStateCode(String cmdStateCode) {
		this.cmdStateCode = cmdStateCode;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
