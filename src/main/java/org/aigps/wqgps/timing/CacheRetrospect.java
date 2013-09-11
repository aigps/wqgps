package org.sunleads.timing;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.entity.WqClientInfo;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.DateUtil;
import org.sunleads.common.util.SqlStringUtil;
import org.sunleads.common.util.StrUtil;
import org.sunleads.common.util.TimingUtil;
import org.sunleads.common.util.UIDUtil;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * 该类用于保存系统中员工和区域发生变化的数据，同时将这些变化更新到上位机去进行数据追溯的运算
 * @author Administrator
 *
 */
@Component
@Transactional
@SuppressWarnings({"unchecked"})
public class CacheRetrospect {
	private final static Log log = LogFactory.getLog(CacheRetrospect.class);

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	//staffId,[staffId,regionIds,startTime,null,companyId,remark]
	private static Map<String,String[]> staffRegionMap = new ConcurrentHashMap<String,String[]>();

	private static Set<String> deleteStaffIdSet = new HashSet<String>();
	private static Set<String> deleteRegionIdSet = new HashSet<String>();
	private static Set<String> deleteStaffRegionSet = new HashSet<String>();
	
	//修改客户区域
	public static void updateClient(WqClientInfo client){
		String regions = client.getRegionIds();
		if(StringUtils.isBlank(regions)){
			return;
		}
		//客户和员工的关系
		Set<String> staffIds = DataCache.clientStaffIdMap.get(client.getId());
		if(staffIds==null || staffIds.isEmpty()){
			return;
		}
		String remark = "修改客户:"+client.getName();
		List<String> regionIds = Arrays.asList(regions.split(","));
		for(String regionId : regionIds){
			for(String staffId : staffIds){
				add(staffId,regionId,remark);
			}
		}
	}
	//修改公司区域
	public static void updateCompany(WqCompanyInfo company){
		String regions = company.getRegionIds();
		if(StringUtils.isBlank(regions)){
			return;
		}
		Set<String> staffIds = new HashSet<String>();
		for(WqStaffInfo staff : DataCache.staffMap.values()){
			if(staff.getCompanyId().equals(company.getId())){
				staffIds.add(staff.getId());
			}
		}
		String remark = "修改公司区域:"+company.getName();
		List<String> regionIds = Arrays.asList(regions.split(","));
		for(String regionId : regionIds){
			for(String staffId : staffIds){
				add(staffId,regionId,remark);
			}
		}
	}
	
