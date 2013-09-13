package org.aigps.wqgps.timing;

import java.util.List;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.WqTradeInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CacheTradeList extends HibernateDAO<WqTradeInfo, String> {
	public final static Log log = LogFactory.getLog(CacheTradeList.class);
	
	public static void startup(){
		new Thread(new Runnable(){
			public void run() { 
				try{
					CacheTradeList refresh = (CacheTradeList) AppUtil.getBean("cacheTradeList");
					DataCache.tradeList = refresh.findAll();
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
	public List<WqTradeInfo> findAll(){
		return this.getAll("id", true);
	}

}

