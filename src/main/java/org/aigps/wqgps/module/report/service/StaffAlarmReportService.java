package org.aigps.wqgps.module.report.service;

import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.module.report.dao.AlarmReportDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class StaffAlarmReportService {
	public final static Log log = LogFactory.getLog(StaffAlarmReportService.class);
	
	private AlarmReportDAO alarmReportDAO;
	
	@Autowired
	public void setAlarmReportDAO(AlarmReportDAO alarmReportDAO) {
		this.alarmReportDAO = alarmReportDAO;
	}
	
	//��ѯָ�������£�����Ա����ĳ���ڶ��ڵ����о�����Ϣ
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
					map.put("ALARM_MSG", "Υ����Χ��["+names[0]+"]�Ĺ���["+names[1]+"]");
				}
			}
			return alarmList;
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
}

