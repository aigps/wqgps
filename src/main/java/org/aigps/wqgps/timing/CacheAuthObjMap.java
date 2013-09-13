package org.aigps.wqgps.timing;

import java.util.List;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.WqAuthObj;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ��ʱ�����ȡϵͳ����������Ϣ
 * @author Administrator
 *
 */
@Component
@Transactional
public class CacheAuthObjMap extends HibernateDAO<WqAuthObj, String> {
	public final static Log log = LogFactory.getLog(CacheAuthObjMap.class);
	
	/**
	 * ������ʼ�������ȡϵͳ����������Ϣ
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
	 * ��ȡ����ϵͳ����������Ϣ
	 * @return
	 */
	public List<WqAuthObj> findAll(){
		return this.getAll();
	}

}

