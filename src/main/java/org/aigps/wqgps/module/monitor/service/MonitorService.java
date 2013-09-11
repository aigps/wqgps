package org.sunleads.module.monitor.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.auth.AuthType;
import org.sunleads.auth.AuthUtil;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.entity.WqDepInfo;
import org.sunleads.common.entity.WqStaffInfo;

import edu.emory.mathcs.backport.java.util.Collections;

@Component
@Transactional
@SuppressWarnings({"rawtypes","unchecked"})
public class MonitorService {
	public final static Log log = LogFactory.getLog(MonitorService.class);
	
	public List<Map<String, Object>> getPhoneState() throws Exception{
		//权限看到的部门
		List<WqDepInfo> deps = AuthUtil.getResList(AuthType.USER_DEP);
		//权限看到的员工
		List<WqStaffInfo> staffs = AuthUtil.getStaffList(deps);
		Map<String,WqStaffInfo> staffMap = new HashMap<String,WqStaffInfo>();
		for(WqStaffInfo staff:staffs){
			staffMap.put(staff.getId(), staff);
		}
		Map<String,Map<String,Object>> sateMap = new HashMap<String,Map<String,Object>>();
		
		try{
			for(Map.Entry<String, String[]> entry : DataCache.phoneStateMap.entrySet()){
				WqStaffInfo staff = staffMap.get(entry.getKey());
				if(staff != null){//权限内的员工
					String[] datas = entry.getValue();//[状态码，状态时间]
					Map<String,Object> map = sateMap.get(entry.getValue());
					if(map == null){
						sateMap.put(datas[0], map = new HashMap<String,Object>());
						map.put("state", datas[0]);
						map.put("count", 0);
						map.put("staffList", new ArrayList<WqStaffInfo>());
					}
					Integer count = (Integer)map.get("count");
					map.put("count", count+1);
					List<WqStaffInfo> staffList = (List<WqStaffInfo>)map.get("staffList");
					staff = (WqStaffInfo)BeanUtils.cloneBean(staff);
					staff.setStandby3(datas[1]);//状态时间
					staffList.add(staff);
				}
			}
			
			List<Map<String, Object>> list = new ArrayList(sateMap.values());
	
			Collections.sort(list, new Comparator<Map<String, Object>>() {
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Integer count1 = (Integer)o1.get("count");
					Integer count2 = (Integer)o2.get("count");
					return count1>count2?1:(count1<count2?-1:0);
				}
			});
			return list;
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	

	/**
	 * 返回报警数量统计
	 * @param _searchCode 	要查询的明细报警类型  
	 * 					 	如果为"ALL"  则返回所有明细
	 * 						 
	 * @return
	 * @throws Exception
	 */
	public Object getAlarmStatCount(String _searchCode) throws Exception{
		if(_searchCode == null){
			_searchCode = "STAT";
		}
		try{
			if(_searchCode.equalsIgnoreCase("ALL")){
				for(Map.Entry<String, List<String[]>> entry : DataCache.staffAlarmMap.entrySet()){
					log.error("-------------"+entry.getKey()+":"+entry.getValue().size());
				}
				return DataCache.staffAlarmMap;
			}else{
				Map rMap = new HashMap();
				for (String key : DataCache.staffAlarmMap.keySet()) {
					if(key.contains(_searchCode)){
						rMap.put(key, DataCache.staffAlarmMap.get(key));
					}else{
						rMap.put(key, DataCache.staffAlarmMap.get(key).size());
					}
				}
				log.error("-------------"+rMap.size());
				return rMap;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
}
