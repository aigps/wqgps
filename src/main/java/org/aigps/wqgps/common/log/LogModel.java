package org.aigps.wqgps.common.log;

public class LogModel  implements java.io.Serializable {

	private static final long serialVersionUID = -89375650L;
	
	private String startLocateTime;//��ʼ��λʱ��
	private String endLocateTime;//������λʱ��
	private Long locateInterval;//��λ����ʱ��,����Ϊ��λ
	private String serverReturnTime;//��̨����ʱ��
	private Long netInterval;//���紫��ʱ��,����Ϊ��λ
	private String lng;//����
	private String lat;//γ��
	private String locWay;//��GOOGLE��λ:GOOGLE;����GPS��λ:GPS
	private String locType;//�ǵ��ζ�λ:00���������ڶ�λ:01
	private Boolean isSuccess;//��λ�Ƿ�ɹ�
	private String errorMsg;//ʧ��ԭ��
	private String reportTime;//��λ��Ϣ��ʱ��
	
	private String cmpLat;//�ԱȾ���
	private String cmpLng;//�Ա�γ��
	private Double offset;//ƫ�����
	private Double distance;//ƫ�����
	private String gpsSpeed;
	
	
	
	public String getGpsSpeed() {
		return gpsSpeed;
	}
	public void setGpsSpeed(String gpsSpeed) {
		this.gpsSpeed = gpsSpeed;
	}
	public String getCmpLat() {
		return cmpLat;
	}
	public void setCmpLat(String cmpLat) {
		this.cmpLat = cmpLat;
	}
	public String getCmpLng() {
		return cmpLng;
	}
	public void setCmpLng(String cmpLng) {
		this.cmpLng = cmpLng;
	}
	public Double getOffset() {
		return offset;
	}
	public void setOffset(Double offset) {
		this.offset = offset;
	}
	public String getStartLocateTime() {
		return startLocateTime;
	}
	public void setStartLocateTime(String startLocateTime) {
		this.startLocateTime = startLocateTime;
	}
	public String getEndLocateTime() {
		return endLocateTime;
	}
	public void setEndLocateTime(String endLocateTime) {
		this.endLocateTime = endLocateTime;
	}
	public Long getLocateInterval() {
		return locateInterval;
	}
	public void setLocateInterval(Long locateInterval) {
		this.locateInterval = locateInterval;
	}
	public String getServerReturnTime() {
		return serverReturnTime;
	}
	public void setServerReturnTime(String serverReturnTime) {
		this.serverReturnTime = serverReturnTime;
	}
	public Long getNetInterval() {
		return netInterval;
	}
	public void setNetInterval(Long netInterval) {
		this.netInterval = netInterval;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getLocWay() {
		return locWay;
	}
	public void setLocWay(String locWay) {
		this.locWay = locWay;
	}
	public String getLocType() {
		return locType;
	}
	public void setLocType(String locType) {
		this.locType = locType;
	}
	public Boolean getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}
	public String getReportTime() {
		return reportTime;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public Double getDistance() {
		return distance;
	}
}
