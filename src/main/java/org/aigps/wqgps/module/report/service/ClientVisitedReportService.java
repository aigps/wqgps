package org.sunleads.module.report.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.WqClientInfo;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.util.DateUtil;
import org.sunleads.module.report.dao.RegionVisitDAO;

import edu.emory.mathcs.backport.java.util.Arrays;
/**
 * ���ദ��ͻ���Ա���ݷõ���ϸ���
 */
@Component
@Transactional
@SuppressWarnings({"rawtypes","unchecked"})
public class ClientVisitedReportService {
	public final static Log log = LogFactory.getLog(ClientVisitedReportService.class);
	
	private PublicDAO publicDAO;
	private RegionVisitDAO regionVisitDAO;
	
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	@Autowired
	public void setRegionVisitDAO(RegionVisitDAO regionVisitDAO) {
		this.regionVisitDAO = regionVisitDAO;
	}
	
	private void increase(Map model,String property,Long v){
		Long value = (Long)model.get(property);
		model.put(property, value == null ? 0l : (v == null ? ++value : (v+value)));
	}
	
	//��ͻ���Ա���ݷ������ѯ����
	public List<Map<String,Object>> queryClientsVisitedReport(List<String> clientIds,String startDate,String endDate,Long visitLong) throws Exception{
		Map<String,Map<String,Object>> visitedMap = new LinkedHashMap<String,Map<String,Object>>();
		
		try{
			//��ʼ�����ص�����
			for(String clientId : clientIds){
				Map<String,Object> model = new HashMap<String,Object>();
				visitedMap.put(clientId, model);
				
				model.put("clientId", clientId);
				model.put("visitedTimes", 0L);//��ʼ�����ݷô���
				model.put("validVisitedTimes", 0L);//��ʼ����Ч���ݷô���
				model.put("validVisitedLong", 0L);//��ʼ����Ч���ݷ�ʱ��
				model.put("visitedLong", 0L);//��ʼ���ܱ��ݷ�ʱ��
			}
			
			//��ѯ�Ŀͻ�����
			List<WqClientInfo> clients = publicDAO.findBy("id", clientIds, WqClientInfo.class);
			
			Map<String,WqClientInfo> regionClientMap = new HashMap<String,WqClientInfo>();//����ID�Ϳͻ��Ķ��չ�ϵ
			Set<String> regionIds = new HashSet<String>();//�ͻ�����������ID����
			for(WqClientInfo client : clients){
				Map<String,Object> model = visitedMap.get(client.getId());
				model.put("clientName", client.getName());//�ͻ�����
				
				if(StringUtils.isNotBlank(client.getRegionIds())){
					String[] rids = StringUtils.split(client.getRegionIds(), ",");
					regionIds.addAll(Arrays.asList(rids));
					for(String rid : rids){
						regionClientMap.put(rid, client);
					}
				}
			}
			
			//��ѯ��Щ���������ڷ�Χ�ڣ������������м�¼
			List<Map<String,Object>> visitedHisList = regionVisitDAO.findRegionVisitHisByRegionIds(regionIds, startDate, endDate);
			for(Map<String,Object> map : visitedHisList){
				String regionId = (String)map.get("REGION_ID");
				WqClientInfo client = regionClientMap.get(regionId);
				
				Map<String,Object> model = visitedMap.get(client.getId());
				increase(model,"visitedTimes",1L);//���ݷô����ۼ�
	
				String enterTime = (String)map.get("ENTER_TIME");
				String leaveTime = (String)map.get("LEAVE_TIME");
				
				//����ѯʱ��ͽ���ʱ����бȽϣ�ȡ�������ʱ��
				String maxEnterTime = enterTime.compareTo(startDate+" 00:00:00")>0?enterTime:(startDate+" 00:00:00");
				String minLeaveTime = leaveTime.compareTo(endDate+" 23:59:59")<0?leaveTime:(endDate+" 23:59:59");
				
				Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
				Long validVisitLong = client.getVisitLong().longValue()*60;//������ת����
				
				if(stayLong >= validVisitLong){//��Ч�ݷ�
					increase(model,"validVisitedTimes",1L);//��Ч���ݷô����ۼ�
					increase(model,"validVisitedLong",stayLong);//��Ч���ݷ�ʱ���ۼ�
				}
				increase(model,"visitedLong",stayLong);//���ݷ�ʱ���ۼ�
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>(visitedMap.values());
		
		//����ʱ��С��visitLongֵ��
		for(Iterator<Map<String,Object>> it=list.iterator(); it.hasNext(); ){
			Long vl = (Long)it.next().get("visitedLong");
			if(vl < visitLong*60){
				it.remove();
			}
		}
		return list;
	}
	

	//���ͻ����ݷ��������
	public List<Map<String,Object>> queryClientVisitedReport(String clientId,String startDate,String endDate) throws Exception{
		Map<String,Map<String,Object>> visitMap = new LinkedHashMap<String,Map<String,Object>>();
		
		try{
			//��ѯ�Ŀͻ�����
			WqClientInfo client = (WqClientInfo)publicDAO.get(clientId, WqClientInfo.class);
			
			String[] rids = StringUtils.split(client.getRegionIds(), ",");
	
			//��ѯ����ͻ����������ڷ�Χ�ڣ������������м�¼
			List<Map<String,Object>> visitedHisList = regionVisitDAO.findRegionVisitHisByRegionIds(Arrays.asList(rids), startDate, endDate);
	
			//��ʼ����������
			for(Map<String,Object> map : visitedHisList){
				String staffId = (String)map.get("STAFF_ID");
				if(!visitMap.containsKey(staffId)){
					WqStaffInfo staff = DataCache.staffMap.get(staffId);
					if(staff == null){
						continue;
					}
					Map<String,Object> model = new HashMap<String,Object>();
					visitMap.put(staffId, model);
	
					model.put("staffId", staffId);
					model.put("staffName", staff.getCnName());
					model.put("clientId", clientId);
					model.put("clientName", client.getName());
					model.put("visitTimes", 0L);//��ʼ��Ա���ݷô���
					model.put("validVisitTimes", 0L);//��ʼ��Ա����Ч�ݷô���
					model.put("validVisitLong", 0L);//��ʼ��Ա����Ч�ݷ�ʱ��
					model.put("visitLong", 0L);//��ʼ��Ա���ܰݷ�ʱ��
				}
			}
			
			for(Map<String,Object> map : visitedHisList){
				String staffId = (String)map.get("STAFF_ID");
				Map<String,Object> model = visitMap.get(staffId);
				if(model == null){
					continue;
				}
				increase(model,"visitTimes",1L);//Ա���ݷô����ۼ�
	
				String enterTime = (String)map.get("ENTER_TIME");
				String leaveTime = (String)map.get("LEAVE_TIME");
				
				String maxEnterTime = enterTime.compareTo(startDate+" 00:00:00")>0?enterTime:(startDate+" 00:00:00");
				String minLeaveTime = leaveTime.compareTo(endDate+" 23:59:59")<0?leaveTime:(endDate+" 23:59:59");
				
				Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
				Long validVisitLong = client.getVisitLong().longValue()*60;//������ת����
				
				if(stayLong >= validVisitLong){//��Ч�ݷ�
					increase(model,"validVisitTimes",1L);//Ա����Ч�ݷô����ۼ�
					increase(model,"validVisitLong",stayLong);//Ա����Ч�ݷ�ʱ���ۼ�
				}
				increase(model,"visitLong",stayLong);//Ա���ݷ�ʱ���ۼ�
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		return new ArrayList<Map<String,Object>>(visitMap.values());
	}

	//���ͻ�����Ա���ݷ��������
	public List<Map<String,Object>> querySingleVisitedReport(String clientId,String staffId,String startDate,String endDate) throws Exception{
		try{
			//��ѯ�Ŀͻ�����
			WqClientInfo client = (WqClientInfo)publicDAO.get(clientId, WqClientInfo.class);
			String[] rids = StringUtils.split(client.getRegionIds(), ",");
			
			//��ѯ����ͻ����������ڷ�Χ�ڣ������������м�¼
			List<Map<String,Object>> visitedHisList = regionVisitDAO.findRegionVisitHisByRegionIds(Arrays.asList(rids), startDate, endDate);
			
			for(Iterator<Map<String,Object>> it=visitedHisList.iterator(); it.hasNext(); ){
				Map<String,Object> map = it.next();
				if(!staffId.equals(map.get("STAFF_ID"))){
					it.remove();
				}else{
					Long validVisitLong = client.getVisitLong().longValue()*60;//������ת����
					
					String enterTime = (String)map.get("ENTER_TIME");
					String leaveTime = (String)map.get("LEAVE_TIME");
					
					String maxEnterTime = enterTime.compareTo(startDate+" 00:00:00")>0?enterTime:(startDate+" 00:00:00");
					String minLeaveTime = leaveTime.compareTo(endDate+" 23:59:59")<0?leaveTime:(endDate+" 23:59:59");
					Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
					
					map.put("STAY_LONG", stayLong);
					map.put("ENTER_TIME", maxEnterTime);
					map.put("LEAVE_TIME", minLeaveTime);
					map.put("STATE", stayLong >= validVisitLong ? "��Ч�ݷ�" : "��Ч�ݷ�");
				}
			}
			return visitedHisList;
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
	}
}