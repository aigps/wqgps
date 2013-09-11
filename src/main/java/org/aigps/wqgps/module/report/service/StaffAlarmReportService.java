package org.sunleads.module.report.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.util.AppUtil;
import org.sunleads.module.report.dao.AlarmReportDAO;

@Component
@Transactional
public class StaffAlarmReportService {
	public final static Log log = LogFactory.getLog(StaffAlarmReportService.class);
	
	private AlarmReportDAO alarmReportDAO;
	
	@Autowired
	public void setAlarmReportDAO(AlarmReportDAO alarmReportDAO) {
		this.alarmReportDAO = alarmReportDAO;
	}
	
	//查询指定部门下，所有员工在某日期段内的所有警告信息
	public List<Map<String,Object>> queryStaffAlarmReport(List<String> staffIds,String startDate,String endDate) throws Exception{
		try{
			List<Map<String,Object>> alarmList = alarmReportDAO.findAlarmList(staffIds, startDate, endDate);
			if(alarmList.isEmpty()){
				return alarmList;
			}
			
			Map<String,String[]> fenceRuleNameMap = alarmReportDAO.getFenceRuleNameMap(AppUtil.getUserInfo().getCompanyId());
			
			for(Map<String,Object> map : alarmList) {
				String staffId = (String)map.get("STAFF_ID");
				WqStaffInfo staff = DataCache.staffMap.get(staffId);
				map.put("STAFF_ID", staffId);
				map.put("STAFF_NAME", staff.getCnName());
				String[] names = fenceRuleNameMap.get(map.get("RULE_ID"));
				if(names!=null){
					map.put("ALARM_MSG", "违反了围栏["+names[0]+"]的规则["+names[1]+"]");
				}
			}
			return alarmList;
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
}

