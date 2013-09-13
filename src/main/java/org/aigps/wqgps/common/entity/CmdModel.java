package org.aigps.wqgps.common.entity;

/**
 * ����ģ��
 * @author admin
 *
 */
public class CmdModel implements java.io.Serializable {
	private static final long serialVersionUID = -438434348L;;
	
	/**
	 * �ȴ��ظ� 0
	 */ 
	public static String CMD_PROCESS_CODE = "0";
	public static String CMD_PROCESS = "�ȴ��ظ�";
	/**
	 * �ɹ� 1
	 */
	public static String CMD_SUCCESS_CODE = "1";
	public static String CMD_SUCCESS = "�ɹ�";
	/**
	 * ʧ�� 2
	 */ 
	public static String CMD_FAIL_CODE = "2";
	public static String CMD_FAIL = "ʧ��";
	
	/**
	 * ��ʱ 3
	 */ 
	public static String CMD_TIMEOUT_CODE = "3";
	public static String CMD_TIMEOUT = "��ʱ";
	
	private String staffId;//Ա�����
	private String cmdTypeCode;//ָ�����ʹ���
	private String cmdStateCode;//ָ��״̬����
	private String sendTime;//����ʱ��
	private String userName;//������
	
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
