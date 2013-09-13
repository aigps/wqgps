package org.aigps.wqgps.timing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.comparator.DcChinaAreaComparator;
import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.DcChinaArea;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CacheDcChinaAreaMap extends HibernateDAO<DcChinaArea, String> {
	public final static Log log = LogFactory.getLog(CacheDcChinaAreaMap.class);
	
	public static void startup(){
		new Thread(new Runnable(){
			public void run() { 
				try{
					CacheDcChinaAreaMap refresh = (CacheDcChinaAreaMap) AppUtil.getBean("cacheDcChinaAreaMap");
					List<DcChinaArea> list = refresh.findAll();
					
					Map<String,DcChinaArea> map = new HashMap<String, DcChinaArea>();
					for(DcChinaArea model : list){
						map.put(model.getZcode(), model);
					}
					DataCache.dcChinaAreaMap = map;
					
					List<DcChinaArea> treeList = refresh.getParentChildrenList(map);
					refresh.sortData(treeList);
					
					DataCache.dcChinaAreaList = treeList;
					
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}).start();
	}
	
	/**
	 * 获取所有
	 * @return
	 */
	public List<DcChinaArea> findAll(){
		return this.getAll();
	}

	//生成树形结构集合
	private List<DcChinaArea> getParentChildrenList(Map<String,DcChinaArea> map){
		List<DcChinaArea> topArray = new ArrayList<DcChinaArea>();
		
		for (String key : map.keySet()) {
			DcChinaArea model = map.get(key);
			if(key.length() == 2){
				model.setParent(null);
				model.setName(model.getProv());
				topArray.add(model);
			}
			if(key.length() == 4){
				model.setName(model.getCity());
				String parentKey = key.substring(0,2);
				DcChinaArea parentModel = map.get(parentKey);
				setParentAndChildren(parentModel, model);
			}
			if(key.length() == 6){
				model.setName(model.getTown());
				String parentKey = key.substring(0,4);
				String grandPaKey = key.substring(0,2);
				DcChinaArea pmodel = map.get(parentKey);
				DcChinaArea gmodel = map.get(grandPaKey);
				DcChinaArea parentModel = null;
				
				if(pmodel != null){
					parentModel = pmodel;
				}else{
					if(gmodel != null){
						parentModel = gmodel;
					}
				}
				setParentAndChildren(parentModel, model);
			}
		}
		return topArray;
	}
	
	//设置父子关系
	private void setParentAndChildren(DcChinaArea parentModel,DcChinaArea model){
		if(parentModel != null){
			if(parentModel.getChildren() == null){
				parentModel.setChildren(new ArrayList<DcChinaArea>());
			}
			model.setParent(parentModel);
			parentModel.getChildren().add(model);
		}
	}
	
	//排序
	private void sortData(List<DcChinaArea> list){
		Collections.sort(list, new DcChinaAreaComparator());
		for (DcChinaArea dcChinaArea : list) {
			if(dcChinaArea.getChildren() != null){
				sortData(dcChinaArea.getChildren());
			}
		}
		
	}
}

