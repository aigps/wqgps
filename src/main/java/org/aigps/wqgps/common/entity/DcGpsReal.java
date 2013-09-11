package org.sunleads.common.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.gps.util.EncodeUtil;
import org.sunleads.common.util.DateUtil;
import org.sunleads.common.util.StrUtil;
import org.sunleads.common.util.ValueConvertUtil;

public class DcGpsReal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 988787855578342L;

	private String tmnCode;//�ն˱�ż�Ա���ֻ���
	private String tmnAlias;//ҵ��ż�Ա����
	private String reportTime;
	private BigDecimal logit;
	private BigDecimal lat;
	private BigDecimal logitOffset;
	private BigDecimal latOffset;
	private String speed;
	private String angle;
	private String height;
	private String stts1;
	private String stts2;
	private String stts3;
	private String stts4;
	private String alarmStts;
	private String alarmMark;
	private String gsmSign;
	private String gpsSatl;
	private BigDecimal moni1;
	private BigDecimal moni2;
	private BigDecimal mile;
	private String zcode;
	private String gpsType;
	private String precision;
	private String locDesc;
	private String alarmType;
	private Boolean isValidGps;
	
	private String direction;//���ķ���
	private String state;//Ա��״̬
	private String cmdState;//ָ��״̬
	private String phoneState;//�ֻ�״̬
	
	/** default constructor */
	public DcGpsReal() {
	}
	
	public String getTmnCode() {
		return tmnCode;
	}

	public void setTmnCode(String tmnCode) {
		this.tmnCode = tmnCode;
	}

	public String getTmnAlias() {
		return tmnAlias;
	}

	public void setTmnAlias(String tmnAlias) {
		this.tmnAlias = tmnAlias;
	}

	public String getReportTime() {
		return reportTime;
	}

	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}

	public BigDecimal getLogit() {
		return logit;
	}

	public void setLogit(BigDecimal logit) {
		this.logit = logit;
	}

	public BigDecimal getLat() {
		return lat;
	}

	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}

	public BigDecimal getLogitOffset() {
		return logitOffset;
	}

	public void setLogitOffset(BigDecimal logitOffset) {
		this.logitOffset = logitOffset;
	}

	public BigDecimal getLatOffset() {
		return latOffset;
	}

	public void setLatOffset(BigDecimal latOffset) {
		this.latOffset = latOffset;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getAngle() {
		return angle;
	}

	public void setAngle(String angle) {
		this.angle = angle;
		if(StringUtils.isNotBlank(this.angle)){
			direction = StrUtil.getDrivePos(this.angle);
		}
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getStts1() {
		return stts1;
	}

	public void setStts1(String stts1) {
		this.stts1 = stts1;
	}

	public String getStts2() {
		return stts2;
	}

	public void setStts2(String stts2) {
		this.stts2 = stts2;
	}

	public String getStts3() {
		return stts3;
	}

	public void setStts3(String stts3) {
		this.stts3 = stts3;
	}

	public String getStts4() {
		return stts4;
	}

	public void setStts4(String stts4) {
		this.stts4 = stts4;
	}

	public String getAlarmStts() {
		return alarmStts;
	}

	public void setAlarmStts(String alarmStts) {
		this.alarmStts = alarmStts;
	}

	public String getAlarmMark() {
		return alarmMark;
	}

	public void setAlarmMark(String alarmMark) {
		this.alarmMark = alarmMark;
	}

	public String getGsmSign() {
		return gsmSign;
	}

	public void setGsmSign(String gsmSign) {
		this.gsmSign = gsmSign;
	}

	public String getGpsSatl() {
		return gpsSatl;
	}

	public void setGpsSatl(String gpsSatl) {
		this.gpsSatl = gpsSatl;
	}

	public BigDecimal getMoni1() {
		return moni1;
	}

	public void setMoni1(BigDecimal moni1) {
		this.moni1 = moni1;
	}

	public BigDecimal getMoni2() {
		return moni2;
	}

	public void setMoni2(BigDecimal moni2) {
		this.moni2 = moni2;
	}

	public BigDecimal getMile() {
		return mile;
	}

	public void setMile(BigDecimal mile) {
		this.mile = mile;
	}

	public String getZcode() {
		return zcode;
	}

	public void setZcode(String zcode) {
		this.zcode = zcode;
	}

	public String getGpsType() {
		return gpsType;
	}

	public void setGpsType(String gpsType) {
		this.gpsType = gpsType;
	}

	public String getLocDesc() {
		return locDesc;
	}

	public void setLocDesc(String locDesc) {
		this.locDesc = locDesc;
	}
	
	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setCmdState(String cmdState) {
		this.cmdState = cmdState;
	}

	public String getCmdState() {
		return cmdState;
	}
	
	public Boolean getIsValidGps() {
		return isValidGps;
	}

	public void setIsValidGps(Boolean isValidGps) {
		this.isValidGps = isValidGps;
	}

	public void setPhoneState(String phoneState) {
		this.phoneState = phoneState;
	}

	public String getPhoneState() {
		return phoneState;
	}

	/**
	 * ת����ʵʱ��λģ��
	 * @param tmnCode �ֻ���
	 * @param staffId Ա����
	 * @param contentStr �����ַ���
	 */
	public static DcGpsReal convertDataGpsReal(String tmnCode,String staffId,String contentStr){
		String[] strArr = StringUtils.splitPreserveAllTokens(contentStr,"|");
		int index = 1;
		String alarmType = strArr[index++];//��������
		String reportTime = strArr[index++];//gpsʱ��
		String logit = strArr[index++];//����
		String lat = strArr[index++];//γ��
		String speed = strArr[index++];//�ٶ�
		String angle = strArr[index++];//����
		String mile = strArr[index++];//���
		String stts1 = strArr[index++];//״̬1
		String stts2 = strArr[index++];//״̬2
		String stts3 = strArr[index++];//״̬3
		String stts4 = strArr[index++];//״̬4
		String alarmStts = strArr[index++];//����״̬
		String alarmMark = strArr[index++];//����״̬����
		String gsmSign = strArr[index++];//gsm�ź�ǿ��
		String gpsSatl = strArr[index++];//gps���ǿ���
		String moni1 = strArr[index++];//ģ����1
		String moni2 = strArr[index++];//ģ����2
		String height = strArr[index++];//�߶�
		String gpsType = strArr[index++];//gps����
		String precision= strArr[index++];//��λ����
		String logitOffset = strArr[index++];//ƫ�ƾ���
		String latOffset = strArr[index++];//ƫ��γ��
		String zcode = strArr[index++];//�����������
		String locDesc = strArr[index++];//����λ������
		
		DcGpsReal dataGpsReal =new DcGpsReal();
		dataGpsReal.setTmnCode(tmnCode);
		dataGpsReal.setTmnAlias(staffId);
		dataGpsReal.setAlarmType(alarmType);
		dataGpsReal.setReportTime(DateUtil.dateNumberToDateStr(reportTime));
		dataGpsReal.setLogit(BigDecimal.valueOf(Double.valueOf(logit)));
		dataGpsReal.setLat(BigDecimal.valueOf(Double.valueOf(lat)));
		dataGpsReal.setSpeed(speed);
		dataGpsReal.setAngle(angle);
		dataGpsReal.setMile(BigDecimal.valueOf(Long.valueOf(mile)));
		dataGpsReal.setStts1(stts1);
		dataGpsReal.setStts2(stts2);
		dataGpsReal.setStts3(stts3);
		dataGpsReal.setStts4(stts4);
		dataGpsReal.setAlarmStts(alarmStts);
		dataGpsReal.setAlarmMark(alarmMark);
		dataGpsReal.setGsmSign(gsmSign);
		dataGpsReal.setGpsSatl(gpsSatl);
		dataGpsReal.setMoni1(StringUtils.isBlank(moni1)?BigDecimal.valueOf(0):BigDecimal.valueOf(Long.valueOf(moni1)));
		dataGpsReal.setMoni2(StringUtils.isBlank(moni2)?BigDecimal.valueOf(0):BigDecimal.valueOf(Long.valueOf(moni2)));
		dataGpsReal.setHeight(height);
		dataGpsReal.setGpsType(gpsType);
		dataGpsReal.setPrecision(precision);
		if(StringUtils.isNotBlank(logitOffset)){
			dataGpsReal.setLogitOffset(BigDecimal.valueOf(Double.valueOf(logitOffset)));
		}else{
			dataGpsReal.setLogitOffset(new BigDecimal(0));
		}
		if(StringUtils.isNotBlank(latOffset)){
			dataGpsReal.setLatOffset(BigDecimal.valueOf(Double.valueOf(latOffset)));
		}else{
			dataGpsReal.setLatOffset(new BigDecimal(0));
		}
		dataGpsReal.setZcode(zcode);
		dataGpsReal.setLocDesc(locDesc);
		dataGpsReal.setIsValidGps((stts1.charAt(3) == '0'));//�Ƿ�����Ч�Ķ�λ  1-��Ч;0-��Ч
		return dataGpsReal;
	}
	
	/**
	 * ����ʵʱģ�ͻ���ʷģ��
	 * @param map
	 * @param dcGpsReal
	 */
	public static void createModel(Map<String,Object> map,DcGpsReal dcGpsReal){
		dcGpsReal.setTmnCode(ValueConvertUtil.getString(map,"TMN_CODE"));
		dcGpsReal.setTmnAlias(ValueConvertUtil.getString(map,"TMN_ALIAS"));
		dcGpsReal.setAlarmType(ValueConvertUtil.getString(map,"ALARM_TYPE"));
		dcGpsReal.setReportTime(DateUtil.dateNumberToDateStr(ValueConvertUtil.getString(map,"REPORT_TIME")));
		dcGpsReal.setLogit(ValueConvertUtil.getBigDecimal(map,"LOGIT"));
		dcGpsReal.setLat(ValueConvertUtil.getBigDecimal(map,"LAT"));
		dcGpsReal.setSpeed(ValueConvertUtil.getString(map,"SPEED"));
		dcGpsReal.setAngle(ValueConvertUtil.getString(map,"ANGLE"));
		dcGpsReal.setMile(ValueConvertUtil.getBigDecimal(map,"MILE"));
		String stts1 = ValueConvertUtil.getString(map, "STTS1");
		String stts2 = ValueConvertUtil.getString(map, "STTS2");
		String stts3 = ValueConvertUtil.getString(map, "STTS3");
		String stts4 = ValueConvertUtil.getString(map, "STTS4");
		dcGpsReal.setStts1(EncodeUtil.byte2binaryStr(EncodeUtil.HexString2Bytes(stts1, stts1.length()/2)));
		dcGpsReal.setStts2(EncodeUtil.byte2binaryStr(EncodeUtil.HexString2Bytes(stts2, stts2.length()/2)));
//		dcGpsReal.setStts3(EncodeUtil.byte2binaryStr(EncodeUtil.HexString2Bytes(stts3, stts3.length()/2)));
		dcGpsReal.setStts3(stts3);
		dcGpsReal.setStts4(EncodeUtil.byte2binaryStr(EncodeUtil.HexString2Bytes(stts4, stts4.length()/2)));
		dcGpsReal.setAlarmStts(ValueConvertUtil.getString(map, "ALARM_STTS"));
		dcGpsReal.setAlarmMark(ValueConvertUtil.getString(map, "ALARM_MARK"));
		dcGpsReal.setGsmSign(ValueConvertUtil.getString(map, "GSM_SIGN"));
		dcGpsReal.setGpsSatl(ValueConvertUtil.getString(map, "GPS_SATL"));
		dcGpsReal.setMoni1(ValueConvertUtil.getBigDecimal(map,"MONI1"));
		dcGpsReal.setMoni2(ValueConvertUtil.getBigDecimal(map,"MONI2"));
		dcGpsReal.setHeight(ValueConvertUtil.getString(map, "HEIGHT"));
		dcGpsReal.setGpsType(ValueConvertUtil.getString(map, "GPS_TYPE"));
		dcGpsReal.setPrecision(ValueConvertUtil.getString(map, "PRECISION"));
		dcGpsReal.setLogitOffset(ValueConvertUtil.getBigDecimal(map,"LOGIT_OFFSET"));
		dcGpsReal.setLatOffset(ValueConvertUtil.getBigDecimal(map,"LAT_OFFSET"));
		dcGpsReal.setZcode(ValueConvertUtil.getString(map,"ZCODE"));
		dcGpsReal.setLocDesc(ValueConvertUtil.getString(map,"LOC_DESC"));
		dcGpsReal.setIsValidGps(ValueConvertUtil.getBoolean(map,"IS_VALID_GPS"));
	}

}
