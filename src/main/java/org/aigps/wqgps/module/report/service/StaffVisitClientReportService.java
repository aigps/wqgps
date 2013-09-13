package org.aigps.wqgps.module.report.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.WqClientInfo;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.module.report.dao.RegionVisitDAO;
import org.aigps.wqgps.module.report.model.VisitReportModel;
import org.aigps.wqgps.timing.CacheClientStaffIdMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 该类处理员工和客户之间的关系，来获取员工拜访客户的详细情况
 */
@Component
@Transactional
@SuppressWarnings({"unchecked"})
public class StaffVisitClientReportService {
	public final static Log log = LogFactory.getLog(StaffVisitClientReportService.class);
	
	private PublicDAO publicDAO;
	private RegionVisitDAO regionVisitDAO;

	@Autowired
	public void setRegionVisitDAO(RegionVisitDAO regionVisitDAO) {
		this.regionVisitDAO = regionVisitDAO;
	}
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	
	//多员工拜访客户情况查询报表
	public List<VisitReportModel> queryStaffsVisitReport(List<String> staffIds,String startDate,String endDate,Long visitLong) throws Exception{
		Map<String,VisitReportModel> visitMap = new LinkedHashMap<String,VisitReportModel>();
		
		Map<String,List<String>> hadVisitClientMap = new HashMap<String,List<String>>();//员工已经访问的客户ID
		Map<String,List<String>> validVisitClientMap = new HashMap<String,List<String>>();//员工已经访问的有效客户ID
		try{
			//初始化准备变量
			for(String staffId : staffIds){
				String staffName = DataCache.staffMap.get(staffId).getCnName();
				visitMap.put(staffId, new VisitReportModel(staffId,staffName,null));
				
				hadVisitClientMap.put(staffId, new ArrayList<String>());
				validVisitClientMap.put(staffId, new ArrayList<String>());
			}
			
			Map<String,String> staffRegionClientMap = new HashMap<String,String>();//员工ID+区域ID和客户ID之间的对应
			Map<String,WqClientInfo> clientMap = new HashMap<String,WqClientInfo>();//客户ID和客户之间的对应
			
			//每个员工负责的客户资源
			for(String staffId : staffIds){
				Set<String> clientIds = CacheClientStaffIdMap.getClientIds(staffId);
				if(clientIds.isEmpty()){
					continue;
				}
				List<WqClientInfo> clientList = publicDAO.findBy("id", clientIds, WqClientInfo.class);
				for(WqClientInfo client : clientList){
					if(StringUtils.isBlank(client.getRegionIds())){
						continue;
					}
					String[] rids = StringUtils.split(client.getRegionIds(), ",");
					for(String rid : rids){
						staffRegionClientMap.put(staffId+" "+rid, client.getId());
					}
					clientMap.put(client.getId(), client);
				}
			}
			
			//查询这些员工在日期范围内，经过的所有区域记录
			List<Map<String,Object>> visitHisList = regionVisitDAO.findRegionVisitHisByStaffIds(staffIds, startDate, endDate);
			
			for(Map<String,Object> map : visitHisList){
				String staffId = (String)map.get("STAFF_ID");
				String regionId = (String)map.get("REGION_ID");
				
				//员工拜访的区域不是该员工所负责的客户
				String clientId = staffRegionClientMap.get(staffId+" "+regionId);
				if(clientId == null){
					continue;
				}
				VisitReportModel model = visitMap.get(staffId);
				model.visitTimes ++;//拜访客户次数累加
	
				List<String> cIds = hadVisitClientMap.get(staffId);
				if(!cIds.contains(clientId)){
					model.actualVisitCount++;//实际拜访客户数累加
					cIds.add(clientId);
				}
	
				WqClientInfo client = clientMap.get(clientId);
				
				String enterTime = (String)map.get("ENTER_TIME");
				String leaveTime = (String)map.get("LEAVE_TIME");
				
				String maxEnterTime = enterTime.compareTo(startDate+" 00:00:00")>0?enterTime:(startDate+" 00:00:00");
				String minLeaveTime = leaveTime.compareTo(endDate+" 23:59:59")<0?leaveTime:(endDate+" 23:59:59");
				
				Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
				Long validVisitLong = client.getVisitLong().longValue()*60;//将分钟转成秒
				
				if(stayLong >= validVisitLong){//有效拜访
					model.validVisitTimes ++;//有效拜访客户次数累加
					model.validVisitLong += stayLong;//有效拜访总时长累加
					cIds = validVisitClientMap.get(staffId);
					if(!cIds.contains(clientId)){
						model.validVisitCount++;//有效拜访客户数累加
						cIds.add(clientId);
					}
				}
				model.visitTotalLong += stayLong;//拜访总时长累加
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		List<VisitReportModel> list = new ArrayList<VisitReportModel>(visitMap.values());
		
		//过滤掉拜访时长小于visitLong的记录
		for(Iterator<VisitReportModel> it=list.iterator(); it.hasNext(); ){
			VisitReportModel map = it.next();
			if(map.visitTotalLong < visitLong*60){
				it.remove();
			}
		}
		return list;
	}
	

	//单员工拜访客户情况查询报表
	public List<VisitReportModel> queryStaffVisitReport(String staffId,String startDate,String endDate,Long visitLong) throws Exception{
		Map<String,VisitReportModel> visitMap = new LinkedHashMap<String,VisitReportModel>();
		Map<String,List<String>> hadVisitClientMap = new HashMap<String,List<String>>();//员工已经访问的客户ID
		Map<String,List<String>> validVisitClientMap = new HashMap<String,List<String>>();//员工已经访问的有效客户ID
		
		Map<String,WqClientInfo> regionClientMap = new HashMap<String,WqClientInfo>();//区域ID和客户之间的对应
		Map<String,WqClientInfo> clientMap = new HashMap<String,WqClientInfo>();//客户ID和客户之间的对应
		
		try{
			//当前员工负责的客户资源
			Set<String> clientIds = CacheClientStaffIdMap.getClientIds(staffId);
			if(clientIds.isEmpty()){
				return new ArrayList<VisitReportModel>();
			}
			List<WqClientInfo> clientList = publicDAO.findBy("id", clientIds, WqClientInfo.class);
			for(WqClientInfo client : clientList){
				if(StringUtils.isBlank(client.getRegionIds())){
					continue;
				}
				String[] rids = StringUtils.split(client.getRegionIds(), ",");
				for(String rid : rids){
					regionClientMap.put(rid, client);
				}
				clientMap.put(client.getId(), client);
			}
			
			//查询这些员工在日期范围内，经过的所有区域记录
			List<Map<String,Object>> visitHisList = regionVisitDAO.findRegionVisitHisByStaffId(staffId, startDate, endDate);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(DateUtil.dateFormat.parse(startDate));
			String staffName = DataCache.staffMap.get(staffId).getCnName();
			
			while(true){
				String visitDate = DateUtil.dateFormat.format(calendar.getTime());
				visitMap.put(visitDate, new VisitReportModel(staffId,staffName,visitDate));
				
				hadVisitClientMap.put(visitDate, new ArrayList<String>());
				validVisitClientMap.put(visitDate, new ArrayList<String>());
				if(visitDate.equals(endDate)){
					break;
				}
				calendar.add(Calendar.DATE, 1);
			}
			
			for(Map<String,Object> map : visitHisList){
				String regionId = (String)map.get("REGION_ID");
				//员工拜访的区域不是员工负责的客户的区域
				WqClientInfo client = regionClientMap.get(regionId);
				if(client == null){
					continue;
				}
				
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
	
					Map<String,Object> newMap = new HashMap<String,Object>(map);
					//拜访的客户名称
					newMap.put("clientName", client.getName());
					//添加经过的区域
					model.addVisitRecord(newMap);
					model.visitTimes ++;//拜访客户次数累加
		
					List<String> cIds = hadVisitClientMap.get(visitDate);
					if(!cIds.contains(client.getId())){
						model.actualVisitCount++;//实际拜访客户数累加
						cIds.add(client.getId());
						model.addVisitClient(client);
					}
		
					String maxEnterTime = enterTime.compareTo(visitDate+" 00:00:00")>0?enterTime:(visitDate+" 00:00:00");
					String minLeaveTime = leaveTime.compareTo(visitDate+" 23:59:59")<0?leaveTime:(visitDate+" 23:59:59");
					Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
					newMap.put("STAY_LONG", stayLong);
					newMap.put("ENTER_TIME", maxEnterTime);
					newMap.put("LEAVE_TIME", minLeaveTime);
					
					Long validVisitLong = client.getVisitLong().longValue()*60;//将分钟转成秒
					
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
	public Map<String,WqMapRegion> queryMapRegion(List<String> regionIds) throws Exception{
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

