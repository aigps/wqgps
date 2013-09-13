/**
 * 
 */
package org.aigps.wqgps.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

/**�ַ���������
 * @author xiongwb
 *
 */
@SuppressWarnings("rawtypes")
public class StrUtil {
	private static StringBuffer sumSb = new StringBuffer();
	public final static String getSumStr(String columnName){
		sumSb.setLength(0);
		sumSb.append("SUM");
		sumSb.append("(").append("columnName").append(")");
		return sumSb.toString();
	}
	
	public static String collectionToString(Collection array){
		if(array == null || array.isEmpty()){
			return "";
		}
		StringBuffer str = new StringBuffer("");
		for(Object obj : array){
			str.append(obj.toString()).append(",");
		}
		return str.deleteCharAt(str.length()-1).toString();
	}
	
	/**
	 * �����ַ������鷵��sql�����in��ֵ
	 * @param array
	 * @return
	 */
	public final static String getInSqlByArray(String[] array){
		StringBuffer stringBuffer = new StringBuffer();
		for(String value:array){
			stringBuffer.append("'").append(value).append("'").append(",");
		}
		return stringBuffer.delete(stringBuffer.length()-1, stringBuffer.length()).toString();
	}
	/**����sql�����in��ֵ
	 * @param valueList �����ַ���ֵ��List����
	 * @return
	 */
	public final static String getInSqlByStringList(List<String> valueList){
		return getInSqlByStringList(valueList,true);
	}
	
	/**����sql�����in��ֵ
	 * @param valueList �����ַ���ֵ��List����
	 * @return
	 */
	public final static String getInSqlByStringList(List<String> valueList,boolean isAppendStrFlag){
		StringBuffer stringBuffer = new StringBuffer();
		for(String value:valueList){
		//	String value = (String)valueList.get(i);
			if(isAppendStrFlag){
				stringBuffer.append("'");
			}
			stringBuffer.append(value);
			if(isAppendStrFlag){
				stringBuffer.append("'");
			}
			stringBuffer.append(",");
		}
		return stringBuffer.delete(stringBuffer.length()-1, stringBuffer.length()).toString();
	}
	
	/**����sql�����in��ֵ
	 * @param valueList �����ַ���ֵ��List����
	 * @return
	 */
	public final static String getInSqlByIntegerList(List<Integer> valueList){
		StringBuffer stringBuffer = new StringBuffer();
		for(Integer value:valueList){
			stringBuffer.append(value).append(",");
		}
		return stringBuffer.delete(stringBuffer.length()-1, stringBuffer.length()).toString();
	}
	
	
	/**��ȡ���ݿ����Զ��������
	 * @param dataCode ��ǰ��ѯ������������ݱ��
	 * @return �����������
	 * @throws Exception
	 */
	public static  String getAutoGoUpSequence(String dataCode) throws Exception{  
		if(dataCode==null){
			dataCode = "0";
		}else{
			dataCode = new Integer(Integer.valueOf(dataCode)+1).toString();
			/*if(temp.length()<=10){
				StringBuffer nextsb = new StringBuffer(temp);
				for (int i = temp.length(); i < 10; i++) {
					nextsb.insert(0, "0");
				}
				dataCode = nextsb.toString();
			}else{
				throw new Exception("����Զ������Ѿ�����10λ�������ƣ� next ="+dataCode);
			}*/
		}
		return dataCode;
	}
	
	/**
	 * һλ�ַ���ǰ�油0
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String addZeroStr(String str)throws Exception{
		String zero = "0";
		if(str.length()==1){
			str = zero.concat(str);
		}
		return str;
	}
	
	/**
	 * ���ݲ���ֵ��ȡ�������Ͻṹ��
	 * @param paramsList
	 * @return
	 * @throws Exception
	 */
	public static Object[] getSqlParams(List paramsList) throws Exception{
		Object[] params=null;
		if(paramsList!=null && paramsList.size()>0){
			params = new Object[paramsList.size()];
			for (int i = 0; i < paramsList.size(); i++) {
				params[i] = paramsList.get(i);
			}
		}else{
			params = new Object[0];
		}
		return params;
	}

