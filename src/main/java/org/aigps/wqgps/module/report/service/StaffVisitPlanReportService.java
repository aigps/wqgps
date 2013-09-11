package org.sunleads.module.report.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.entity.WqClientInfo;
import org.sunleads.common.entity.WqMapRegion;
import org.sunleads.common.entity.WqVisitPlan;
import org.sunleads.common.util.DateUtil;
import org.sunleads.module.report.dao.RegionVisitDAO;
import org.sunleads.module.report.dao.VisitReportDAO;
import org.sunleads.module.report.model.VisitReportModel;
/**
 * 该类处理员工做的计划拜访，来获取员工计划拜访客户的详细情况
 */
@Component
@Transactional
public class StaffVisitPlanReportService {
	public final static Log log = LogFactory.getLog(StaffVisitPlanReportService.class);
	
	private VisitReportDAO visitReportDAO;
	private RegionVisitDAO regionVisitDAO;
	
	@Autowired
	public void setVisitReportDAO(VisitReportDAO visitReportDAO) {
		this.visitReportDAO = visitReportDAO;
	}
	@Autowired
	public void setRegionVisitDAO(RegionVisitDAO regionVisitDAO) {
		this.regionVisitDAO = regionVisitDAO;
	}
	
	//多员工按计划拜访情况查询报表
	public List<VisitReportModel> queryStaffsVisitReport(List<String> staffIds,String startDate,String endDate,Long visitLong) throws Exception{
		Map<String,VisitReportModel> visitMap = new LinkedHashMap<String,VisitReportModel>();
		
		Map<String,List<String>> planVisitClientMap = new HashMap<String,List<String>>(),//员工计划访问的客户ID
		hadVisitClientMap = new HashMap<String,List<String>>(),//员工已经访问的客户ID
		validVisitClientMap = new HashMap<String,List<String>>();//员工已经访问的有效客户ID
		try{
			//初始化准备变量
			for(String staffId : staffIds){
				String staffName = DataCache.staffMap.get(staffId).getCnName();
				visitMap.put(staffId, new VisitReportModel(staffId,staffName,null));
				
				planVisitClientMap.put(staffId, new ArrayList<String>());
				hadVisitClientMap.put(staffId, new ArrayList<String>());
				validVisitClientMap.put(staffId, new ArrayList<String>());
			}
			
			//查询这些员工在日期范围内的所有计划拜访记录
			List<Object[]> vpList = visitReportDAO.findVisitPlan(staffIds, startDate, endDate);
			
			Map<String,String> staffRegionClientMap = new HashMap<String,String>();//员工ID+区域ID和客户ID之间的对应
			Map<String,WqClientInfo> clientMap = new HashMap<String,WqClientInfo>();//客户ID和客户之间的对应
			Map<String,List<String>> staffRegionClientDateMap = new HashMap<String,List<String>>();//员工ID+区域ID和多个拜访日期之间的对应
			
			//对计划拜访的客户进行整理，同时累加计划拜访客户数
			for(Object[] obj : vpList){
				WqClientInfo client = (WqClientInfo) obj[1];
				if(StringUtils.isBlank(client.getRegionIds())){
					continue;
				}
				WqVisitPlan plan = (WqVisitPlan) obj[0];
				String staffId = plan.getStaffId();
				String[] rids = StringUtils.split(client.getRegionIds(), ",");
				for(int i=0; i<rids.length; i++){
					staffRegionClientMap.put(staffId+" "+rids[i], client.getId());
					List<String> dates = (List<String>)staffRegionClientDateMap.get(staffId+" "+rids[i]);
					if(dates==null){
						staffRegionClientDateMap.put(staffId+" "+rids[i], dates=new ArrayList<String>());
					}
					dates.add(plan.getVisitDate());
				}
				clientMap.put(client.getId(), client);
				
				List<String> cIds = planVisitClientMap.get(staffId);
				if(!cIds.contains(client.getId())){
					visitMap.get(staffId).planVisitCount++;//计划拜访客户数累加
					cIds.add(client.getId());
				}
			}
			
			//查询这些员工在日期范围内，经过的所有区域记录
			List<Map<String,Object>> visitHisList = regionVisitDAO.findRegionVisitHisByStaffIds(staffIds, startDate, endDate);
			
			for(Map<String,Object> map : visitHisList){
				String staffId = (String)map.get("STAFF_ID");
				String regionId = (String)map.get("REGION_ID");
				
				//员工拜访的区域不是通过计划安排客户的
				String clientId = staffRegionClientMap.get(staffId+" "+regionId);
				if(clientId == null){
					continue;
				}
				String enterTime = (String)map.get("ENTER_TIME");
				String leaveTime = (String)map.get("LEAVE_TIME");
				if(StringUtils.isBlank(enterTime)||StringUtils.isBlank(leaveTime)){
					continue;
				}
				
				String enterDate = enterTime.substring(0,10);
				String leaveDate= leaveTime.substring(0,10);
				
				List<String> visitDates = staffRegionClientDateMap.get(staffId+" "+regionId);
				
				for(String visitDate : visitDates){
					//访问区域的日期，并不是拜访计划中定制的日期
					if(visitDate.compareTo(enterDate)<0 || visitDate.compareTo(leaveDate)>0){
						continue;
					}
	
					WqClientInfo client = clientMap.get(clientId);
	
					String maxEnterTime = enterTime.compareTo(visitDate+" 00:00:00")>0?enterTime:(visitDate+" 00:00:00");
					String minLeaveTime = leaveTime.compareTo(visitDate+" 23:59:59")<0?leaveTime:(visitDate+" 23:59:59");
					
					Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
					Long validVisitLong = client.getVisitLong().longValue()*60;//将分钟转成秒
					
					VisitReportModel model = visitMap.get(staffId);
					if(stayLong >= validVisitLong){//有效拜访
						model.validVisitTimes ++;//有效拜访客户次数累加
						model.validVisitLong += stayLong;//有效拜访总时长累加
						List<String> cIds = validVisitClientMap.get(staffId);
						if(!cIds.contains(clientId)){
							model.validVisitCount++;//有效拜访客户数累加
							cIds.add(clientId);
						}
					}
					model.visitTotalLong += stayLong;//拜访总时长累加
					model.visitTimes ++;//拜访客户次数累加
					List<String> cIds = hadVisitClientMap.get(staffId);
					if(!cIds.contains(clientId)){
						model.actualVisitCount++;//实际拜访客户数累加
						cIds.add(clientId);
					}
				}
	
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		List<VisitReportModel> list = new ArrayList<VisitReportModel>(visitMap.values());
		for(Iterator<VisitReportModel> it=list.iterator(); it.hasNext(); ){
			VisitReportModel map = it.next();
			if(map.visitTotalLong < visitLong*60){
				it.remove();
			}
		}
		return list;
	}
	

	//单员工按计划拜访情况查询报表
	public List<VisitReportModel> queryStaffVisitReport(String staffId,String startDate,String endDate,Long visitLong) throws Exception{
		Map<String,VisitReportModel> visitMap = new LinkedHashMap<String,VisitReportModel>();
		Map<String,List<String>> planVisitClientMap = new HashMap<String,List<String>>(),//员工计划访问的客户ID
		hadVisitClientMap = new HashMap<String,List<String>>(),//员工已经访问的客户ID
		validVisitClientMap = new HashMap<String,List<String>>();//员工已经访问的有效客户ID
		
		try{
			//查询员工在日期范围内的所有计划拜访记录
			List<Object[]> vpList = visitReportDAO.findVisitPlan(staffId, startDate, endDate);
			for(Object[] obj : vpList){
				String visitDate = ((WqVisitPlan) obj[0]).getVisitDate();
				String staffName = DataCache.staffMap.get(staffId).getCnName();
				visitMap.put(visitDate, new VisitReportModel(staffId,staffName,visitDate));
				
				planVisitClientMap.put(visitDate, new ArrayList<String>());
				hadVisitClientMap.put(visitDate, new ArrayList<String>());
				validVisitClientMap.put(visitDate, new ArrayList<String>());
			}
			
			Map<String,WqClientInfo> regionClientMap = new HashMap<String,WqClientInfo>();//区域ID和客户之间的对应
			//对计划拜访的客户进行整理，同时累加计划拜访客户数
			for(Object[] obj : vpList){
				WqClientInfo client = (WqClientInfo) obj[1];
				if(StringUtils.isBlank(client.getRegionIds())){
					continue;
				}
				WqVisitPlan plan = (WqVisitPlan) obj[0];
				String visitDate = plan.getVisitDate();
				String[] rids = StringUtils.split(client.getRegionIds(), ",");
				for(int i=0; i<rids.length; i++){
					regionClientMap.put(rids[i], client);
				}
				
				VisitReportModel model = visitMap.get(visitDate);
				//添加未按计划拜访的客户，等下如果拜访了，会从里面删除，剩下的就是真正未拜访的
				model.addNotVisitClientRecord(client);
				
				List<String> cIds = planVisitClientMap.get(visitDate);
				if(!cIds.contains(client.getId())){
					model.planVisitCount++;//计划拜访客户数累加
					cIds.add(client.getId());
				}
			}
			
			//查询这些员工在日期范围内，经过的所有区域记录
			List<Map<String,Object>> visitHisList = regionVisitDAO.findRegionVisitHisByStaffId(staffId, startDate, endDate);
	
			for(Map<String,Object> map : visitHisList){
				String regionId = (String)map.get("REGION_ID");
	
				//员工拜访的区域不是通过计划安排客户的
				WqClientInfo client = regionClientMap.get(regionId);
				if(client == null){
					continue;
				}
				//拜访的客户名称
				map.put("clientName", client.getName());
				
				String enterTime = (String)map.get("ENTER_TIME");
				String leaveTime = (String)map.get("LEAVE_TIME");
				String enterDate = enterTime.substring(0,10);
				String leaveDate= leaveTime.substring(0,10);
				
				for(String visitDate : visitMap.keySet()){
					//访问区域的日期，并不是拜访计划中定制的日期
					if(visitDate.compareTo(enterDate)<0 || visitDate.compareTo(leaveDate)>0){
						continue;
					}
					VisitReportModel model = visitMap.get(visitDate);
					
					//client客户被拜访了，要从未被拜访中删除
					model.removeNotVisitClient(client.getId());
	
					//添加经过的区域
					Map<String,Object> newMap = new HashMap<String,Object>(map);
					model.addVisitRecord(newMap);
					
					model.visitTimes ++;//拜访客户次数累加
	
					List<String> cIds = hadVisitClientMap.get(visitDate);
					if(!cIds.contains(client.getId())){
						model.actualVisitCount++;//实际拜访客户数累加
						cIds.add(client.getId());
						model.addVisitClient(client);
					}
					
					Long validVisitLong = client.getVisitLong().longValue()*60;//将分钟转成秒
					String maxEnterTime = enterTime.compareTo(visitDate+" 00:00:00")>0?enterTime:(visitDate+" 00:00:00");
					String minLeaveTime = leaveTime.compareTo(visitDate+" 23:59:59")<0?leaveTime:(visitDate+" 23:59:59");
					Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
	
					newMap.put("STAY_LONG", stayLong);
					newMap.put("ENTER_TIME", maxEnterTime);
					newMap.put("LEAVE_TIME", minLeaveTime);
					
					if(stayLong >= validVisitLong){//有效拜访
						model.validVisitTimes ++;//有效拜访客户次数累加
						model.validVisitLong += stayLong;//有效拜访总时长累加
						cIds = validVisitClientMap.get(visitDate);
						if(!cIds.contains(client.getId())){
							model.validVisitCount++;//有效拜访客户数累加
							cIds.add(client.getId());
						}
						newMap.put("isValidVisit", true);//有效拜访
					}else{
						newMap.put("isValidVisit", false);//无效拜访
					}
					model.visitTotalLong += stayLong;//拜访总时长累加
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		List<VisitReportModel> list = new ArrayList<VisitReportModel>(visitMap.values());
		for(Iterator<VisitReportModel> it=list.iterator(); it.hasNext(); ){
			VisitReportModel map = it.next();
			if(map.visitTotalLong < visitLong*60){
				it.remove();
			}
		}
		return list;
	}
	
	//通过区域ID集合，查找区域对象，用以在地图上显示这个人走过的客户区域
	public Map<String,WqMapRegion> queryMapRegion(List<String> regionIds){
		Map<String,WqMapRegion> map = new HashMap<String,WqMapRegion>();
		for(String rid : regionIds){
			WqMapRegion region = DataCache.mapRegionMap.get(rid);
			if(region != null){
				map.put(rid, region);
			}
		}
		return map;
	}
}

