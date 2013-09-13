/**
 * 
 */
package org.aigps.wqgps.common.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.timing.CacheClientStaffIdMap;
import org.aigps.wqgps.timing.CacheRetrospect;

public class RetrospectUtil {
	
	//�ж��������е������ڵ�ͼ�ϵķ�Χ�Ƿ�һ��
	public static Boolean isDeffRegions(List<WqMapRegion> oldRegionList,List<WqMapRegion> newRegionList){
		//���ǿյģ�������
		if((oldRegionList==null||oldRegionList.isEmpty()) && (newRegionList==null||newRegionList.isEmpty())){
			return false;
		}
		//��С��һ����������
		if((oldRegionList==null||oldRegionList.isEmpty()) && newRegionList!=null && !newRegionList.isEmpty()){
			return true;
		}
		//��С��һ����������
		if((newRegionList==null||newRegionList.isEmpty()) && oldRegionList!=null && !oldRegionList.isEmpty()){
			return true;
		}
		//��С��һ����������
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
			//û�ñȽ�
			if(region == null){
				return true;
			}
			//�Ƚϵ�
			if(!old.getPoints().equals(region.getPoints())){
				return true;
			}
			//�Ƚ�����
			if(!old.getType().equals(region.getType())){
				return true;
			}
			try{
				//�Ƚϰ뾶
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
	 * @param type setȫ�·���ͻ�    addֻ���ָ���ͻ���ԭ�пͻ�����   removeֻɾ��ָ���ͻ���ԭ�пͻ�����
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
		
		if("set".equals(type)){//ȫ�·���ͻ�
			//��ɾ����Ա���ͻ���ϵ
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
			//����ӵ�Ա���ͻ���ϵ
			Set<String[]> addRecord = new HashSet<String[]>();
			for(String clientId : clientIdList){
				Set<String> staffSet = DataCache.clientStaffIdMap.get(clientId);
				for(String staffId : staffIds){
					if(staffSet==null || staffSet.isEmpty() || !staffSet.contains(staffId)){
						addRecord.add(new String[]{clientId,staffId});
					}
				}
			}
			//����ɾ����Ա���Ϳͻ���ϵ�����»��棬ͬʱɾ��׷�ݼ�¼
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
			//����������Ա���Ϳͻ���ϵ�����»��棬ͬʱ���׷�ݼ�¼
			String remark = "ΪԱ����ӿͻ�";
			for(String[] record : addRecord){
				CacheClientStaffIdMap.add(record[0], record[1]);
				Set<String> regionSet = DataCache.clientRegionIdMap.get(record[0]);
				if(regionSet!=null){
					for(String regionId : regionSet){
						CacheRetrospect.add(record[1], regionId, remark);
					}
				}
			}
		}else if("add".equals(type)){//ֻ���ָ���ͻ���ԭ�пͻ�����

			String remark = "ΪԱ����ӿͻ�";
			//����ӵ�Ա���ͻ���ϵ
			for(String clientId : clientIdList){
				Set<String> staffSet = DataCache.clientStaffIdMap.get(clientId);
				Set<String> regionSet = DataCache.clientRegionIdMap.get(clientId);
				for(String staffId : staffIds){
					if(staffSet==null || staffSet.isEmpty() || !staffSet.contains(staffId)){
						CacheRetrospect.add(staffId, regionSet, remark);
					}
				}
			}
			
			//���¿ͻ���Ա����ϵ����
			for(String clientId : clientIdList){
				CacheClientStaffIdMap.add(clientId, staffIds);
			}
			
		}else if("remove".equals(type)){//ֻɾ��ָ���ͻ���ԭ�пͻ�����
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