	private static final String B="����";
	private static final String DB="����";
	private static final String D="����";
	private static final String DN="����";
	private static final String N = "����";
	private static final String XN="����";
	private static final String X = "����";
	private static final String XB = "����";
	/**���ݽǶȻ�ȡ����
	 * @param angle
	 * @return
	 */
	public final static String getDrivePos(String angleStr){
		int angle = 0;
		if(angleStr!=null){
			angle = Integer.valueOf(angleStr).intValue();
		}
		String drivePos =B;
		if(angle>0 && angle<90){
			drivePos = DB;
		}else if(angle ==90){
			drivePos = D;
		}else if(angle>90 && angle<180){
			drivePos = DN;
		}else if(angle==180){
			drivePos = N;
		}else if(angle>180 && angle<270){
			drivePos = XN;
		}else if(angle==270){
			drivePos = X;
		}else if(angle>270 && angle<360){
			drivePos = XB;
		}
		return drivePos;
	}
	
	/**
	 * ͨ����γ��,�õ����ĵ�ַ
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public static String geocodeAddr(String latitude, String longitude) {
		if(StringUtils.isBlank(latitude) || StringUtils.isBlank(longitude)){
			return null;
		}
		String addr = "";
		String url = String.format(
				"http://ditu.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s",
				latitude, longitude);
		try {
			URL myURL = new URL(url);
			URLConnection httpsConn = (URLConnection) myURL.openConnection();
			if (httpsConn != null) {
				InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(), "UTF-8");
				BufferedReader br = new BufferedReader(insr);
				String data = null;
				if ((data = br.readLine()) != null) {
					String[] retList = data.split(",");
					if (retList.length > 2 && ("200".equals(retList[0]))) {
						data = data.replaceAll("\"", "").replace(", �й�", "").replace("�й�", "");
						addr = data.substring(data.indexOf(",",5)+1);
					} else {
						addr = "";
					}
				}
				insr.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return addr;
	} 
	
	  public static String[] getcodeAddrByCellAndLacByExlive(String cellId,String lacId){
			if(StringUtils.isBlank(cellId) || StringUtils.isBlank(lacId)){
				return null;
			}
			String[] retList = null;
			String url = String.format(
					"http://www.exlive.cn/posinfo/LBSLocation?cellid=%s&lac=%s",
					cellId, lacId);
			try {
				URL myURL = new URL(url);
				HttpURLConnection httpsConn = (HttpURLConnection) myURL.openConnection();
				if (httpsConn != null) {
					InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream());
					BufferedReader br = new BufferedReader(insr);
					String data = null;
					while ((data = br.readLine()) != null) {
						if(data!=null && data.trim().length()>0){
							retList = data.split(",",4);
						}
					}
					insr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return retList;
		}
	  
	  public static String[] getcodeAddrByCellAndLacByAnttna(String cellId,String lacId){
		  
			  if(StringUtils.isBlank(cellId) || StringUtils.isBlank(lacId)){
					return null;
				}
			    String[] retList = null;
				String url = String.format(
						"http://www.anttna.com/cell2gps/cell2gps.php?cellid=%s&lac=%s",
						cellId, lacId);
				try {
					URL myURL = new URL(url);
					HttpURLConnection httpsConn = (HttpURLConnection) myURL.openConnection();
					if (httpsConn != null) {
						InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream());
						BufferedReader br = new BufferedReader(insr);
						String latLngStr = br.readLine();
						if(latLngStr!=null && latLngStr.trim().length()>0){
							latLngStr = latLngStr.substring(0, latLngStr.length()-1);
							try{
								String[] tmps = latLngStr.split(",",2);
								if(tmps!=null && tmps.length==2){
									retList = new String[4];
									retList[0] = tmps[0];
									retList[1] = tmps[1];
								}
							}catch(Exception e){
							}
						}
						String geo= br.readLine();
						if(geo!=null && geo.trim().length()>0){
							if(retList!=null){
								retList[3] = geo;
							}
						}
						insr.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				return retList;
	  }

	/**
	 * ��ȡ���ݿ�������ΨһID(���µ�ID��ȡ��ʽ)
	 * @return
	 * @throws Exception
	 */
	public static String getUuidSequence() {
		try{
			return UUID.randomUUID().toString().replaceAll("-", "");  
		}catch (Exception e) {}
		return null;
	}
		
	public static void main(String[] args) {
		System.out.println(getcodeAddrByCellAndLacByAnttna("1093","8412"));
	}
	
}
