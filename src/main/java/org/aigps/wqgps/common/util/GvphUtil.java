
package org.aigps.wqgps.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.comparator.RptTimeComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2011-4-28����10:20:54
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
 */
@SuppressWarnings("unused")
public class GvphUtil {
	
	private static String columns = "TMN_CODE,TMN_ALIAS,REPORT_TIME,ALARM_TYPE,LOGIT,LAT,LOGIT_OFFSET,LAT_OFFSET,SPEED,ANGLE,HEIGHT,STTS1,STTS2,STTS3,STTS4,ALARM_STTS,ALARM_MARK,GSM_SIGN,GPS_SATL,MONI1,MONI2,MILE,ZCODE,GPS_TYPE,LOC_DESC,IS_VALID_GPS";
	
	public final static Log log = LogFactory.getLog(GvphUtil.class);
	
	/**
	 * ��ѯʱ�䷶Χ�ڶ�Ա������ʷ�켣
	 * @param staffList
	 * @param startTime
	 * @param endTime
	 * @param jdbcTemplate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String,Object>> findAllGvphList(List<String> staffList, String startTime, String endTime, JdbcTemplate jdbcTemplate){
		if(StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)){
			return new ArrayList<Map<String,Object>>();
		}
		
		long starttime = System.currentTimeMillis();
		List<Map<String,Object>> hourList = new ArrayList<Map<String,Object>>();
//		List<Map<String,Object>> hourList = findHourList(staffList,startTime,endTime,jdbcTemplate);
		List<Map<String,Object>> gvphList = findGvphList(staffList,startTime,endTime,jdbcTemplate);
		hourList.addAll(gvphList);
		
		long endtime = System.currentTimeMillis();
		log.info("��ѯ��ʷ�켣���ˣ�"+(endtime-starttime)+"  �ܼ�¼����"+hourList.size());
		
		Collections.sort(hourList, new RptTimeComparator());
		log.info("���ϱ�ʱ���������ˣ�"+(System.currentTimeMillis()-endtime));
		
		return hourList;
	}
	
	/**
	 * ��ѯʱ�䷶Χ��ָԱ������ʷ�켣
	 * @param staffId
	 * @param startTime
	 * @param endTime
	 * @param jdbcTemplate
	 * @return
	 */
	public static List<Map<String,Object>> findAllGvphList(String staffId, String startTime, String endTime, JdbcTemplate jdbcTemplate){
		List<String> l = new ArrayList<String>();
		l.add(staffId);
		return findAllGvphList(l,startTime,endTime,jdbcTemplate);
	}
	
	/**
	 * ��ѯʱ�䷶Χ������Ա������ʷ�켣
	 * @param startTime
	 * @param endTime
	 * @param jdbcTemplate
	 * @return
	 */
	public static List<Map<String,Object>> findAllGvphList(String startTime, String endTime, JdbcTemplate jdbcTemplate){
		return findAllGvphList(new ArrayList<String>(),startTime,endTime,jdbcTemplate);
	}
	
	
	
	
	
	private static List<Map<String,Object>> findHourList(List<String> staffList, String startTime, String endTime, JdbcTemplate jdbcTemplate){
		StringBuilder sql = new StringBuilder("SELECT ").append(columns).append(" FROM DC_GPS_HOUR WHERE REPORT_TIME >= ? AND REPORT_TIME <= ?");
		appendToSql(staffList,sql);
		return jdbcTemplate.queryForList(sql.toString(),DateUtil.getNumberDate(startTime),DateUtil.getNumberDate(endTime));
	}
	
	private static List<Map<String,Object>> findGvphList(List<String> staffList, String startTime, String endTime, JdbcTemplate jdbcTemplate){
		StringBuilder sql = new StringBuilder("SELECT ").append(columns).append(" FROM DC_GPS_HIS WHERE REPORT_TIME >= ? AND REPORT_TIME <= ?");
		appendToSql(staffList,sql);
		return jdbcTemplate.queryForList(sql.toString(),DateUtil.getNumberDate(startTime),DateUtil.getNumberDate(endTime));
	}
	
	private static void appendToSql(List<String> staffList,StringBuilder sql){
		if(staffList!=null && !staffList.isEmpty()){
			if(staffList.size() == 1){
				sql.append(" AND TMN_ALIAS = '").append(staffList.get(0)).append("'");
			}else{
				sql.append(SqlStringUtil.getInSqlByStringList("TMN_ALIAS", staffList));
			}
		}
	}
}

