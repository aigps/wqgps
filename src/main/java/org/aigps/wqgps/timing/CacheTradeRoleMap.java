package org.aigps.wqgps.timing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.WqTradeRole;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.Collections;

@Component
@Transactional
public class CacheTradeRoleMap extends HibernateDAO<WqTradeRole, String> {
	public final static Log log = LogFactory.getLog(CacheTradeRoleMap.class);
	
	public static void startup(){
		new Thread(new Runnable(){
			public void run() { 
				try{
					CacheTradeRoleMap refresh = (CacheTradeRoleMap) AppUtil.getBean("cacheTradeRoleMap");
					List<WqTradeRole> list = refresh.findAll();

					for(WqTradeRole trade : list){
						DataCache.tradeRoleMap.put(trade.getId(), trade);
					}
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}).start();
	}
	
	public List<WqTradeRole> findAll(){
		return this.getAll("tradeName", true);
	}
	
	public synchronized static void saveOrUpdateCache(WqTradeRole trade){

		Map<String,WqTradeRole> tradeRoleMap = new LinkedHashMap<String,WqTradeRole>(DataCache.tradeRoleMap);
		tradeRoleMap.put(trade.getId(), trade);
		List<WqTradeRole> list = new ArrayList<WqTradeRole>(tradeRoleMap.values());
		
		Collections.sort(list, new Comparator<WqTradeRole>() {
			public int compare(WqTradeRole o1, WqTradeRole o2) {
				return o1.getTradeName().compareTo(o2.getTradeName());
			}
		});
		
		tradeRoleMap.clear();
		for(WqTradeRole t : list){
			tradeRoleMap.put(t.getId(), t);
		}
		
		DataCache.tradeRoleMap = tradeRoleMap;
	}
	
	public synchronized static void deleteCache(String id){
		Map<String,WqTradeRole> tradeRoleMap = new LinkedHashMap<String,WqTradeRole>(DataCache.tradeRoleMap);
		tradeRoleMap.remove(id);
		DataCache.tradeRoleMap = tradeRoleMap;
	}
}

