package org.aigps.wqgps.timing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CacheStaffMap extends HibernateDAO<WqStaffInfo, String> {
	public final static Log log = LogFactory.getLog(CacheStaffMap.class);
	
	public static void startup(){
		new Thread(new Runnable(){
			public void run() { 
				try{
					CacheStaffMap refresh = (CacheStaffMap) AppUtil.getBean("cacheStaffMap");
					List<WqStaffInfo> list = refresh.findAll();
					
					for(WqStaffInfo staff : list){
						if(staff.getIsEnable() == false){
							continue;
						}
						List<WqStaffInfo> staffList = DataCache.depStaffMap.get(staff.getDepId());
						if(staffList == null){
							staffList = new CopyOnWriteArrayList<WqStaffInfo>();
							DataCache.depStaffMap.put(staff.getDepId(), staffList);
						}
						staffList.add(staff);
						DataCache.staffMap.put(staff.getId(), staff);
						createphoneStaffIdMap(staff);
					}
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}).start();
	}
	
	//保存终端与员工关系集合
	public static void createphoneStaffIdMap(WqStaffInfo wqStaffInfo){
		String tmnCode = wqStaffInfo.getMobileNumber();
		String staffId = wqStaffInfo.getId();
		if(StringUtils.isNotBlank(tmnCode) && StringUtils.isNotBlank(staffId)){
			DataCache.phoneStaffIdMap.put(tmnCode, staffId);
		}
	}
	
	
	/**
	 * 获取所有系统菜单
	 * @return
	 */
	public List<WqStaffInfo> findAll(){
		return this.getAll("cnName",true);
	}

	//对员工缓存的修改
	public static void addStaff(WqStaffInfo staff){
		DataCache.staffMap.put(staff.getId(), staff);
		DataCache.phoneStaffIdMap.put(staff.getMobileNumber(), staff.getId());
		List<WqStaffInfo> list = DataCache.depStaffMap.get(staff.getDepId());
		if(list == null){
			DataCache.depStaffMap.put(staff.getDepId(), list = new ArrayList<WqStaffInfo>());
		}
		list.add(staff);
	}
	public static void updateStaff(WqStaffInfo staff){
		WqStaffInfo s = DataCache.staffMap.get(staff.getId());
		//部门发生变化
		if(!staff.getDepId().equals(s.getDepId())){
			List<WqStaffInfo> list = DataCache.depStaffMap.get(s.getDepId());
			if(list != null) {
				for(WqStaffInfo sf : list) {
					if(sf.getId().equals(s.getId())){
						list.remove(sf);
						break;
					}
				}
			}
			list = DataCache.depStaffMap.get(staff.getDepId());
			if(list == null) {
				DataCache.depStaffMap.put(staff.getDepId(), list = new ArrayList<WqStaffInfo>());
			}
			list.add(staff);
		}
		DataCache.phoneStaffIdMap.remove(s.getMobileNumber());//删除旧的员工手机号和员工ID关系
		BeanUtils.copyProperties(staff, s);
		DataCache.phoneStaffIdMap.put(staff.getMobileNumber(), staff.getId());
	}
	public static void deleteStaff(WqStaffInfo staff){
		DataCache.staffMap.remove(staff.getId());
		DataCache.phoneStaffIdMap.remove(staff.getMobileNumber());
		List<WqStaffInfo> stafflist = DataCache.depStaffMap.get(staff.getDepId());
		
		if(stafflist!=null){
			for(WqStaffInfo s : stafflist){
				if(s.getId().equals(staff.getId())){
					stafflist.remove(s);
					break;
				}
			}
		}
	}
}

