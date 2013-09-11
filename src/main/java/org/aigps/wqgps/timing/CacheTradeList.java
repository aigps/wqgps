package org.sunleads.timing;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.HibernateDAO;
import org.sunleads.common.entity.WqTradeInfo;
import org.sunleads.common.util.AppUtil;

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
	 * ��ȡ����ϵͳ�˵�
	 * @return
	 */
	public List<WqTradeInfo> findAll(){
		return this.getAll("id", true);
	}

}

