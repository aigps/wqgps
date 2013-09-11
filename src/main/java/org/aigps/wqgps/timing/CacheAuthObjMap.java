package org.sunleads.timing;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.HibernateDAO;
import org.sunleads.common.entity.WqAuthObj;
import org.sunleads.common.util.AppUtil;

/**
 * 定时间隔获取系统参数配置信息
 * @author Administrator
 *
 */
@Component
@Transactional
public class CacheAuthObjMap extends HibernateDAO<WqAuthObj, String> {
	public final static Log log = LogFactory.getLog(CacheAuthObjMap.class);
	
	/**
	 * 启动初始化间隔获取系统参数配置信息
	 */
	public static void startup(){
		new Thread(new Runnable(){
			public void run() { 
				try {
					CacheAuthObjMap refresh = (CacheAuthObjMap) AppUtil.getBean("cacheAuthObjMap");
					List<WqAuthObj> list = refresh.findAll();
					
					for(WqAuthObj ao : list){
						DataCache.authObjMap.put(ao.getType(), ao);
					}
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}).start();
	}
	
	/**
	 * 获取所有系统参数配置信息
	 * @return
	 */
	public List<WqAuthObj> findAll(){
		return this.getAll();
	}

}

