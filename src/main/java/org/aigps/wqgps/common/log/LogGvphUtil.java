
package org.sunleads.common.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.sunleads.common.comparator.RptTimeComparator;
import org.sunleads.common.util.DateUtil;

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
public class LogGvphUtil {
	
	private static String columns = "LONGIT,LAT,REPORT_TIME,SPEED";
	
	public final static Log log = LogFactory.getLog(LogGvphUtil.class);
	
	/**
	 * 查询时间范围内多部车的历史轨迹
	 * @param vhcList
	 * @param startTime
	 * @param endTime
	 * @param jdbcTemplate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String,Object>> findAllGvphList(String vhcList, String startTime, String endTime, JdbcTemplate jdbcTemplate){
		if(StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)){
			return new ArrayList<Map<String,Object>>();
		}
		
		long starttime = System.currentTimeMillis();
		
		List<Map<String,Object>> hourList = findHourList(vhcList,startTime,endTime,"0",jdbcTemplate);
		List<Map<String,Object>> gvphList = findGvphList(vhcList,startTime,endTime,"0",jdbcTemplate);
		hourList.addAll(gvphList);
		
		long endtime = System.currentTimeMillis();
		log.info("查询历史轨迹用了："+(endtime-starttime)+"  总记录数："+hourList.size());
		
		Collections.sort(hourList, new RptTimeComparator());
		log.info("按上报时间排序用了："+(System.currentTimeMillis()-endtime));
		
		return hourList;
	}
	
	private static List<Map<String,Object>> findHourList(String vhcList, String startTime, String endTime,String speed, JdbcTemplate jdbcTemplate){
		StringBuilder sql = new StringBuilder("SELECT ").append(columns).append(" FROM GIS_VEHICLE_POSITION_HOUR WHERE REPORT_TIME >= ? AND REPORT_TIME <= ? AND SPEED>=?");
		sql.append(" and vehicle_code = ?");
		return jdbcTemplate.queryForList(sql.toString(),DateUtil.dateStrToBeginDateNumber(startTime),DateUtil.dateStrToBeginDateNumber(endTime),speed,vhcList);
	}
	
	private static List<Map<String,Object>> findGvphList(String vhcList, String startTime, String endTime,String speed, JdbcTemplate jdbcTemplate){
		StringBuilder sql = new StringBuilder("SELECT ").append(columns).append(" FROM GIS_VEHICLE_POSITION_H WHERE REPORT_TIME >= ? AND REPORT_TIME <= ? AND SPEED>=?");
		sql.append(" and vehicle_code = ?");
		return jdbcTemplate.queryForList(sql.toString(),DateUtil.dateStrToBeginDateNumber(startTime),DateUtil.dateStrToBeginDateNumber(endTime),speed,vhcList);
	}
	
}

