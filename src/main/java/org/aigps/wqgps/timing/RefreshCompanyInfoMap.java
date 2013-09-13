package org.aigps.wqgps.timing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.WqCompanyInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.TimingUtil;
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
public class RefreshCompanyInfoMap extends HibernateDAO<WqCompanyInfo, String> {
	public final static Log log = LogFactory.getLog(RefreshCompanyInfoMap.class);
	
	/**
	 * ������ʼ�������ȡϵͳ����������Ϣ
	 */
	public static void startup(){
		Thread t = new Thread(new Runnable(){
			public void run() { 
				while(true){
					try {
						RefreshCompanyInfoMap refresh = (RefreshCompanyInfoMap) AppUtil.getBean("refreshCompanyInfoMap");
						List<WqCompanyInfo> list = refresh.findAll();
						
						Map<String,WqCompanyInfo> map = new HashMap<String,WqCompanyInfo>();
						for(WqCompanyInfo ci : list){
							if(ci.getIsEnable() == false){
								continue;
							}
							map.put(ci.getId(),ci);
						}
						DataCache.companyInfoMap = map;
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					} finally {
						try{
							Thread.sleep(TimingUtil.getForInt("refresh.company.interval"));
						} catch (Exception e) {
							log.error(e.getMessage(),e);
						}
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}
	
	/**
	 * ��ȡ����ϵͳ����������Ϣ
	 * @return
	 */
	public List<WqCompanyInfo> findAll(){
		return this.getAll();
	}

}

