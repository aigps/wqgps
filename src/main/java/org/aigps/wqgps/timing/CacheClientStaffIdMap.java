package org.aigps.wqgps.timing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aigps.wqgps.auth.AuthType;
import org.aigps.wqgps.auth.dao.AuthDAO;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.WqAuthObj;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CacheClientStaffIdMap {
	public final static Log log = LogFactory.getLog(CacheClientStaffIdMap.class);
	public static Boolean finish = false;

	private JdbcTemplate jdbcTemplate;
	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public JdbcTemplate getJdbcTemplate(){
		return jdbcTemplate;
	}
	
	public static void startup(){
		Thread t = new Thread(new Runnable(){
			public void run() { 
				while(true){
					try{
						//��DataCache.authObjMap��ʼ������
						Thread.sleep(1000);
						
						WqAuthObj authObj = DataCache.authObjMap.get(AuthType.STAFF_CLIENT);
						//Ա���Ϳͻ�����Ĺ�ϵ
						if(authObj != null){
							CacheClientStaffIdMap refresh = (CacheClientStaffIdMap)AppUtil.getBean("cacheClientStaffIdMap");
							//ϵͳ����Ա���Ϳͻ�֮��Ĺ�ϵȨ���б�
							List<Map<String,Object>> authList = AuthDAO.getListByObjId(authObj.getId(), refresh.getJdbcTemplate());
	
							HashMap<String,Set<String>> csMap = new HashMap<String,Set<String>>();
							for(Map<String,Object> map : authList){
								String staffId = (String)map.get("STAFF_ID");
								String clientId = (String)map.get("RES_ID");
								
								Set<String> staffSet = csMap.get(clientId);
								if(staffSet == null){
									csMap.put(clientId, staffSet=new HashSet<String>());
								}
								staffSet.add(staffId);
							}
							DataCache.clientStaffIdMap = csMap;
							finish = true;
							break;
						}
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}
	
	public static void delete(String clientId,List<String> staffIds){
		Set<String> ids = DataCache.clientStaffIdMap.get(clientId);
		if(ids != null){
			ids.removeAll(staffIds);
		}
	}

	public static void delete(String clientId,String staffId){
		Set<String> ids = DataCache.clientStaffIdMap.get(clientId);
		if(ids != null){
			ids.remove(staffId);
		}
	}
	
	public static void add(String clientId,List<String> staffIds){
		Set<String> ids = DataCache.clientStaffIdMap.get(clientId);
		if(staffIds == null){
			DataCache.clientStaffIdMap.put(clientId, ids=new HashSet<String>());
		}
		ids.addAll(staffIds);
	}

	public static void add(String clientId,String staffId){
		Set<String> ids = DataCache.clientStaffIdMap.get(clientId);
		if(ids == null){
			DataCache.clientStaffIdMap.put(clientId, ids=new HashSet<String>());
		}
		ids.add(staffId);
	}
	
	//��ȡԱ����Ȩ�޵Ŀͻ�
	public static Set<String> getClientIds(String staffId){
		Set<String> clientIds = new HashSet<String>();
		for(Map.Entry<String, Set<String>> entry : DataCache.clientStaffIdMap.entrySet()){
			Set<String> staffSet = entry.getValue();
			if(staffSet.contains(staffId)){
				clientIds.add(entry.getKey());
			}
		}
		return clientIds;
	}
}

