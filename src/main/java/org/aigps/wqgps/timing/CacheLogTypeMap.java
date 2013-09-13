package org.aigps.wqgps.timing;

import java.util.List;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.WqLogType;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CacheLogTypeMap extends HibernateDAO<WqLogType, String> {
	public final static Log log = LogFactory.getLog(CacheLogTypeMap.class);
	
	public static void startup(){
		new Thread(new Runnable(){
			public void run() { 
				try{
					CacheLogTypeMap refresh = (CacheLogTypeMap) AppUtil.getBean("cacheLogTypeMap");
					List<WqLogType> list = refresh.findAll();

					for(WqLogType type : list){
						DataCache.logTypeMap.put(type.getLogType(), type);
					}
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
	public List<WqLogType> findAll(){
		return this.getAll("sort", true);
	}

}