	//按公司追溯时间，对员工在区域的追溯
	public static void add(String staffId,String regionId,String remark){
		if(StringUtils.isBlank(regionId)){
			return;
		}
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		if(staff == null){
			return;
		}
		//如果员工没有定位信息，不用追溯
		if(!DataCache.staffPostionMap.containsKey(staffId)){
			return;
		}
		try{
			String startTime = getStartTime(staff);
			if(startTime == null){
				return;
			}
			String[] data = staffRegionMap.get(staffId);
			if(data == null){
				staffRegionMap.put(staffId, new String[]{staffId,regionId,startTime,null,staff.getCompanyId(),remark});
			}else if(data[1].indexOf(regionId) == -1){
				data[1] = data[1]+","+regionId;
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}
	
	public static void add(String staffId,Collection<String> regionIds,String remark){
		if(regionIds==null || regionIds.isEmpty()){
			return;
		}
		for(String regionId : regionIds){
			add(staffId,regionId,remark);
		}
	}
	
	//获取开始时间，根据公司的追溯时间进行计算
	private static String getStartTime(WqStaffInfo staff){
		WqCompanyInfo company = DataCache.companyInfoMap.get(staff.getCompanyId());
		if(company==null || company.getRetrospectLong()==null){
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -company.getRetrospectLong().intValue());
		
		String startTime = DateUtil.dateFormat.format(calendar.getTime());
		return startTime+" 00:00:00";
	}
	
	
	
	
	
	
	
	
	
	
	
	//删除了区域
	public static void deleteRegion(String regionId){
		List<String> deleteList = new ArrayList<String>();
		for(Map.Entry<String, String[]> entry : staffRegionMap.entrySet()){
			String[] values = entry.getValue();
			if(values[1].equals(regionId)){
				deleteList.add(entry.getKey());
			}else if(values[1].indexOf(regionId)!=-1){
				values[1] = values[1].replaceAll(","+regionId, "").replaceAll(regionId+",", "").replaceAll(regionId, "");
			}
		}
		for(String key : deleteList){
			staffRegionMap.remove(key);
		}
		deleteRegionIdSet.add(regionId);
	}
	
	//删除员工
	public static void deleteStaff(String staffId){
		List<String> deleteList = new ArrayList<String>();
		for(Map.Entry<String, String[]> entry : staffRegionMap.entrySet()){
			String[] values = entry.getValue();
			if(values[0].equals(staffId)){
				deleteList.add(entry.getKey());
			}
		}
		for(String key : deleteList){
			staffRegionMap.remove(key);
		}
		deleteStaffIdSet.add(staffId);
	}
	
	//删除某员工的某区域
	public static void delete(String staffId,String regionId){
		List<String> deleteList = new ArrayList<String>();
		for(Map.Entry<String, String[]> entry : staffRegionMap.entrySet()){
			String[] values = entry.getValue();
			if(values[0].equals(staffId)){
				if(values[1].equals(regionId)){
					deleteList.add(entry.getKey());
				}else if(values[1].indexOf(regionId)!=-1){
					values[1] = values[1].replaceAll(","+regionId, "").replaceAll(regionId+",", "").replaceAll(regionId, "");
				}
			}
		}
		for(String key : deleteList){
			staffRegionMap.remove(key);
		}
		deleteStaffRegionSet.add(staffId+regionId);
	}
	
	
	
	
	

	//从regionIds中删除delRegionIds的区域
	private static String getLeftRegionIds(Set<String> delRegionIds,String regionIds){
		if(StringUtils.isBlank(regionIds)){
			return regionIds;
		}
		Set<String> regionSet = new HashSet<String>();
		regionSet.addAll(Arrays.asList(regionIds.split(",")));
		if(delRegionIds!=null && !delRegionIds.isEmpty()){
			regionSet.removeAll(delRegionIds);
		}
		return StrUtil.collectionToString(regionSet);
	}
	
	public static void startup(){
		Thread t = new Thread(new Runnable(){
			public void run() { 
				while(true){
					try {
						Thread.sleep(TimingUtil.getForInt("retrospect.staff.region.interval"));
						
						//没有新的追溯数据，直接返回
						if(staffRegionMap.isEmpty() && deleteRegionIdSet.isEmpty() && deleteStaffIdSet.isEmpty() && deleteStaffRegionSet.isEmpty()){
							continue;
						}
	
						Map<String,String[]> staffRegionMapOld = staffRegionMap;
						staffRegionMap = new ConcurrentHashMap<String,String[]>();
						
						Set<String> deleteRegionIdSetOld = deleteRegionIdSet;
						deleteRegionIdSet = new HashSet<String>();
	
						Set<String> deleteStaffIdSetOld = deleteStaffIdSet;
						deleteStaffIdSet = new HashSet<String>();
	
						Set<String> deleteStaffRegionSetOld = deleteStaffRegionSet;
						deleteStaffRegionSet = new HashSet<String>();
						
						
						CacheRetrospect refresh = (CacheRetrospect)AppUtil.getBean("cacheRetrospect");
						//数据库中已经有的未执行的追溯数据
						List<Map<String,Object>> list = refresh.getNotExceRecord();
						
						
						if(list!=null && !list.isEmpty()){
							List<String> deleteIds = new ArrayList<String>();
							for(Map<String,Object> map : list){
								String staffId = (String)map.get("STAFF_ID");
								if(deleteStaffIdSetOld.contains(staffId)){//员工已经被删除
									deleteIds.add((String)map.get("ID"));
									continue;
								}
								String remark = (String)map.get("REMARK");
								String regionIds = (String)map.get("REGION_IDS");

								//该员工所有删除的区域
								Set<String> deleteRegionIdSet = new HashSet<String>();
								for(String s_r : deleteStaffRegionSetOld){
									if(s_r.startsWith(staffId)){
										deleteRegionIdSet.add(s_r.substring(staffId.length()));//该员工被指定删除的区域
									}
								}
								deleteRegionIdSet.addAll(deleteRegionIdSetOld);
								
								if(staffRegionMapOld.containsKey(staffId)){//该员工有新的区域需要追溯
									deleteIds.add((String)map.get("ID"));//把旧的删除
									
									regionIds = regionIds+","+staffRegionMapOld.get(staffId)[1];//区域的累加
									String rIds = getLeftRegionIds(deleteRegionIdSet,regionIds);//旧的和新的追溯区域最终的叠加
									if(StringUtils.isNotBlank(rIds)){
										staffRegionMapOld.get(staffId)[1] = rIds;
									}else{
										staffRegionMapOld.remove(staffId);//没有区域，直接删除
									}
								}else{//在旧有的区域中，有的被删除了
									String rIds = getLeftRegionIds(deleteRegionIdSet,regionIds);
									if(!regionIds.equals(rIds)){
										deleteIds.add((String)map.get("ID"));
										if(StringUtils.isNotBlank(rIds)){
											String startTime = (String)map.get("START_TIME");
											String endTime = (String)map.get("END_TIME");
											String companyId = (String)map.get("COMPANY_ID");
											staffRegionMapOld.put(staffId, new String[]{staffId,rIds,startTime,endTime,companyId,remark});
										}
									}
								}
							}
							
							if(!deleteIds.isEmpty()){
								refresh.deleteRet(deleteIds);
							}
							refresh.updateToExceState();
						}
						
						List<String[]> datas = new ArrayList<String[]>();
						datas.addAll(staffRegionMapOld.values());
						if(!datas.isEmpty()){
							refresh.saveRet(datas);
						}
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}
	
	
	//数据库中已经有的未执行的追溯数据
	public List<Map<String,Object>> getNotExceRecord(){
		//先将未执行状态０设为４
		int count = this.jdbcTemplate.update(updateToNotExceSql);
		if(count<=0){
			return null;
		}
		//再将状态为４的查询出来
		return this.jdbcTemplate.queryForList(selectNotExceSql);
	}
	
	//将状态从4设回0
	public void updateToExceState(){
		this.jdbcTemplate.update(updateToExceSql);
	}
	
	//删除没用的追溯数据
	public void deleteRet(List<String> deleteIds){
		String sql = "DELETE FROM WQ_RETROSPECT WHERE "+SqlStringUtil.formatListToSQLIn("ID", deleteIds, true);
		this.jdbcTemplate.update(sql);
	}
	
	public void saveRet(final List<String[]> datas){
		final Iterator<String[]> it = datas.iterator();
		final int size = datas.size();
		final String date = DateUtil.getCurDate();
		try{
			this.jdbcTemplate.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
				public int getBatchSize() {
					return size;
				}
	
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					String[] data = it.next();
					ps.setString(1, UIDUtil.getId());
					ps.setString(2, data[0]);
					ps.setString(3, data[1]);
					ps.setString(4, data[2]);
					ps.setString(5, data[3]);
					ps.setString(6, "0");
					ps.setString(7, date);
					ps.setString(8, data[4]);
					ps.setString(9, data[5]);
				}
			});
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
	}
	
	
	private static String updateToNotExceSql = "UPDATE WQ_RETROSPECT SET STATE='4' WHERE STATE='0'";
	private static String updateToExceSql = "UPDATE WQ_RETROSPECT SET STATE='0' WHERE STATE='4'";
	private static String selectNotExceSql = "SELECT * FROM WQ_RETROSPECT WHERE STATE='4'";
	private static String insertSql = "INSERT INTO WQ_RETROSPECT(ID,STAFF_ID,REGION_IDS,START_TIME,END_TIME,STATE,CREATE_TIME,COMPANY_ID,REMARK) VALUES(?,?,?,?,?,?,?,?,?)";
}

