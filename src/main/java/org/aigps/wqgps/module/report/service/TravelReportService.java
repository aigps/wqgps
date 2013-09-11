package org.sunleads.module.report.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.entity.DcChinaArea;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqTravelPlan;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.DateUtil;
import org.sunleads.common.util.GvphUtil;
import org.sunleads.module.report.dao.TravelReportDAO;
/**
 * ���ദ��ͻ���Ա���ݷõ���ϸ���
 */
@Component
@Transactional
public class TravelReportService {
	public final static Log log = LogFactory.getLog(TravelReportService.class);
	
	private TravelReportDAO travelReportDAO;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Autowired
	public void setTravelReportDAO(TravelReportDAO travelReportDAO) {
		this.travelReportDAO = travelReportDAO;
	}
	
	//ͳ��Ա����ĳʱ����ڣ�ÿ�γ������������ڳ�������ͣ��ʱ����
	public List<Map<String,Object>> queryStaffTravelPlansReport(String staffId, String startTime, String endTime)throws Exception{
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		try{
			List<WqTravelPlan> list = travelReportDAO.findTravelPlan(staffId, startTime, endTime);
			for(WqTravelPlan plan : list){
				Map<String,Object> model = new HashMap<String,Object>();
				returnList.add(model);
				
				model.put("startTime", plan.getStartTime());
				model.put("endTime", plan.getEndTime());
				model.put("reason", plan.getReason());
				model.put("staffId", staffId);
				model.put("areaIds", plan.getAreaIds());
				if(StringUtils.isNotBlank(plan.getAreaIds())){
					Long stayLong = travelReportDAO.findStayLong(staffId, plan.getAreaIds(), plan.getStartTime(), plan.getEndTime());
					model.put("stayLong", stayLong);
					
					String[] areaIds = StringUtils.split(plan.getAreaIds(), ",");
					String areaName = "";
					for(int i=0; i<areaIds.length; i++){
						DcChinaArea area = DataCache.dcChinaAreaMap.get(areaIds[i]);
						areaName = areaName + area.getFullName()+" ";
					}
					model.put("areaName", areaName);
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		return returnList;
	}
	
	//ͳ��Ա����ĳ�γ����У�;��������ʱ���ͣ��ʱ��
	public List<Map<String,Object>> queryStaffTravelPlanReport(String staffId, String areaIds, String startTime, String endTime)throws Exception{
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		
		try{
			//Ա������ʱ��������߹���������������
			List<Map<String,Object>> list = travelReportDAO.findAreas(staffId, startTime, endTime);
			
			int zCodeLenght = 0;
			String[] zCodes = StringUtils.split(areaIds, ",");
			for(int i=0; i<zCodes.length; i++){
				if(zCodes[i].length()>zCodeLenght){
					zCodeLenght = zCodes[i].length();
				}
			}
			String prevZCode = null;
			for(Map<String,Object> map : list){
				String zCode = (String)map.get("RG_AREA_CODE");
				DcChinaArea area = this.getValidArea(zCodeLenght, zCode);
				if(area == null){
					continue;
				}
				startTime = (String)map.get("START_TIME");
				endTime = (String)map.get("END_TIME");
//				if(!area.getZcode().equals(prevZCode)){
//					Map<String,Object> model = new HashMap<String,Object>();
//					returnList.add(model);
//				}
				Map<String,Object> model = new HashMap<String,Object>();
				returnList.add(model);
				prevZCode = area.getZcode();
				model.put("areaName", area.getFullName());
				model.put("isTravelArea", isTravelArea(prevZCode,zCodes) ? "��" : "��");
				model.put("startTime", startTime);
				model.put("endTime", endTime);
				model.put("stayLong", DateUtil.getBetweenSecond(startTime, endTime));
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		return returnList;
	}
	
	public List<Map<String,Object>> querySingleVisitedReport(String staffId, String startTime, String endTime)throws Exception{
		try{
			List<Map<String,Object>> list = GvphUtil.findAllGvphList(staffId, startTime, endTime, jdbcTemplate);
			WqCompanyInfo company = AppUtil.getSessionData().getCompany();
			Boolean flag = company.getUseInvalidLoc();
			for(Iterator<Map<String,Object>> it=list.iterator(); it.hasNext(); ){
				Map<String,Object> map = it.next();
				if(flag!=true && "0".equals(map.get("IS_VALID_GPS"))){
					it.remove();
				}else{
					String rptTime = map.get("REPORT_TIME").toString();
					rptTime = DateUtil.dateNumberToDateStr(rptTime);
					map.put("REPORT_TIME", rptTime);
				}
			}
			return list;
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	private boolean isTravelArea(String zCode, String[] zCodes){
		for(int i=0; i<zCodes.length; i++){
			if(zCode.startsWith(zCodes[i])){
				return true;
			}
		}
		return false;
	}
	
	private DcChinaArea getValidArea(int zCodeLenght, String zCode){
		for(int i=zCodeLenght; i<=zCode.length(); i++){
			String code = zCode.substring(0,i);
			DcChinaArea area = DataCache.dcChinaAreaMap.get(code);
			if(area != null){
				return area;
			}
		}
		return null;
	}
//
//	private void increase(Map<String,Object> model,String property,Long v){
//		Long value = (Long)model.get(property);
//		model.put(property, value == null ? v : (v+value));
//	}
	
}

