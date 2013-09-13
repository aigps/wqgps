package org.aigps.wqgps.timing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.Arrays;

@Component
@Transactional
public class CacheClientRegionIdMap {
	public final static Log log = LogFactory.getLog(CacheClientRegionIdMap.class);
	public static Boolean finish = false;

	private JdbcTemplate jdbcTemplate;
	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public static void startup(){
		new Thread(new Runnable(){
			public void run() { 
				try{
					CacheClientRegionIdMap refresh = (CacheClientRegionIdMap)AppUtil.getBean("cacheClientRegionIdMap");
					DataCache.clientRegionIdMap = refresh.getClientRegionMap();
					finish = true;
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}).start();
	}

	//系统所有客户ID和客户所在区域ID的对照关系
	public Map<String,Set<String>> getClientRegionMap(){
		String sql = "SELECT ID,REGION_IDS FROM WQ_CLIENT_INFO WHERE IS_ENABLE!=0 AND REGION_IDS IS NOT NULL";
		List<Map<String,Object>> list = this.jdbcTemplate.queryForList(sql);
		
		Map<String,Set<String>> returnMap = new HashMap<String,Set<String>>();
		for(Map<String,Object> map : list){
			Set<String> set = new HashSet<String>();
			String regionIds = (String)map.get("REGION_IDS");
			if(StringUtils.isNotBlank(regionIds)){
				set.addAll(Arrays.asList(regionIds.split(",")));
			}
			returnMap.put(map.get("ID").toString(), set);
		}
		
		return returnMap;
	}

	public static void update(String clientId,String regionIds){
		Set<String> set = new HashSet<String>();
		if(StringUtils.isNotBlank(regionIds)){
			set.addAll(Arrays.asList(regionIds.split(",")));
		}
		DataCache.clientRegionIdMap.put(clientId, set);
	}
	
	public static Set<String> getRegionIds(Collection<String> clientIds){
		Set<String> regionIds = new HashSet<String>();
		for(String clientId : clientIds){
			Set<String> ids = DataCache.clientRegionIdMap.get(clientId);
			if(ids != null && !ids.isEmpty()){
				regionIds.addAll(ids);
			}
		}
		return regionIds;
	}
}

