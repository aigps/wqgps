package org.sunleads.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bsh.Interpreter;
/**
 * 
 * <pre>
 * Title:���ڹ�����
 * Description: ���ڹ�����
 * </pre>
 * @author xiongwenbo  xiongwenbo@gmail.com
 * @version 1.00.00
 * <pre>
 * �޸ļ�¼
 *    �޸ĺ�汾:     �޸��ˣ�  �޸�����:     �޸�����: 
 * </pre>
 */
public final class DateUtil {
	private static final Log log = LogFactory.getLog(DateUtil.class);
	private static final String secondLabel = "��";

	private static final String minuteLabel = "��";

	private static final String hourLabel = "ʱ";

	private static final String dayLabel = "��";
	/**
	 * "yyyyMMdd HH:mm"
	 */
	public static final String DEFAULT_SHORT_DATE_TIME_FORMAT = "yyyyMMdd HH:mm";
	
	/**
	 * "yyyyMMddHHmmss"
	 */
	public static final String DEFAULT_ALL_DATE_TIME_FORMAT = "yyyyMMddHHmmss";

	/**
	 * "yyyyMM"
	 */
	public static final String DEFAULT_SHORT_YEAR_MONTH_FORMAT = "yyyyMM";
	/**
	 * "yyyyMMdd"
	 */
	public static final String DEFAULT_SHORT_DATE_FORMAT = "yyyyMMdd";
	/**
	 * "yyyy-MM-dd"
	 */
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	/**
	 * "yyyy-MM-dd HH:mm"
	 */
	public static final String DEFAULT_DATETIME_FORMAT_EX = "yyyy-MM-dd HH:mm";
	/**
	 * "yyyy-MM-dd HH:mm:ss"
	 */
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/**
	 * "HH:mm:ss"
	 */
	public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

	public static SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
	public static SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
	
	/**
	 * ��ȡ��ǰϵͳʱ���ַ�������ǰʱ�䣩
	 * @param format
	 * @return
	 */
	public static String getCurDateStr(String format) {
		return dateToString(new Date(),format);
	}
	
