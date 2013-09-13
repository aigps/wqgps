package org.aigps.wqgps.timing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aigps.wqgps.auth.AuthType;
import org.aigps.wqgps.auth.dao.AuthDAO;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.WqAuthObj;
import org.aigps.wqgps.common.entity.WqCompanyInfo;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.TimingUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.infinispan.wq.WqMemoryCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.Arrays;

@Component
@Transactional
@SuppressWarnings({"unchecked"})
public class SynStaffRegionMap extends HibernateDAO<Object, String> {
	public final static Log log = LogFactory.getLog(SynStaffRegionMap.class);

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public static void startup(){
		
		new ClassPathXmlApplicationContext(new String[]{"cacheContext.xml"});
		
		Thread t = new Thread(new Runnable(){
			public void run() { 
				while(true){

					try {
						Thread.sleep(TimingUtil.getForInt("syn.staff.region.interval"));
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}
					
					try {
						HashMap<String,Set<String>> staffRegionMap = new HashMap<String,Set<String>>();
						
						//初始化
						for(WqStaffInfo staff : DataCache.staffMap.values()){
							Set<String> regionSet = new HashSet<String>();
							staffRegionMap.put(staff.getId(), regionSet);
							//员工和公司区域的关系
							WqCompanyInfo company = DataCache.companyInfoMap.get(staff.getCompanyId());
							if(company != null && StringUtils.isNotBlank(company.getRegionIds())){
								List<String> companyRegionIds = Arrays.asList(StringUtils.split(company.getRegionIds(),","));
								regionSet.addAll(companyRegionIds);
							}
						}
						
						SynStaffRegionMap refresh = (SynStaffRegionMap)AppUtil.getBean("synStaffRegionMap");
						
						//员工和客户区域的关系
						if(CacheClientRegionIdMap.finish && CacheClientStaffIdMap.finish){
							//系统所有客户及其区域的对照关系
							Map<String,Set<String>> clientRegionMap = DataCache.clientRegionIdMap;
							for(Map.Entry<String,Set<String>> entry : DataCache.clientStaffIdMap.entrySet()){
								Set<String> staffIds = entry.getValue();
								String clientId = entry.getKey();
								if(!clientRegionMap.containsKey(clientId)){
									continue;
								}
								for(String staffId : staffIds){
									Set<String> regionSet = staffRegionMap.get(staffId);
									if(regionSet == null){
										continue;
									}
									regionSet.addAll(clientRegionMap.get(clientId));
								}
							}

							System.gc();
							
							//发送staffRegionMap对象到数据缓存中心
							//<员工ID,[区域ID1,区域ID2....]>
							WqMemoryCache.saveWqStaffRegion(staffRegionMap);
							log.info("staffRegionMap="+WqMemoryCache.getWqStaffRegion().size());
						}
						
						
						
						
						//Map<staffId,Set<String[]{ruleId,regionId}>
						HashMap<String,List<String[]>> staffRuleMap = new HashMap<String,List<String[]>>();
						WqAuthObj authObj = DataCache.authObjMap.get(AuthType.STAFF_RULE);
						//员工和围栏规则的关系
						if(authObj != null){
							//系统所有员工和围栏规则之间的关系权限列表
							List<Map<String,Object>> authList = AuthDAO.getListByObjId(authObj.getId(), refresh.getJdbcTemplate());
							Map<String,String[]> fenceRuleRegionMap = refresh.getFenceRuleRegionMap();
							for(Map<String,Object> map : authList){
								String staffId = map.get("STAFF_ID").toString();
								if(!DataCache.staffMap.containsKey(staffId)){
									continue;
								}
								String ruleId = map.get("RES_ID").toString();
								String[] rule = fenceRuleRegionMap.get(ruleId);
								if(rule == null){
									continue;
								}
								List<String[]> list = staffRuleMap.get(staffId);
								if(list == null){
									list = new ArrayList<String[]>();
									staffRuleMap.put(staffId, list);
								}
								list.add(rule);
							}
						}
						//发送staffRuleMap对象到数据缓存中心
						//<员工ID,[[规则ID,地图区域ID,行政区域ID,是否发短信通知],[...]]>
						WqMemoryCache.saveWqStaffRule(staffRuleMap);
						log.info("staffRuleMap="+staffRuleMap.size());
						
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					} finally {
						System.gc();
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}
	
	//系统所有规则，以其对应的区域ID
	public Map<String,String[]> getFenceRuleRegionMap(){
		String sql = "SELECT R.ID,F.REGION_ID,F.AREA_ID,F.COMPANY_ID FROM WQ_ELE_FENCE F,WQ_RULE R WHERE F.ID=R.ELE_FENCE_ID AND R.IS_ENABLE!=0 AND (F.REGION_ID IS NOT NULL OR F.AREA_ID IS NOT NULL)";
		List<Map<String,Object>> list = this.jdbcTemplate.queryForList(sql);
		
		Map<String,String[]> returnMap = new HashMap<String,String[]>();
		Map<String, WqCompanyInfo> companyMap = DataCache.companyInfoMap;
		for(Map<String,Object> map : list){
			String companyId = (String)map.get("COMPANY_ID");
			WqCompanyInfo c = companyMap.get(companyId);
			if(c == null){
				continue;
			}
			String notice = c.getIsSmsNotice()==null?"0":(c.getIsSmsNotice()==true?"1":"0");
			
			String ruleId = map.get("ID").toString();
			String regionId = (String)map.get("REGION_ID");
			String areaId = (String)map.get("AREA_ID");
			
			returnMap.put(ruleId, new String[]{ruleId,regionId,areaId,notice});
		}
		return returnMap;
	}
	
}

