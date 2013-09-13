
package org.aigps.wqgps.module.report.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.util.SqlStringUtil;
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
public class AlarmReportDAO{

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public List<Map<String,Object>> findAlarmList(List<String> staffIds,String startTime,String endTime){
		startTime = startTime + " 00:00:00";
		endTime = endTime + " 23:59:59";
		String sql = "SELECT * FROM WQ_ALARM_INFO WHERE ALARM_TIME>=? AND ALARM_TIME<=? AND "+SqlStringUtil.formatListToSQLIn("STAFF_ID", staffIds, true)+" ORDER BY STAFF_ID,ALARM_TIME";
		return this.jdbcTemplate.queryForList(sql, startTime, endTime);
	}

	//系统所有规则，以其对应的区域ID
	public Map<String,String[]> getFenceRuleNameMap(String companyId){
		String sql = "SELECT R.ID,F.NAME FNAME,R.NAME RNAME FROM WQ_ELE_FENCE F,WQ_RULE R WHERE F.ID=R.ELE_FENCE_ID AND R.IS_ENABLE!=0 AND F.REGION_ID IS NOT NULL AND R.COMPANY_ID=?";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,companyId);
		
		Map<String,String[]> returnMap = new HashMap<String,String[]>();
		for(Map<String,Object> map : list){
			String ruleId = map.get("ID").toString();
			String fName = map.get("FNAME").toString();
			String rName = map.get("RNAME").toString();
			returnMap.put(ruleId, new String[]{fName,rName});
		}
		return returnMap;
	}
}

