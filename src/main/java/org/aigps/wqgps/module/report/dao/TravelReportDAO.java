
package org.sunleads.module.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.sunleads.common.dao.HibernateDAO;
import org.sunleads.common.entity.WqTravelPlan;
import org.sunleads.common.util.DateUtil;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2011-6-28����01:22:17
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
 */
@Component
public class TravelReportDAO extends HibernateDAO<Object, String>{

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	//��ȡԱ��staffId��ʱ���startTime��endTime�����ƵĲ��üƻ�
	public List<WqTravelPlan> findTravelPlan(String staffId,String startTime,String endTime){
		String hql = "from WqTravelPlan where staffId=? and startTime>=? and endTime<=? order by startTime";
		return this.find(hql, staffId, startTime, endTime);
	}
	
	//��ȡԱ��staffId ������areaIds�ڣ�ʱ���startTime��endTimeͣ����ʱ��
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
	
	//��ȡԱ��staffId ʱ��������ߵ���������
	public List<Map<String,Object>> findAreas(String staffId, String startTime, String endTime){
		startTime = startTime+" 00:00:00";
		endTime = endTime+" 23:59:59";
		String sql = "SELECT RG_AREA_CODE, START_TIME, END_TIME FROM dc_rg_area_his WHERE TMN_ALIAS=? AND START_TIME>=? AND END_TIME<=? ORDER BY START_TIME";
		return this.jdbcTemplate.queryForList(sql,staffId,startTime,endTime);
	}
}

