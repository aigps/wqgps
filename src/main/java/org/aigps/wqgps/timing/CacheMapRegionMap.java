package org.sunleads.timing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.HibernateDAO;
import org.sunleads.common.entity.WqMapRegion;
import org.sunleads.common.util.AppUtil;

@Component
@Transactional
public class CacheMapRegionMap extends HibernateDAO<WqMapRegion, String> {
	public final static Log log = LogFactory.getLog(CacheMapRegionMap.class);
	
	public static void startup(){
		new Thread(new Runnable(){
			public void run() { 
				try{
					CacheMapRegionMap refresh = (CacheMapRegionMap) AppUtil.getBean("cacheMapRegionMap");
					List<WqMapRegion> list = refresh.findAll();
					
					Map<String,WqMapRegion> map = new HashMap<String,WqMapRegion>();
					for(WqMapRegion model : list){
						map.put(model.getId(), model);
					}
					DataCache.mapRegionMap = map;
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}).start();
	}
	
	/**
	 * 获取所有系统菜单
	 * @return
	 */
	public List<WqMapRegion> findAll(){
		return this.getAll();
	}

}

