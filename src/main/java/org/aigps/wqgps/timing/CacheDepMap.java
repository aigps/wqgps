package org.aigps.wqgps.timing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.WqDepInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CacheDepMap extends HibernateDAO<WqDepInfo, String> {
	public final static Log log = LogFactory.getLog(CacheDepMap.class);
	
	public static void startup(){
		new Thread(new Runnable(){
			public void run() { 
				try{
					CacheDepMap refresh = (CacheDepMap) AppUtil.getBean("cacheDepMap");
					List<WqDepInfo> list = refresh.findAll();
					
					Map<String,List<WqDepInfo>> depMap = new HashMap<String,List<WqDepInfo>>();
					Map<String,WqDepInfo> depIdMap = new HashMap<String,WqDepInfo>();
					for(WqDepInfo dep : list){
						if(dep.getIsEnable() == false){
							continue;
						}
						List<WqDepInfo> depList = depMap.get(dep.getCompanyId());
						if(depList == null){
							depList = new CopyOnWriteArrayList<WqDepInfo>();
							depMap.put(dep.getCompanyId(), depList);
						}
						depList.add(dep);
						depIdMap.put(dep.getId(), dep);
					}
					DataCache.comDepMap = depMap;
					DataCache.depMap = depIdMap;
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
	public List<WqDepInfo> findAll(){
		return this.getAll("name",true);
	}

	//�Բ��Ż�����޸�
	public static void addDep(WqDepInfo dep){
		DataCache.depMap.put(dep.getId(), dep);
		List<WqDepInfo> deplist = DataCache.comDepMap.get(dep.getCompanyId());
		if(deplist == null){
			DataCache.comDepMap.put(dep.getCompanyId(), deplist = new ArrayList<WqDepInfo>());
		}
		deplist.add(dep);
	}
	public static void updateDep(WqDepInfo dep){
		WqDepInfo di = DataCache.depMap.get(dep.getId());
		BeanUtils.copyProperties(dep, di);
	}
	public static void deleteDep(WqDepInfo dep){
		DataCache.depMap.remove(dep.getId());
		List<WqDepInfo> deplist = DataCache.comDepMap.get(dep.getCompanyId());
		for(WqDepInfo d : deplist){
			if(d.getId().equals(dep.getId())){
				deplist.remove(d);
				break;
			}
		}
	}
}

