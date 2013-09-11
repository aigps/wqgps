package org.sunleads.common.log;

import java.io.Serializable;

public class PositionHisReportModel implements Serializable{
	
	private static final long serialVersionUID = -351352135082L;
	
	private String carCode;
	private double lat;
	private double lng;
	private double offsetlat;
	private double offsetlng;
	private long repotrTime;
	private long speed;
	private long mile;
	private String pos;
	private String locDesc;
	private int signalLevel;
	private int moniStts0;
	private int moniStts1;
	private int satlCount;
	private String status;
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getSignalLevel() {
		return signalLevel;
	}

	public void setSignalLevel(int signalLevel) {
		this.signalLevel = signalLevel;
	}

	public int getMoniStts0() {
		return moniStts0;
	}

	public void setMoniStts0(int moniStts0) {
		this.moniStts0 = moniStts0;
	}

	public int getMoniStts1() {
		return moniStts1;
	}

	public void setMoniStts1(int moniStts1) {
		this.moniStts1 = moniStts1;
	}

	public int getSatlCount() {
		return satlCount;
	}

	public void setSatlCount(int satlCount) {
		this.satlCount = satlCount;
	}

	public String getLocDesc() {
		return locDesc;
	}

	public void setLocDesc(String locDesc) {
		this.locDesc = locDesc;
	}

	public double getOffsetlat() {
		return offsetlat;
	}

	public void setOffsetlat(double offsetlat) {
		this.offsetlat = offsetlat;
	}

	public double getOffsetlng() {
		return offsetlng;
	}

	public void setOffsetlng(double offsetlng) {
		this.offsetlng = offsetlng;
	}

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public long getRepotrTime() {
		return repotrTime;
	}

	public void setRepotrTime(long repotrTime) {
		this.repotrTime = repotrTime;
	}

	public long getSpeed() {
		return speed;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	public long getMile() {
		return mile;
	}

	public void setMile(long mile) {
		this.mile = mile;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

}
