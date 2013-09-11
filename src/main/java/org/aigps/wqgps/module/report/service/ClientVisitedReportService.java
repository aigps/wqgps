package org.sunleads.module.report.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.WqClientInfo;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.util.DateUtil;
import org.sunleads.module.report.dao.RegionVisitDAO;

import edu.emory.mathcs.backport.java.util.Arrays;
/**
 * 该类处理客户被员工拜访的详细情况
 */
@Component
@Transactional
@SuppressWarnings({"rawtypes","unchecked"})
public class ClientVisitedReportService {
	public final static Log log = LogFactory.getLog(ClientVisitedReportService.class);
	
	private PublicDAO publicDAO;
	private RegionVisitDAO regionVisitDAO;
	
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	@Autowired
	public void setRegionVisitDAO(RegionVisitDAO regionVisitDAO) {
		this.regionVisitDAO = regionVisitDAO;
	}
	
	private void increase(Map model,String property,Long v){
		Long value = (Long)model.get(property);
		model.put(property, value == null ? 0l : (v == null ? ++value : (v+value)));
	}
	
	//多客户被员工拜访情况查询报表
	public List<Map<String,Object>> queryClientsVisitedReport(List<String> clientIds,String startDate,String endDate,Long visitLong) throws Exception{
		Map<String,Map<String,Object>> visitedMap = new LinkedHashMap<String,Map<String,Object>>();
		
		try{
			//初始化返回的数据
			for(String clientId : clientIds){
				Map<String,Object> model = new HashMap<String,Object>();
				visitedMap.put(clientId, model);
				
				model.put("clientId", clientId);
				model.put("visitedTimes", 0L);//初始化被拜访次数
				model.put("validVisitedTimes", 0L);//初始化有效被拜访次数
				model.put("validVisitedLong", 0L);//初始化有效被拜访时长
				model.put("visitedLong", 0L);//初始化总被拜访时长
			}
			
			//查询的客户对象
			List<WqClientInfo> clients = publicDAO.findBy("id", clientIds, WqClientInfo.class);
			
			Map<String,WqClientInfo> regionClientMap = new HashMap<String,WqClientInfo>();//区域ID和客户的对照关系
			Set<String> regionIds = new HashSet<String>();//客户的所有区域ID集合
			for(WqClientInfo client : clients){
				Map<String,Object> model = visitedMap.get(client.getId());
				model.put("clientName", client.getName());//客户名称
				
				if(StringUtils.isNotBlank(client.getRegionIds())){
					String[] rids = StringUtils.split(client.getRegionIds(), ",");
					regionIds.addAll(Arrays.asList(rids));
					for(String rid : rids){
						regionClientMap.put(rid, client);
					}
				}
			}
			
			//查询这些区域在日期范围内，被经过的所有记录
			List<Map<String,Object>> visitedHisList = regionVisitDAO.findRegionVisitHisByRegionIds(regionIds, startDate, endDate);
			for(Map<String,Object> map : visitedHisList){
				String regionId = (String)map.get("REGION_ID");
				WqClientInfo client = regionClientMap.get(regionId);
				
				Map<String,Object> model = visitedMap.get(client.getId());
				increase(model,"visitedTimes",1L);//被拜访次数累加
	
				String enterTime = (String)map.get("ENTER_TIME");
				String leaveTime = (String)map.get("LEAVE_TIME");
				
				//将查询时间和进出时间进行比较，取出合理的时间
				String maxEnterTime = enterTime.compareTo(startDate+" 00:00:00")>0?enterTime:(startDate+" 00:00:00");
				String minLeaveTime = leaveTime.compareTo(endDate+" 23:59:59")<0?leaveTime:(endDate+" 23:59:59");
				
				Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
				Long validVisitLong = client.getVisitLong().longValue()*60;//将分钟转成秒
				
				if(stayLong >= validVisitLong){//有效拜访
					increase(model,"validVisitedTimes",1L);//有效被拜访次数累加
					increase(model,"validVisitedLong",stayLong);//有效被拜访时长累加
				}
				increase(model,"visitedLong",stayLong);//被拜访时长累加
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>(visitedMap.values());
		
		//过滤时长小于visitLong值的
		for(Iterator<Map<String,Object>> it=list.iterator(); it.hasNext(); ){
			Long vl = (Long)it.next().get("visitedLong");
			if(vl < visitLong*60){
				it.remove();
			}
		}
		return list;
	}
	

	//单客户被拜访情况报表
	public List<Map<String,Object>> queryClientVisitedReport(String clientId,String startDate,String endDate) throws Exception{
		Map<String,Map<String,Object>> visitMap = new LinkedHashMap<String,Map<String,Object>>();
		
		try{
			//查询的客户对象
			WqClientInfo client = (WqClientInfo)publicDAO.get(clientId, WqClientInfo.class);
			
			String[] rids = StringUtils.split(client.getRegionIds(), ",");
	
			//查询这个客户区域在日期范围内，被经过的所有记录
			List<Map<String,Object>> visitedHisList = regionVisitDAO.findRegionVisitHisByRegionIds(Arrays.asList(rids), startDate, endDate);
	
			//初始化返回数据
			for(Map<String,Object> map : visitedHisList){
				String staffId = (String)map.get("STAFF_ID");
				if(!visitMap.containsKey(staffId)){
					WqStaffInfo staff = DataCache.staffMap.get(staffId);
					if(staff == null){
						continue;
					}
					Map<String,Object> model = new HashMap<String,Object>();
					visitMap.put(staffId, model);
	
					model.put("staffId", staffId);
					model.put("staffName", staff.getCnName());
					model.put("clientId", clientId);
					model.put("clientName", client.getName());
					model.put("visitTimes", 0L);//初始化员工拜访次数
					model.put("validVisitTimes", 0L);//初始化员工有效拜访次数
					model.put("validVisitLong", 0L);//初始化员工有效拜访时长
					model.put("visitLong", 0L);//初始化员工总拜访时长
				}
			}
			
			for(Map<String,Object> map : visitedHisList){
				String staffId = (String)map.get("STAFF_ID");
				Map<String,Object> model = visitMap.get(staffId);
				if(model == null){
					continue;
				}
				increase(model,"visitTimes",1L);//员工拜访次数累加
	
				String enterTime = (String)map.get("ENTER_TIME");
				String leaveTime = (String)map.get("LEAVE_TIME");
				
				String maxEnterTime = enterTime.compareTo(startDate+" 00:00:00")>0?enterTime:(startDate+" 00:00:00");
				String minLeaveTime = leaveTime.compareTo(endDate+" 23:59:59")<0?leaveTime:(endDate+" 23:59:59");
				
				Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
				Long validVisitLong = client.getVisitLong().longValue()*60;//将分钟转成秒
				
				if(stayLong >= validVisitLong){//有效拜访
					increase(model,"validVisitTimes",1L);//员工有效拜访次数累加
					increase(model,"validVisitLong",stayLong);//员工有效拜访时长累加
				}
				increase(model,"visitLong",stayLong);//员工拜访时长累加
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		return new ArrayList<Map<String,Object>>(visitMap.values());
	}

	//单客户被单员工拜访情况报表
	public List<Map<String,Object>> querySingleVisitedReport(String clientId,String staffId,String startDate,String endDate) throws Exception{
		try{
			//查询的客户对象
			WqClientInfo client = (WqClientInfo)publicDAO.get(clientId, WqClientInfo.class);
			String[] rids = StringUtils.split(client.getRegionIds(), ",");
			
			//查询这个客户区域在日期范围内，被经过的所有记录
			List<Map<String,Object>> visitedHisList = regionVisitDAO.findRegionVisitHisByRegionIds(Arrays.asList(rids), startDate, endDate);
			
			for(Iterator<Map<String,Object>> it=visitedHisList.iterator(); it.hasNext(); ){
				Map<String,Object> map = it.next();
				if(!staffId.equals(map.get("STAFF_ID"))){
					it.remove();
				}else{
					Long validVisitLong = client.getVisitLong().longValue()*60;//将分钟转成秒
					
					String enterTime = (String)map.get("ENTER_TIME");
					String leaveTime = (String)map.get("LEAVE_TIME");
					
					String maxEnterTime = enterTime.compareTo(startDate+" 00:00:00")>0?enterTime:(startDate+" 00:00:00");
					String minLeaveTime = leaveTime.compareTo(endDate+" 23:59:59")<0?leaveTime:(endDate+" 23:59:59");
					Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
					
					map.put("STAY_LONG", stayLong);
					map.put("ENTER_TIME", maxEnterTime);
					map.put("LEAVE_TIME", minLeaveTime);
					map.put("STATE", stayLong >= validVisitLong ? "有效拜访" : "无效拜访");
				}
			}
			return visitedHisList;
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
	}
}