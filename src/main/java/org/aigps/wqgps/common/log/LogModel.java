package org.aigps.wqgps.common.log;

public class LogModel  implements java.io.Serializable {

	private static final long serialVersionUID = -89375650L;
	
	private String startLocateTime;//开始定位时间
	private String endLocateTime;//结束定位时间
	private Long locateInterval;//定位消耗时长,以秒为单位
	private String serverReturnTime;//后台返回时间
	private Long netInterval;//网络传输时长,以秒为单位
	private String lng;//经度
	private String lat;//纬度
	private String locWay;//是GOOGLE定位:GOOGLE;还是GPS定位:GPS
	private String locType;//是单次定位:00，还是周期定位:01
	private Boolean isSuccess;//定位是否成功
	private String errorMsg;//失败原因
	private String reportTime;//定位信息的时间
	
	private String cmpLat;//对比经度
	private String cmpLng;//对比纬度
	private Double offset;//偏差距离
	private Double distance;//偏差距离
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
