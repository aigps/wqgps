package org.sunleads.common.log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.gps.map.model.MapLocation;
import org.gps.map.util.MapUtil;
import org.gps.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.util.DateUtil;

/**
 * @Title��<�����>
 * @Description��<������>
 * 
 * @author xiexueze
 * @version 1.0
 * 
 *          Create Date�� 2011-6-20����04:47:45 Modified By�� <�޸�����������ƴ����д> Modified
 *          Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 * 
 *          Copyright��Copyright(C),1995-2011 ��IPC��09004804�� Company�������е��Ƽ��������޹�˾
 */
@Component
@Transactional
public class MobileLogService {
	private ReportDAO reportDAO;

	@Autowired
	public void setReportDAO(ReportDAO reportDAO) {
		this.reportDAO = reportDAO;
	}
	
	
	//�Զ���λ begin******************************************************************************************
	private static List<Map<String,String>> locateList = new ArrayList<Map<String,String>>();
	public List<Map<String,String>> findLocateList(){
		return locateList;
	}
	public List<Map<String,String>> createLocate(Map<String,String> locate){
		locateList.add(locate);
		locate.put("startTime", DateUtil.getCurDate());
		Thread t = new LocateThread(locate, locateList);
		t.setDaemon(true);
		t.start();
		return locateList;
	}
	public List<Map<String,String>> deleteLocate(String mobileNumber){
		for(Iterator<Map<String,String>> it=locateList.iterator(); it.hasNext();){
			Map<String,String> map = it.next();
			if(mobileNumber.equals(map.get("mobileNumber"))){
				it.remove();
			}
		}
		return locateList;
	}
	//�Զ���λ end******************************************************************************************
	
