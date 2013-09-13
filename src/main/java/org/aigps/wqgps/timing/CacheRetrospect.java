package org.aigps.wqgps.timing;

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

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.WqClientInfo;
import org.aigps.wqgps.common.entity.WqCompanyInfo;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.common.util.SqlStringUtil;
import org.aigps.wqgps.common.util.StrUtil;
import org.aigps.wqgps.common.util.TimingUtil;
import org.aigps.wqgps.common.util.UIDUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * �������ڱ���ϵͳ��Ա�����������仯�����ݣ�ͬʱ����Щ�仯���µ���λ��ȥ��������׷�ݵ�����
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
	
	//�޸Ŀͻ�����
	public static void updateClient(WqClientInfo client){
		String regions = client.getRegionIds();
		if(StringUtils.isBlank(regions)){
			return;
		}
		//�ͻ���Ա���Ĺ�ϵ
		Set<String> staffIds = DataCache.clientStaffIdMap.get(client.getId());
		if(staffIds==null || staffIds.isEmpty()){
			return;
		}
		String remark = "�޸Ŀͻ�:"+client.getName();
		List<String> regionIds = Arrays.asList(regions.split(","));
		for(String regionId : regionIds){
			for(String staffId : staffIds){
				add(staffId,regionId,remark);
			}
		}
	}
	//�޸Ĺ�˾����
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
		String remark = "�޸Ĺ�˾����:"+company.getName();
		List<String> regionIds = Arrays.asList(regions.split(","));
		for(String regionId : regionIds){
			for(String staffId : staffIds){
				add(staffId,regionId,remark);
			}
		}
	}
	
	//����˾׷��ʱ�䣬��Ա���������׷��
	public static void add(String staffId,String regionId,String remark){
		if(StringUtils.isBlank(regionId)){
			return;
		}
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		if(staff == null){
			return;
		}
		//���Ա��û�ж�λ��Ϣ������׷��
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
	
	//��ȡ��ʼʱ�䣬���ݹ�˾��׷��ʱ����м���
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
	
	
	
	
	
	
	
	
	
	
	
	//ɾ��������
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
	
	//ɾ��Ա��
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
	
	//ɾ��ĳԱ����ĳ����
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
	
	
	
	
	

	//��regionIds��ɾ��delRegionIds������
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
						
						//û���µ�׷�����ݣ�ֱ�ӷ���
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
						//���ݿ����Ѿ��е�δִ�е�׷������
						List<Map<String,Object>> list = refresh.getNotExceRecord();
						
						
						if(list!=null && !list.isEmpty()){
							List<String> deleteIds = new ArrayList<String>();
							for(Map<String,Object> map : list){
								String staffId = (String)map.get("STAFF_ID");
								if(deleteStaffIdSetOld.contains(staffId)){//Ա���Ѿ���ɾ��
									deleteIds.add((String)map.get("ID"));
									continue;
								}
								String remark = (String)map.get("REMARK");
								String regionIds = (String)map.get("REGION_IDS");

								//��Ա������ɾ��������
								Set<String> deleteRegionIdSet = new HashSet<String>();
								for(String s_r : deleteStaffRegionSetOld){
									if(s_r.startsWith(staffId)){
										deleteRegionIdSet.add(s_r.substring(staffId.length()));//��Ա����ָ��ɾ��������
									}
								}
								deleteRegionIdSet.addAll(deleteRegionIdSetOld);
								
								if(staffRegionMapOld.containsKey(staffId)){//��Ա�����µ�������Ҫ׷��
									deleteIds.add((String)map.get("ID"));//�Ѿɵ�ɾ��
									
									regionIds = regionIds+","+staffRegionMapOld.get(staffId)[1];//������ۼ�
									String rIds = getLeftRegionIds(deleteRegionIdSet,regionIds);//�ɵĺ��µ�׷���������յĵ���
									if(StringUtils.isNotBlank(rIds)){
										staffRegionMapOld.get(staffId)[1] = rIds;
									}else{
										staffRegionMapOld.remove(staffId);//û������ֱ��ɾ��
									}
								}else{//�ھ��е������У��еı�ɾ����
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
	
	
	//���ݿ����Ѿ��е�δִ�е�׷������
	public List<Map<String,Object>> getNotExceRecord(){
		//�Ƚ�δִ��״̬����Ϊ��
		int count = this.jdbcTemplate.update(updateToNotExceSql);
		if(count<=0){
			return null;
		}
		//�ٽ�״̬Ϊ���Ĳ�ѯ����
		return this.jdbcTemplate.queryForList(selectNotExceSql);
	}
	
	//��״̬��4���0
	public void updateToExceState(){
		this.jdbcTemplate.update(updateToExceSql);
	}
	
	//ɾ��û�õ�׷������
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

