
package org.sunleads.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.sunleads.common.comparator.RptTimeComparator;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-4-28上午10:20:54
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
@SuppressWarnings("unused")
public class GvphUtil {
	
	private static String columns = "TMN_CODE,TMN_ALIAS,REPORT_TIME,ALARM_TYPE,LOGIT,LAT,LOGIT_OFFSET,LAT_OFFSET,SPEED,ANGLE,HEIGHT,STTS1,STTS2,STTS3,STTS4,ALARM_STTS,ALARM_MARK,GSM_SIGN,GPS_SATL,MONI1,MONI2,MILE,ZCODE,GPS_TYPE,LOC_DESC,IS_VALID_GPS";
	
	public final static Log log = LogFactory.getLog(GvphUtil.class);
	
	/**
	 * 查询时间范围内多员工的历史轨迹
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
		log.info("查询历史轨迹用了："+(endtime-starttime)+"  总记录数："+hourList.size());
		
		Collections.sort(hourList, new RptTimeComparator());
		log.info("按上报时间排序用了："+(System.currentTimeMillis()-endtime));
		
		return hourList;
	}
	
	/**
	 * 查询时间范围内指员工的历史轨迹
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
	 * 查询时间范围内所有员工的历史轨迹
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

