
package org.aigps.wqgps.module.report.dao;

import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.WqTravelPlan;
import org.aigps.wqgps.common.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-6-28下午01:22:17
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
@Component
public class TravelReportDAO extends HibernateDAO<Object, String>{

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	//获取员工staffId，时间从startTime到endTime，定制的差旅计划
	public List<WqTravelPlan> findTravelPlan(String staffId,String startTime,String endTime){
		String hql = "from WqTravelPlan where staffId=? and startTime>=? and endTime<=? order by startTime";
		return this.find(hql, staffId, startTime, endTime);
	}
	
	//获取员工staffId 在区域areaIds内，时间从startTime到endTime停留的时长
	public long findStayLong(String staffId, String areaIds, String startTime, String endTime){
		startTime = startTime+" 00:00:00";
		endTime = endTime+" 23:59:59";
		
		String sql = "SELECT START_TIME, END_TIME FROM dc_rg_area_his WHERE TMN_ALIAS=? AND START_TIME>=? AND END_TIME<=? AND (";
		
		String[] zCodes = StringUtils.split(areaIds, ",");
		for(int i=0; i<zCodes.length; i++){
			sql = sql + " RG_AREA_CODE LIKE '"+zCodes[i]+"%' OR ";
		}
		sql = sql.substring(0,sql.length()-4);
		sql = sql + ")";
		
		List<Map<String,Object>> list = this.jdbcTemplate.queryForList(sql,staffId,startTime,endTime);
		long stayLong = 0;
		for(Map<String,Object> map : list){
			startTime = (String)map.get("START_TIME");
			endTime = (String)map.get("END_TIME");
			stayLong += DateUtil.getBetweenSecond(startTime, endTime);
		}
		return stayLong;
	}
	
	//获取员工staffId 时间段内行走的行政区域
	public List<Map<String,Object>> findAreas(String staffId, String startTime, String endTime){
		startTime = startTime+" 00:00:00";
		endTime = endTime+" 23:59:59";
		String sql = "SELECT RG_AREA_CODE, START_TIME, END_TIME FROM dc_rg_area_his WHERE TMN_ALIAS=? AND START_TIME>=? AND END_TIME<=? ORDER BY START_TIME";
		return this.jdbcTemplate.queryForList(sql,staffId,startTime,endTime);
	}
}

