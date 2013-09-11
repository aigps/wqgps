package org.sunleads.common.log;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReportDAO {
	public final static Log log = LogFactory.getLog(ReportDAO.class);
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/**
	 * 历史轨迹查询
	 * @param carList车牌列表
	 * @param startTime开始时间
	 * @param endTime结束时间
	 * @return
	 * @throws Exception
	 */
	public Map<String,LogModel> searchPositionHisReport(String vhcList, String beginDate,String endDate) throws Exception{
		List<Map<String,Object>> tempReustList = LogGvphUtil.findAllGvphList(vhcList, beginDate, endDate, jdbcTemplate);
		
		Map<String,LogModel> resultMap = new HashMap<String, LogModel>();
		
		for(Map<String,Object> tempMap:tempReustList){
			long tempReportTime = ((BigDecimal)tempMap.get("REPORT_TIME")).longValue();
			double templng = tempMap.get("LONGIT")==null?0:((BigDecimal)tempMap.get("LONGIT")).doubleValue();
			double templat = tempMap.get("LAT")==null?0:((BigDecimal)tempMap.get("LAT")).doubleValue();
			int tempSpeed = tempMap.get("SPEED")==null?0:((BigDecimal)tempMap.get("SPEED")).intValue();
			
			LogModel model = new LogModel();
			model.setLat(templat + "");
			model.setLng(templng + "");
			model.setGpsSpeed(tempSpeed + "");
			
			resultMap.put(tempReportTime+"",model);
		}
		return resultMap;
	}
	
}
