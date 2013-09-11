/**
 * 
 */
package org.sunleads.common.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sunleads.common.cache.DataCache;
import org.sunleads.common.entity.WqMapRegion;
import org.sunleads.timing.CacheClientStaffIdMap;
import org.sunleads.timing.CacheRetrospect;

public class RetrospectUtil {
	
	//判断两数组中的区域，在地图上的范围是否一致
	public static Boolean isDeffRegions(List<WqMapRegion> oldRegionList,List<WqMapRegion> newRegionList){
		//都是空的，无区别
		if((oldRegionList==null||oldRegionList.isEmpty()) && (newRegionList==null||newRegionList.isEmpty())){
			return false;
		}
		//大小不一样，有区域
		if((oldRegionList==null||oldRegionList.isEmpty()) && newRegionList!=null && !newRegionList.isEmpty()){
			return true;
		}
		//大小不一样，有区域
		if((newRegionList==null||newRegionList.isEmpty()) && oldRegionList!=null && !oldRegionList.isEmpty()){
			return true;
		}
		//大小不一样，有区域
		if(oldRegionList.size()!=newRegionList.size()){
			return true;
		}
		for(WqMapRegion old : oldRegionList){
			WqMapRegion region = null;
			for(WqMapRegion news : newRegionList){
				if(old.getId().equals(news.getId())){
					region=news;
					break;
				}
			}
			//没得比较
			if(region == null){
				return true;
			}
			//比较点
			if(!old.getPoints().equals(region.getPoints())){
				return true;
			}
			//比较类型
			if(!old.getType().equals(region.getType())){
				return true;
			}
			try{
				//比较半径
				if(old.getRadius().doubleValue() != region.getRadius().doubleValue()){
					return true;
				}
			}catch(Exception e){}
		}
		return false;
	}
	
	/**
	 * 
	 * @param staffList
	 * @param clientIdList
	 * @param type set全新分配客户    add只添加指定客户，原有客户保留   remove只删除指定客户，原有客户保留
	 */
	public static List<String[]> updateStaffClient(List<String> staffIds,List<String> clientIdList,String type){
		List<String> regionIds = new ArrayList<String>();
		for(String clientId : clientIdList){
			Set<String> set = DataCache.clientRegionIdMap.get(clientId);
			if(set != null){
				regionIds.addAll(set);
			}
		}
		List<String[]> deleteStaffRegionList = new ArrayList<String[]>();
		
		if("set".equals(type)){//全新分配客户
			//被删除的员工客户关系
			Set<String[]> deleteRecord = new HashSet<String[]>();
			for(Map.Entry<String, Set<String>> entry : DataCache.clientStaffIdMap.entrySet()){
				String clientId = entry.getKey();
				Set<String> staffSet = entry.getValue();
				Boolean hasClient = clientIdList.contains(clientId);
				for(String staffId : staffSet){
					Boolean hasStaff = staffIds.contains(staffId);
					if(hasClient==false && hasStaff==true){
						deleteRecord.add(new String[]{clientId,staffId});
					}
				}
			}
			//新添加的员工客户关系
			Set<String[]> addRecord = new HashSet<String[]>();
			for(String clientId : clientIdList){
				Set<String> staffSet = DataCache.clientStaffIdMap.get(clientId);
				for(String staffId : staffIds){
					if(staffSet==null || staffSet.isEmpty() || !staffSet.contains(staffId)){
						addRecord.add(new String[]{clientId,staffId});
					}
				}
			}
			//对于删除的员工和客户关系，更新缓存，同时删除追溯记录
			for(String[] record : deleteRecord){
				CacheClientStaffIdMap.delete(record[0], record[1]);
				Set<String> regionSet = DataCache.clientRegionIdMap.get(record[0]);
				if(regionSet!=null){
					for(String regionId : regionSet){
						CacheRetrospect.delete(record[1], regionId);
						deleteStaffRegionList.add(new String[]{record[1], regionId});
					}
				}
			}
			//对于新增的员工和客户关系，更新缓存，同时添加追溯记录
			String remark = "为员工添加客户";
			for(String[] record : addRecord){
				CacheClientStaffIdMap.add(record[0], record[1]);
				Set<String> regionSet = DataCache.clientRegionIdMap.get(record[0]);
				if(regionSet!=null){
					for(String regionId : regionSet){
						CacheRetrospect.add(record[1], regionId, remark);
					}
				}
			}
		}else if("add".equals(type)){//只添加指定客户，原有客户保留

			String remark = "为员工添加客户";
			//新添加的员工客户关系
			for(String clientId : clientIdList){
				Set<String> staffSet = DataCache.clientStaffIdMap.get(clientId);
				Set<String> regionSet = DataCache.clientRegionIdMap.get(clientId);
				for(String staffId : staffIds){
					if(staffSet==null || staffSet.isEmpty() || !staffSet.contains(staffId)){
						CacheRetrospect.add(staffId, regionSet, remark);
					}
				}
			}
			
			//更新客户和员工关系缓存
			for(String clientId : clientIdList){
				CacheClientStaffIdMap.add(clientId, staffIds);
			}
			
		}else if("remove".equals(type)){//只删除指定客户，原有客户保留
			for(String clientId : clientIdList){
				CacheClientStaffIdMap.delete(clientId, staffIds);
			}
			for(String regionId : regionIds){
				for(String staffId:staffIds){
					CacheRetrospect.delete(staffId,regionId);
					deleteStaffRegionList.add(new String[]{staffId, regionId});
				}
			}
		}
		
		return deleteStaffRegionList;
	}
	
}
