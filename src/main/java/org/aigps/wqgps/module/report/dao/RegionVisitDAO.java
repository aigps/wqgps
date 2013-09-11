
package org.sunleads.module.report.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.util.DateUtil;
import org.sunleads.common.util.SqlStringUtil;

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
public class RegionVisitDAO{

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	private List<Map<String,Object>> setLeaveTimeNow(List<Map<String,Object>> list){
		String nowTime = DateUtil.getCurDate();
		for(Map<String,Object> map : list){
			String leaveTime = (String)map.get("LEAVE_TIME");
			if(StringUtils.isBlank(leaveTime)){
				map.put("LEAVE_TIME", nowTime);
			}
		}
		return list;
	}
	
	/**
	 * 在日期段内，查找指定员工拜访过区域的记录
	 * @param regionIds
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Map<String,Object>> findRegionVisitHisByStaffIds(List<String> staffIds,String startDate,String endDate){
		String sql = "SELECT * FROM WQ_REGION_VISIT_HIS WHERE ENTER_TIME<=? AND LEAVE_TIME>=? AND "+SqlStringUtil.formatListToSQLIn("STAFF_ID", staffIds, true);
		startDate = startDate+" 00:00:00";
		endDate = endDate+" 23:59:59";
		List<Map<String,Object>> list = removeNoValidRecode(this.jdbcTemplate.queryForList(sql, endDate, startDate));
		
		sql = "SELECT * FROM WQ_REGION_VISIT WHERE ENTER_TIME<=? AND "+SqlStringUtil.formatListToSQLIn("STAFF_ID", staffIds, true);
		List<Map<String,Object>> list2 = removeNoValidRecode(this.jdbcTemplate.queryForList(sql, endDate));
		list.addAll(setLeaveTimeNow(list2));
		return list;
	}
	
	/**
	 * 在日期段内，查找指定员工拜访过区域的记录
	 * @param regionIds
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Map<String,Object>> findRegionVisitHisByStaffId(String staffId,String startDate,String endDate){
		String sql = "SELECT * FROM WQ_REGION_VISIT_HIS WHERE ENTER_TIME<=? AND LEAVE_TIME>=? AND STAFF_ID=?";
		startDate = startDate+" 00:00:00";
		endDate = endDate+" 23:59:59";
		List<Map<String,Object>> list = removeNoValidRecode(this.jdbcTemplate.queryForList(sql, endDate, startDate, staffId));
		
		sql = "SELECT * FROM WQ_REGION_VISIT WHERE ENTER_TIME<=? AND STAFF_ID=?";
		List<Map<String,Object>> list2 = removeNoValidRecode(this.jdbcTemplate.queryForList(sql, endDate, staffId));
		list.addAll(setLeaveTimeNow(list2));
		return list;
	}

	/**
	 * 在日期段内，查找指定区域被拜访过的记录
	 * @param regionIds
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Map<String,Object>> findRegionVisitHisByRegionIds(Collection<String> regionIds,String startDate,String endDate){
		String sql = "SELECT * FROM WQ_REGION_VISIT_HIS WHERE ENTER_TIME<=? AND LEAVE_TIME>=? AND "+SqlStringUtil.formatListToSQLIn("REGION_ID", regionIds, true);
		startDate = startDate+" 00:00:00";
		endDate = endDate+" 23:59:59";
		List<Map<String,Object>> list = removeNoValidRecode(this.jdbcTemplate.queryForList(sql, endDate, startDate));

		sql = "SELECT * FROM WQ_REGION_VISIT WHERE ENTER_TIME<=? AND "+SqlStringUtil.formatListToSQLIn("REGION_ID", regionIds, true);
		List<Map<String,Object>> list2 = removeNoValidRecode(this.jdbcTemplate.queryForList(sql, endDate));
		list.addAll(setLeaveTimeNow(list2));
		return list;
	}
	
	/**
	 * 删除过滤无些员工的记录
	 * @param visitedHisList
	 * @return
	 */
	private List<Map<String,Object>> removeNoValidRecode(List<Map<String,Object>> visitedHisList){
		for(Iterator<Map<String,Object>> it=visitedHisList.iterator(); it.hasNext(); ){
			Map<String,Object> map = it.next();
			String staffId = (String)map.get("STAFF_ID");
			if(!DataCache.staffMap.containsKey(staffId)){
				it.remove();
			}
		}
		return visitedHisList;
	}
	
	public void deleteRegionVisitByRegionId(String regionId){
		this.jdbcTemplate.update("DELETE FROM WQ_REGION_VISIT_HIS WHERE REGION_ID=?",regionId);
	}

	public void deleteRegionVisit(String staffId,String regionId){
		this.jdbcTemplate.update("DELETE FROM WQ_REGION_VISIT_HIS WHERE REGION_ID=? AND STAFF_ID=?",regionId,staffId);
	}

	/**
	 * 在日期段内，查找公司所有员工拜访过区域的记录
	 * @param companyId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Map<String,Object>> findRegionVisitByComapny(String companyId,String startDate,String endDate){
		String sql = "SELECT * FROM WQ_REGION_VISIT_HIS WHERE COMPANY_ID=? AND ENTER_TIME>=? AND LEAVE_TIME<=?";
		startDate = startDate+" 00:00:00";
		endDate = endDate+" 23:59:59";
		List<Map<String,Object>> list = removeNoValidRecode(this.jdbcTemplate.queryForList(sql, companyId, startDate, endDate));

		sql = "SELECT * FROM WQ_REGION_VISIT WHERE COMPANY_ID=? AND ENTER_TIME<=? ";
		List<Map<String,Object>> list2 = removeNoValidRecode(this.jdbcTemplate.queryForList(sql, companyId, endDate));
		list.addAll(setLeaveTimeNow(list2));
		return list;
	}
	
}