	//��λ��Ϣ�����Ƚ� begin******************************************************************************************
	public List<LogModel> calculateDistance(String lng,String lat,List<LogModel> array) throws Exception{
		try {
			MapLocation logLocation = new MapLocation(Double.parseDouble(lng), Double.parseDouble(lat));
			for(LogModel model : array){
				if(StringUtils.isBlank(model.getLng())||StringUtils.isBlank(model.getLat())){
					continue;
				}
				MapLocation logLocation2 = new MapLocation(Double.parseDouble(model.getLng()), Double.parseDouble(model.getLat()));
				Double dis = MapUtil.distance(logLocation, logLocation2, 'm');
				model.setDistance(MathUtil.multiply(dis, 1000, 2));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return array;
	}
	//��λ��Ϣ�����Ƚ� end******************************************************************************************
	
	public static void main(String[] args) {
		try {
			File file = new File("19.txt");
			List<LogModel> dataList = new MobileLogService().findLogList(
					"2011-08-10 12:55", "2011-08-19 17:55",null, 
					getBytesFromFile(file),false,null);
			System.out.println(dataList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static byte[] getBytesFromFile(File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
		}
		return null;
	}
	
	public List<LogModel> findLogList(String startTime, String endTime, String locType, byte[] file,boolean isCompare,String compareCarCode) throws Exception {
		try {
			startTime = startTime + ":00";
			endTime = endTime + ":59";

			ByteArrayInputStream bi = new ByteArrayInputStream(file);
			InputStreamReader in = new InputStreamReader(bi);
			BufferedReader br = new BufferedReader(in);

			String lineStr = br.readLine();
			lineStr = lineStr == null ? "" : lineStr.trim();

			List<String> singleLocationData = new ArrayList<String>();
			List<LogModel> dataList = new ArrayList<LogModel>();

			while (lineStr != null) {
				singleLocationData.add(lineStr);
				
				if (lineStr.indexOf("WakeLock release") > 0) {//������λ��Ϣ�Ľ�����־
					//���ݵ�����λ��Ϣ����־�����ɶ�λ��Ϣģ��
					LogModel model = analyseSingleLocation(singleLocationData);
					
					if (model != null) {
						boolean isCondition = true;
						if (startTime.compareTo(model.getStartLocateTime()) > 0) {
							isCondition = false;
						}
						if (isCondition
								&& endTime.compareTo(model.getStartLocateTime()) < 0) {
							isCondition = false;
						}
						if (isCondition && locType != null
								&& !locType.equals(model.getLocType())) {
							isCondition = false;
						}
						if (isCondition) {
							dataList.add(model);
						}
					}
					singleLocationData.clear();
				}
				lineStr = br.readLine();
			}

			//���Ҫ�Ƚ�
			if(isCompare){
				Map<String,LogModel> hisMap = searchPositionHisReport(compareCarCode,startTime,endTime);
				for (LogModel logModel : dataList) {
					try {
						String key = logModel.getEndLocateTime().replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
						long longkey = Long.parseLong(key);
						LogModel hisModel = null;
						if(hisMap.get(String.valueOf(longkey)) != null){
							hisModel = hisMap.get(String.valueOf(longkey));
						}else if(hisMap.get(String.valueOf(longkey + 1)) != null){
							hisModel = hisMap.get(String.valueOf(longkey + 1));
						}else if(hisMap.get(String.valueOf(longkey + 2)) != null){
							hisModel = hisMap.get(String.valueOf(longkey + 2));
						}else if(hisMap.get(String.valueOf(longkey + 3)) != null){
							hisModel = hisMap.get(String.valueOf(longkey + 3));
						}else if(hisMap.get(String.valueOf(longkey + 4)) != null){
							hisModel = hisMap.get(String.valueOf(longkey + 4));
						}else if(hisMap.get(String.valueOf(longkey + 5)) != null){
							hisModel = hisMap.get(String.valueOf(longkey + 5));
						}else if(hisMap.get(String.valueOf(longkey + 6)) != null){
							hisModel = hisMap.get(String.valueOf(longkey + 6));
						}else if(hisMap.get(String.valueOf(longkey + 7)) != null){
							hisModel = hisMap.get(String.valueOf(longkey + 7));
						}else if(hisMap.get(String.valueOf(longkey + 8)) != null){
							hisModel = hisMap.get(String.valueOf(longkey + 8));
						}else if(hisMap.get(String.valueOf(longkey + 9)) != null){
							hisModel = hisMap.get(String.valueOf(longkey + 9));
						}else if(hisMap.get(String.valueOf(longkey - 1)) != null){
							hisModel = hisMap.get(String.valueOf(longkey - 1));
						}else if(hisMap.get(String.valueOf(longkey - 2)) != null){
							hisModel = hisMap.get(String.valueOf(longkey - 2));
						}else if(hisMap.get(String.valueOf(longkey - 3)) != null){
							hisModel = hisMap.get(String.valueOf(longkey - 3));
						}else if(hisMap.get(String.valueOf(longkey - 4)) != null){
							hisModel = hisMap.get(String.valueOf(longkey - 4));
						}else if(hisMap.get(String.valueOf(longkey - 5)) != null){
							hisModel = hisMap.get(String.valueOf(longkey - 5));
						}else if(hisMap.get(String.valueOf(longkey - 6)) != null){
							hisModel = hisMap.get(String.valueOf(longkey - 6));
						}else if(hisMap.get(String.valueOf(longkey - 7)) != null){
							hisModel = hisMap.get(String.valueOf(longkey - 7));
						}else if(hisMap.get(String.valueOf(longkey - 8)) != null){
							hisModel = hisMap.get(String.valueOf(longkey - 8));
						}else if(hisMap.get(String.valueOf(longkey - 9)) != null){
							hisModel = hisMap.get(String.valueOf(longkey - 9));
						}
						
						if(hisModel != null){
							setComareData(logModel, hisModel);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				
			}
			
			return dataList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
	
	private void setComareData(LogModel logModel,LogModel hisModel) throws Exception{
		if(logModel.getLat() == null || logModel.getLng() == null){
			return;
		}
		logModel.setCmpLat(hisModel.getLat());
		logModel.setCmpLng(hisModel.getLng());
		logModel.setGpsSpeed(hisModel.getGpsSpeed());
		MapLocation logLocation = new MapLocation(Double.parseDouble(logModel.getLng()), Double.parseDouble(logModel.getLat()));
		MapLocation logLocation2 = new MapLocation(Double.parseDouble(logModel.getCmpLng()), Double.parseDouble(logModel.getCmpLat()));
		Double dis = MapUtil.distance(logLocation, logLocation2, 'k');
		logModel.setOffset(MathUtil.multiply(dis, 1000, 2));
	}

	// ����������λ��־
	private LogModel analyseSingleLocation(List<String> singleLocationData) {
		if (singleLocationData.size() < 10)// ����û�ж�λ��Ϣ
			return null;

		Boolean[] isLocMsgAndLocSuccess = judgeIsLocAndLocSuccess(singleLocationData);
		if (isLocMsgAndLocSuccess[0] == false) {// ������Ƕ�λ��Ϣ������
			return null;
		}
		if (isTempPeriod(singleLocationData)) {// ��ʱ���ڶ�λ����������
			return null;
		}
		LogModel model = new LogModel();
		if (isLocMsgAndLocSuccess[1] == false) {// �����λʧ�ܣ���ȡʧ��ԭ��
			model.setErrorMsg(getErrorMsg(singleLocationData));
		}

		// ���ö�λ�ĸ���ֵ
		setLocModel(singleLocationData, isLocMsgAndLocSuccess[1], model);

		// ��־��Ϣ����ڽ�����ң���ʱ����ͳ�ƣ�����
		if (model.getStartLocateTime() == null
				|| model.getEndLocateTime() == null
				|| model.getServerReturnTime() == null) {
			return null;
		}

		// ��λ����ʱ��,����Ϊ��λ
		Long locateInterval = DateUtil.getBetweenSecond(model.getStartLocateTime(), model.getEndLocateTime());
		model.setLocateInterval(locateInterval);

		// ���紫��ʱ��,����Ϊ��λ
		Long netInterval = DateUtil.getBetweenSecond(model.getEndLocateTime(),model.getServerReturnTime());
		model.setNetInterval(netInterval);

		return model;
	}

	// �ж��Ƿ��Ƕ�λ����־��Ϣ��ͬʱ�ж϶�λ�ɹ�����ʧ��
	private Boolean[] judgeIsLocAndLocSuccess(List<String> singleLocationData) {
		boolean isLocMsg = false, locSuccess = false;
		for (String info : singleLocationData) {
			if (info.indexOf("<RESULT>0</RESULT>") != -1) {
				isLocMsg = true;
				locSuccess = true;
				break;
			} else if (info.indexOf("<RESULT>1</RESULT>") != -1) {
				isLocMsg = true;
				locSuccess = false;
				break;
			}
		}
		return new Boolean[] { isLocMsg, locSuccess };
	}

	// ��ʱ���ڶ�λ����������
	private Boolean isTempPeriod(List<String> singleLocationData) {
		for (String info : singleLocationData) {
			if (info.indexOf("03</REQ_ID>") != -1) {
				return true;
			}
		}
		return false;
	}

	// ��ȡʧ����Ϣ
	private String getErrorMsg(List<String> singleLocationData) {
		for (String info : singleLocationData) {
			int index = info.indexOf("getLocationByGoogle error1");
			if (index != -1) {
				return info.substring(index + 27);
			}
		}
		return null;
	}

	// ��ȡ��λ��ʼʱ�䡢��λ����ʱ�䡢��̨����������ʱ�䡢��λ���͡������λ�ɹ�����ȡ���Ⱥ�γ��
	private void setLocModel(List<String> singleLocationData,Boolean locSuccess, LogModel model) {
		String startLocateTime = null, endLocateTime = null, serverReturnTime = null, locWay = null, locType = null, lng = null, lat = null;
		String reportTime = null;
		for (String info : singleLocationData) {
			info = info.trim();
			if (startLocateTime==null && (info.indexOf("WakeLock acquire") >= 0 || info
							.indexOf("GPS started to fix") >= 0)) {
				startLocateTime = info.substring(0, 19);
			}
			if (info.indexOf("HttpPostFixResult request") >= 0) {
				endLocateTime = info.substring(0, 19);
			}
			if (info.indexOf("HttpPostFixResult response") >= 0) {
				serverReturnTime = info.substring(0, 19);
			}
//			if (locWay == null && info.indexOf("CellID=") > 0) {// �л�վ���ݣ����ǻ�վ��λ
//				locWay = "Google";
//			}
			if (locType == null && info.indexOf("01</REQ_ID>") >= 0) {
				locType = "00";// ���ζ�λ
			}
			if (locType == null && info.indexOf("02</REQ_ID>") >= 0) {
				locType = "01";// ���ڶ�λ
			}
			if (locSuccess == true) {
				if (info.indexOf("<LONGITUDE>") >= 0) {
					lng = info.replaceAll("<LONGITUDE>", "").replaceAll("</LONGITUDE>", "");
				}
				if (info.indexOf("<LATITUDE>") >= 0) {
					lat = info.replaceAll("<LATITUDE>", "").replaceAll("</LATITUDE>", "");
				}
				if(info.indexOf("<FIXTIME>") >= 0) {
					reportTime = info.replaceAll("<FIXTIME>", "").replaceAll("</FIXTIME>", "");
				}
				if (info.indexOf("<FIXMODE>7</FIXMODE>") >= 0) {
					locWay = "Google";
				}else if (info.indexOf("<FIXMODE>2</FIXMODE>") >= 0) {
					locWay = "GPS";
				}else if (info.indexOf("<FIXMODE>1</FIXMODE>") >= 0) {
					locWay = "Google";
				}else if (info.indexOf("<FIXMODE>0</FIXMODE>") >= 0) {
					locWay = "MSA";
				}else if (info.indexOf("<FIXMODE>3</FIXMODE>") >= 0) {
					locWay = "MSA->GPS";
				}else if (info.indexOf("<FIXMODE>4</FIXMODE>") >= 0) {
					locWay = "GPS->Google";
				}else if (info.indexOf("<FIXMODE>9</FIXMODE>") >= 0) {
					locWay = "GPS->Google";
				}else if (info.indexOf("<FIXMODE>5</FIXMODE>") >= 0) {
					locWay = "GPS->MSA";
				}else if (info.indexOf("<FIXMODE>6</FIXMODE>") >= 0) {
					locWay = "GPS->MSA->Google";
				}else if (info.indexOf("<FIXMODE>8</FIXMODE>") >= 0) {
					locWay = "Hybrid";
				}
			}
		}
		model.setStartLocateTime(startLocateTime);
		model.setEndLocateTime(endLocateTime);
		model.setServerReturnTime(serverReturnTime);
		model.setLocType(locType);
		model.setLocWay(locWay);
		model.setLat(lat);
		model.setLng(lng);
		model.setIsSuccess(locSuccess);
		model.setReportTime(reportTime);
	}
	
	/**
	 * ��ʷ�켣��ѯ
	 * @param carList�����б�
	 * @param startTime��ʼʱ��
	 * @param endTime����ʱ��
	 * @return
	 * @throws Exception
	 */
	public Map<String,LogModel> searchPositionHisReport(String vhcList,String startTime, String endTime) throws Exception{
		try {
			Map<String,LogModel> list = reportDAO.searchPositionHisReport(vhcList,  startTime,  endTime);
			return list;
		} catch (Exception e) {
			throw e;
		}
		
	}
}
