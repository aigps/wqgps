package org.sunleads.timing;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.HibernateDAO;
import org.sunleads.common.entity.WqMenuInfo;
import org.sunleads.common.util.AppUtil;

@Component
@Transactional
public class CacheMenuList extends HibernateDAO<WqMenuInfo, String> {
	public final static Log log = LogFactory.getLog(CacheMenuList.class);
	
	public static void startup(){
		new Thread(new Runnable(){
			public void run() { 
				try{
					CacheMenuList refresh = (CacheMenuList) AppUtil.getBean("cacheMenuList");
					DataCache.menuList = refresh.findAll();
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
	public List<WqMenuInfo> findAll(){
		return this.getAll("sort", true);
	}

}