	/**
	 * ��ȡ��ǰϵͳʱ��"yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static String getCurDate() {
		return format.format(new Date());
	}
	/**
	 * ת�����ڶ��������ַ���
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateToString(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}
	
	/**
	 * ת�������ַ��������ڶ���
	 * 
	 * @param string
	 * @param format
	 *            ���ڸ�ʽ
	 * @return
	 */
	public static Date stringToDate(String dateString, String format) {
		Date date = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			date = formatter.parse(dateString);
		} catch (ParseException e) {
			log.error("stringToDate fail", e);
		}
		return date;
	}
	
	/**
	 * �Զ�ƥ��ģʽ-ת�������ַ��������ڶ���
	 * @param dateString
	 * @return
	 */
	public static Date autoStringToDate(String dateString) {
		String format = DEFAULT_DATE_FORMAT;
		if(dateString.matches("\\d{2,4}\\d{1,2}")){
			format = DEFAULT_SHORT_YEAR_MONTH_FORMAT;
		}else if(dateString.matches("\\d{2,4}\\d{1,2}\\d{1,2}")){
			format = DEFAULT_SHORT_DATE_FORMAT;
		}else if (dateString.matches("\\d{2,4}\\-\\d{1,2}\\-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}")){
			format = DEFAULT_DATETIME_FORMAT;
		}else if (dateString.matches("\\d{2,4}\\-\\d{1,2}\\-\\d{1,2} \\d{1,2}:\\d{1,2}")){
			format = DEFAULT_DATETIME_FORMAT_EX;
		}else if (dateString.matches("\\d{1,2}:\\d{1,2}:\\d{1,2}")){
			format = DEFAULT_TIME_FORMAT;
		}
		return stringToDate(dateString, format);
	}

	
	/**
	 * ��ʽ�������ַ���
	 * @param str �����ַ���
	 * @param format
	 * @return
	 */
	public static String formatDateStr(String dateStr,String format) throws Exception{
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date date = formatter.parse(dateStr);
		SimpleDateFormat formatter2 = new SimpleDateFormat(format);
		return formatter2.format(date);
	}

	/**
	 * �� <code>java.util.Date</code> ת��Ϊ <code>java.sql.Date</code>
	 * 
	 * @param date
	 * @return
	 */
	public static java.sql.Date toSqlDate(Date date) {
		if (date != null) {
			return new java.sql.Date(date.getTime());
		}
		return null;
	}

	/**
	 * �� <code>java.util.Date</code> ת��Ϊ <code>java.sql.Date</code>
	 * 
	 * @param date
	 * @return
	 */
	public static java.sql.Timestamp toTimestamp(Date date) {
		if (date != null) {
			return new java.sql.Timestamp(date.getTime());
		}

		return null;
	}

	/**
	 * �Ƚ�s1��s2ת�������ں�ȽϵĽ���� s1 �� s2 ��ͬ���� 0 �� s1 �� s2 ֮�󷵻� 1, ��֮���� -1
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int compareDate(String s1, String s2) {
		try {
			Date d1 = autoStringToDate(s1);
			Date d2 = autoStringToDate(s2);
			return d1.compareTo(d2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static int compareDate(Date d1, Date d2) {
		try {
			return d1.compareTo(d2);
		} catch (Exception ex) {

		}
		return -1;
	}

	private static Calendar getCalendar(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal;
	}

	/**
	 * ��ȡ��
	 * @param date
	 * @return
	 */
	public static int getYear(Date date) {
		Calendar cal = getCalendar(date.getTime());
		return cal.get(Calendar.YEAR);
	}

	/**
	 * ��ȡ��
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
		Calendar cal = getCalendar(date.getTime());
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * ��ȡ��
	 * @param date
	 * @return
	 */
	public static int getDate(Date date) {
		Calendar cal = getCalendar(date.getTime());
		return cal.get(Calendar.DATE);
	}

	/**
	 * ��ȡСʱ
	 * @param date
	 * @return
	 */
	public static int getHour(Date date) {
		Calendar cal = getCalendar(date.getTime());
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * ��ȡ����
	 * @param date
	 * @return
	 */
	public static int getMinute(Date date) {
		Calendar cal = getCalendar(date.getTime());
		return cal.get(Calendar.MINUTE);
	}

	/**
	 * ��ȡ��
	 * @param date
	 * @return
	 */
	public static int getSecond(Date date) {
		Calendar cal = getCalendar(date.getTime());
		return cal.get(Calendar.SECOND);
	}

	/**
	 * ����ʱ���������
	 */
	public static String getRnd() {
		Calendar tCal = Calendar.getInstance();
		Timestamp ts = new Timestamp(tCal.getTime().getTime());
		java.util.Date date = new java.util.Date(ts.getTime());
		SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		String tmpStr = formatter.format(date)
				+ Math.round(Math.random() * 1000 + 1);
		return (tmpStr);
	}

	/**
	 * ��ȡ��������ʱ�����ֵ��ֵ
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getDateSCDiff(String firstTime,String secondTime){
		Date firstDate = autoStringToDate(firstTime);//������
		Date secondDate = autoStringToDate(secondTime);//�ͻ���
		// ��������õ���������
		return firstDate.getTime() - secondDate.getTime();
	}
	
	/**
	 * ���������ȡ�����ʱ����
	 * 
	 * @param dateOne
	 * @param dateTwo
	 * @return
	 */
	public static String dateDiffByDateStr(String dateOne, String dateTwo) {
		Date date1 = autoStringToDate(dateOne);
		Date date2 = autoStringToDate(dateTwo);
		// long diffDate = date1.getTime()-date2.getTime()>0 ?
		// date1.getTime()-date2.getTime():
		// date2.getTime()-date1.getTime();
		// ��������õ���������
		long second = (date1.getTime() - date2.getTime()) / (1000) > 0 ? (date1
				.getTime() - date2.getTime()) / (1000)
				: (date2.getTime() - date1.getTime()) / (1000);
		String castDay = String.valueOf(second / (60 * 60 * 24));// ��һ���������
		String castHours = String
				.valueOf((second % (60 * 60 * 24)) / (60 * 60));// ģһ��������ٳ�һСʱ������
		String castMinute = String
				.valueOf(((second % (60 * 60 * 24)) % (60 * 60)) / 60);// ģһ�����������ģһСʱ������
																		// ,
																		// Ȼ���ٳ�һ���ӵ���
		String castSecond = String
				.valueOf(((second % (60 * 60 * 24)) % (60 * 60)) % 60);// ģһ�����������ģһСʱ������
																		// ,
		StringBuilder cn = new StringBuilder();														// Ȼ����ģһ���ӵ���
		if(!"0".equals(castDay)){
			cn.append(castDay).append(dayLabel);
		}
		if(!"0".equals(castHours)){
			cn.append(castHours).append(hourLabel);
		}
		if(!"0".equals(minuteLabel)){
			cn.append(castMinute).append(minuteLabel);
		}
		if(!"0".equals(castSecond)){
			cn.append(castSecond).append(secondLabel);
		}
		return cn.toString();
//		return castDay.concat(dayLabel).concat(castHours).concat(hourLabel).concat(
//				castMinute).concat(minuteLabel).concat(castSecond).concat(secondLabel);
	}
	/**
	 * ���������ȡ�����ʱ����
	 * 
	 * @param times ��
	 * @return
	 */
	public static String dateDiffByDateStr(long times) {
		long second = times;
				String castDay = String.valueOf(second / (60 * 60 * 24));// ��һ���������
				String castHours = String
				.valueOf((second % (60 * 60 * 24)) / (60 * 60));// ģһ��������ٳ�һСʱ������
				String castMinute = String
				.valueOf(((second % (60 * 60 * 24)) % (60 * 60)) / 60);// ģһ�����������ģһСʱ������
				// ,
				// Ȼ���ٳ�һ���ӵ���
				String castSecond = String
				.valueOf(((second % (60 * 60 * 24)) % (60 * 60)) % 60);// ģһ�����������ģһСʱ������
				// ,
				// Ȼ����ģһ���ӵ���
				return castDay.concat(dayLabel).concat(castHours).concat(hourLabel).concat(
						castMinute).concat(minuteLabel).concat(castSecond).concat(secondLabel);
	}
	
	//��ȡ��������
	public static String getWorkYearByDateDiff(String beginDate,String endDate,boolean showAllMonthFlag) throws Exception{
		Date date1 = autoStringToDate(beginDate);
		Date date2 = autoStringToDate(endDate);
		// ��������õ���������
		long second = (date1.getTime() - date2.getTime()) / (1000) > 0 ? (date1
				.getTime() - date2.getTime()) / (1000)
				: (date2.getTime() - date1.getTime()) / (1000);
	    String castYear = String.valueOf(second/(60 * 60 * 24 * 365));
	    String castMonth = String.valueOf((second%(60 * 60 * 24 * 365))/(60 * 60 * 24 * 30));
		String castTotalMonth = String.valueOf(second / (60 * 60 * 24 * 30));// �����ٸ���
		if(showAllMonthFlag){
			return castYear.concat("��").concat(castMonth).concat("��").concat(",��").concat(castTotalMonth).concat("��");
		}else{
			return castYear.concat("��").concat(castMonth).concat("��");
		}
	}
	
	/**
	 * ��֤ʱ��Ĳ�ֵ������Ϊ���㣩(<diffValue����true);
	 * @param validateTime1
	 * @param validateTime2
	 * @param diffValue
	 * @return
	 * @throws Exception
	 */
	public static boolean validateDayByDiffValue(String validateTime1, String validateTime2, long diffValue) throws Exception{
		return validateTimeByDiffValue(validateTime1,validateTime2,diffValue*24*60);
	}
	
	/**
	 * ������֤ʱ��Ĳ�ֵ(�Է��Ӽ���).(<diffValue����true)
	 * @param validateTime1
	 * @param validateTime2
	 * @param diffValue
	 * @return
	 * @throws Exception
	 */
	public static boolean validateTimeByDiffValue(String validateTime1, String validateTime2, long diffValue) throws Exception{
			Date date1 = autoStringToDate(validateTime1);
			Date date2 = autoStringToDate(validateTime2);
			// ��������õ���������
			long second = (date1.getTime() - date2.getTime()) / (1000*60) > 0 ? (date1.getTime() - date2.getTime()) / (1000*60)
					: (date2.getTime() - date1.getTime()) / (1000*60);
			if(second>=diffValue){
				return false;
			}
			return true;
	}
	
  	/**
  	 * ��ñ����һ�������    
  	 * @return
  	 */
    public static String getFirstDayOfYear(){    
        int yearPlus = getYearPlus();    
        GregorianCalendar currentDate = new GregorianCalendar();    
        currentDate.add(GregorianCalendar.DATE,yearPlus);    
        Date yearDay = currentDate.getTime();    
        SimpleDateFormat df=new SimpleDateFormat(DEFAULT_DATE_FORMAT); 
        String preYearDay = df.format(yearDay);    
        return preYearDay;    
    }    
	private static int getYearPlus(){    
        Calendar cd = Calendar.getInstance();    
        int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);//��õ�����һ���еĵڼ���    
        cd.set(Calendar.DAY_OF_YEAR,1);//��������Ϊ�����һ��    
        cd.roll(Calendar.DAY_OF_YEAR,-1);//�����ڻع�һ�졣    
        int MaxYear = cd.get(Calendar.DAY_OF_YEAR);    
        if(yearOfNumber == 1){    
            return -MaxYear;    
        }else{    
            return 1-yearOfNumber;    
        }    
    } 
	
	/**
	 * ��ȡ���µ�һ��
	 * @return
	 */
	public static String getFirstDayOfMonth(){
		  SimpleDateFormat sdf=new SimpleDateFormat(DEFAULT_DATE_FORMAT);        
	       Calendar lastDate = Calendar.getInstance();    
	       lastDate.set(Calendar.DATE,1);//��Ϊ��ǰ�µ�1��    
		  return sdf.format(lastDate.getTime());
	}
	
	//��õ�ǰ�����뱾������������    (���Ǽ�������й���һ��ʼ����)
    private static int getMondayPlus() {    
        Calendar cd = Calendar.getInstance();    
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);      //�����ĩ�ǵ�1��  
        if(dayOfWeek==1){//��ĩʱ��
        	return dayOfWeek-7;
        } else {    
            return 2-dayOfWeek;    
        }    
    }    
        
    /**
     * ��ȡ���ܵ�һ��(��һ����)
     * @return
     */
    public static String getFirstDayOfWeek(){    
    	 SimpleDateFormat tempDate = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
         int mondayPlus = getMondayPlus();    
         Calendar now=Calendar.getInstance();
 		 now.add(Calendar.DATE, mondayPlus);	
         return tempDate.format(now.getTime());    
    }    
	/**
	 * ��ȡ��ǰ����(yyyyMMdd)
	 * @return
	 */
	public static String getNowDate(){
		SimpleDateFormat tempDate = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		return tempDate.format(new java.util.Date());
	}

	/**
	 * ���ݲ�����ȡ����ǰ�������
	 * LST numΪ��:��ǰ���ں�num���Ƿ���ֵ numΪ��:��ǰ����ǰnum���Ƿ���ֵ ���ص����ڵĸ�ʽ:yyyy-MM-dd
	 */
	public static String getTheDay(int num) {
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		GregorianCalendar gc = new GregorianCalendar();
		gc.add(GregorianCalendar.DATE, num);
		Date theday = gc.getTime();
		return sdf.format(theday);
	}
	
	/**
	 * ���ĳ���ڵ���һ��
	 * 
	 * @param date
	 * @return
	 */
	public static String getNextDayByDataStr(String dateStr) {
		if (dateStr == null || dateStr.trim().length() == 0) {
			return "";
		}
		SimpleDateFormat f = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(f.parse(dateStr));
		} catch (ParseException ex) {
			log.error("getNextDay fail", ex);
		}
		calendar.add(5, 1);
		return f.format(calendar.getTime());
	}

	/**
	 * ���ĳ���ڵ���N��
	 * 
	 * @param date
	 * @return
	 */
	public static String getNextDayByDataStr(String dateStr,int n) {
		if (dateStr == null || dateStr.trim().length() == 0) {
			return "";
		}
		SimpleDateFormat f = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(f.parse(dateStr));
		} catch (ParseException ex) {
			log.error("getNextDay fail", ex);
		}
		calendar.add(5, n);
		return f.format(calendar.getTime());
	}
	
	/**
	 * ����YYYYMMDD��ʽ��ȡ�����ڵ��µ�һ��
	 * @param yyyyMMDD
	 * @return
	 */
	public static String getMonthFirstDayByYYYYMMDD(String yyyyMMDD){
		SimpleDateFormat tempDate = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		try {
			Calendar now = Calendar.getInstance();
			String[] yearAndMonthStr = new String[2];
			yearAndMonthStr[0] = yyyyMMDD.substring(0, 4);
			yearAndMonthStr[1] = yyyyMMDD.substring(4, 6);
			now.set(Integer.valueOf(yearAndMonthStr[0]), Integer
					.valueOf(yearAndMonthStr[1]).intValue()-1,1);
			return tempDate.format(now.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("getMonthFirstDayByYYYYMMDD fail", e);
		}
		return tempDate.format(new java.util.Date());
	}
	
	/**
	 * ����YYYYMMDD��ʽ��ȡ�����ڵ������һ��
	 * @param yyyyMMDD
	 * @return
	 */
	public static String getMonthLastDayByYYYYMMDD(String yyyyMMDD){
		SimpleDateFormat tempDate = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		try {
			Calendar now = Calendar.getInstance();
			String[] yearAndMonthStr = new String[2];
			yearAndMonthStr[0] = yyyyMMDD.substring(0, 4);
			yearAndMonthStr[1] = yyyyMMDD.substring(4, 6);
			now.set(Integer.valueOf(yearAndMonthStr[0]), Integer
					.valueOf(yearAndMonthStr[1]).intValue()-1,1);
			now.add(Calendar.MONTH, 1);
			now.add(Calendar.DATE,-1);
			return tempDate.format(now.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("getMonthLastDayByYYYYMMDD fail", e);
		}
		return tempDate.format(new java.util.Date());
	}
	
	   /**  
     * ��������������������  
     * @param startDate  
     * @param endDate  
     * @return  
     * @throws ParseException  
     */  
    public static int getDistDates(String dateOne,String dateTwo)     
    {   
        int totalDate = 0;  
        Date startDate = autoStringToDate(dateOne);
		Date endDate = autoStringToDate(dateTwo);
        Calendar calendar = Calendar.getInstance();   
        calendar.setTime(startDate);   
        long timestart = calendar.getTimeInMillis();   
        calendar.setTime(endDate);   
        long timeend = calendar.getTimeInMillis();   
        totalDate = Long.valueOf(Math.abs((timeend - timestart))/(1000*60*60*24)).intValue();   
        return totalDate;   
    }  

	/**
	 * ĳ�����Ƿ���������֮���ʱ��
	 * @param sourceDate
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static boolean betweenTwoDate(String sourceDate,String beginDate,String endDate) throws Exception{
		boolean flag = false;
		Date sourceDateD = autoStringToDate(sourceDate);
		Date beginDateD = autoStringToDate(beginDate);
		Date endDateD = autoStringToDate(endDate);
		if(sourceDateD.getTime()>beginDateD.getTime() && sourceDateD.getTime()<endDateD.getTime()){
			flag = true;
		}
		return flag;
	}

	
	/**
	 * ����ʼ���ַ�������ת����������(���硰2011-01-01����Ϊ��20110101000000��)
	 * @param date
	 * @return
	 */
	public static String dateStrToBeginDateNumber(String date){
		date = date.replaceAll("[-| |:]", "");
		while(date.length()<14){
			date += "0";
		}
		return date;
	}
	
	/**
	 * ���������ַ�������ת����������(���硰2011-01-01����Ϊ��20110101235959��)
	 * @param date
	 * @return
	 */
	public static String dateStrToEndDateNumber(String date){
		date = date.replaceAll("[-| |:]", "");
		if(date.length()==8){
			date = date+"235959";
		}else if(date.length()==12){
			date = date+"59";
		}else if(date.length()==10){
			date = date+"5959";
		}
		return date;
	}
	
	/**
	 * ��������ת���ַ�������(���硰20110101235959����Ϊ��2011-01-01 23:59:59��)
	 * @param date
	 * @return
	 */
	public static String dateNumberToDateStr(String dateStr){
		if(dateStr.length()==14)
			return new StringBuilder(dateStr).insert(12, ":").insert(10, ":").insert(8, " ").insert(6, "-").insert(4, "-").toString();
		return dateStr;
	}

	/**
	 * �����ڽ������㣬���ڱ��ʽ��  {DATE}+-����   {MONTH}+-����   {YEAR}+-����   {SYSTIME}
	 * @param sysDate
	 * @param calender
	 * @param timeExp
	 * @return
	 */
	public static String mathDateExpression(Date sysDate,Calendar calender,String timeExp){
		if(StringUtils.isBlank(timeExp)){
			return null;
		}
		if(timeExp.equals("{SYSTIME}")){
			return format.format(sysDate);
		}
		calender.setTime(sysDate);
		
		int calenderType = Calendar.DATE;
		Interpreter i = new Interpreter(); 
		if(timeExp.startsWith("{DATE}")){
			timeExp = timeExp.replace("{DATE}", "");
			calenderType = Calendar.DATE;
		}else if(timeExp.startsWith("{MONTH}")){
			timeExp = timeExp.replace("{MONTH}", "");
			calenderType = Calendar.MONTH;
		}else if(timeExp.startsWith("{YEAR}")){
			timeExp = timeExp.replace("{YEAR}", "");
			calenderType = Calendar.YEAR;
		}
		try{
			i.eval("num = "+timeExp); 
			int num = (Integer)i.get("num");
			calender.add(calenderType, num);
			return format.format(calender.getTime());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	//������ʱ����������
	public static long getBetweenSecond(String startTime, String endTime){
		try{
			return (format.parse(endTime).getTime() - format.parse(startTime).getTime())/1000;
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0l;
	}

	/**
	 * ��ѯʱ�ã��ַ�������ת����������
	 * @param date
	 * @return
	 */
	public static String getNumberDate(String date){
		date = date.replaceAll("[-| |:]", "");
		while(date.length()<14){
			date += "0";
		}
		return date;
	}
	
	public static String dateToLong(String dateString){
		Date date = stringToDate(dateString, DEFAULT_DATETIME_FORMAT);
		return dateToString(date, DEFAULT_ALL_DATE_TIME_FORMAT);
	}
}

