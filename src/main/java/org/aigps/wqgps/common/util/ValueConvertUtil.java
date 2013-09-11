package org.sunleads.common.util;

import java.math.BigDecimal;
import java.util.Map;

/**
 * ֵת������
 * @author admin
 *
 */
public class ValueConvertUtil {
	
	/**
	 * ��ȡ�ַ���
	 * @param map
	 * @param key
	 * @return
	 */
	public static String getString(Map<String,Object> map, String key){
		Object value = map.get(key);
		return value==null ? "" : value.toString();
	}
	
	/**
	 * ��ȡBigDecimal����
	 * @param map
	 * @param key
	 * @return
	 */
	public static BigDecimal getBigDecimal(Map<String,Object> map,String key){
		Object value = map.get(key);
		return value==null ? BigDecimal.valueOf(0) : (BigDecimal)value;
	}
	
	/**
	 * ��ȡint����
	 * @param map
	 * @param key
	 * @return
	 */
	public static int getInt(Map<String,Object> map, String key){
		Object value = map.get(key);
		return value==null ? 0 : Integer.parseInt(value.toString());
	}
	
	public static Boolean getBoolean(Map<String,Object> map, String key){
		Object value = map.get(key);
		return "1".equals(value);
	}
}
