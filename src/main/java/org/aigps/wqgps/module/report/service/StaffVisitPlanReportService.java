package org.aigps.wqgps.module.report.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.WqClientInfo;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.common.entity.WqVisitPlan;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.module.report.dao.RegionVisitDAO;
import org.aigps.wqgps.module.report.dao.VisitReportDAO;
import org.aigps.wqgps.module.report.model.VisitReportModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * ���ദ��Ա�����ļƻ��ݷã�����ȡԱ���ƻ��ݷÿͻ�����ϸ���
 */
@Component
@Transactional
public class StaffVisitPlanReportService {
	public final static Log log = LogFactory.getLog(StaffVisitPlanReportService.class);
	
	private VisitReportDAO visitReportDAO;
	private RegionVisitDAO regionVisitDAO;
	
	@Autowired
	public void setVisitReportDAO(VisitReportDAO visitReportDAO) {
		this.visitReportDAO = visitReportDAO;
	}
	@Autowired
	public void setRegionVisitDAO(RegionVisitDAO regionVisitDAO) {
		this.regionVisitDAO = regionVisitDAO;
	}
	
	//��Ա�����ƻ��ݷ������ѯ����
	public List<VisitReportModel> queryStaffsVisitReport(List<String> staffIds,String startDate,String endDate,Long visitLong) throws Exception{
		Map<String,VisitReportModel> visitMap = new LinkedHashMap<String,VisitReportModel>();
		
		Map<String,List<String>> planVisitClientMap = new HashMap<String,List<String>>(),//Ա���ƻ����ʵĿͻ�ID
		hadVisitClientMap = new HashMap<String,List<String>>(),//Ա���Ѿ����ʵĿͻ�ID
		validVisitClientMap = new HashMap<String,List<String>>();//Ա���Ѿ����ʵ���Ч�ͻ�ID
		try{
			//��ʼ��׼������
			for(String staffId : staffIds){
				String staffName = DataCache.staffMap.get(staffId).getCnName();
				visitMap.put(staffId, new VisitReportModel(staffId,staffName,null));
				
				planVisitClientMap.put(staffId, new ArrayList<String>());
				hadVisitClientMap.put(staffId, new ArrayList<String>());
				validVisitClientMap.put(staffId, new ArrayList<String>());
			}
			
			//��ѯ��ЩԱ�������ڷ�Χ�ڵ����мƻ��ݷü�¼
			List<Object[]> vpList = visitReportDAO.findVisitPlan(staffIds, startDate, endDate);
			
			Map<String,String> staffRegionClientMap = new HashMap<String,String>();//Ա��ID+����ID�Ϳͻ�ID֮��Ķ�Ӧ
			Map<String,WqClientInfo> clientMap = new HashMap<String,WqClientInfo>();//�ͻ�ID�Ϳͻ�֮��Ķ�Ӧ
			Map<String,List<String>> staffRegionClientDateMap = new HashMap<String,List<String>>();//Ա��ID+����ID�Ͷ���ݷ�����֮��Ķ�Ӧ
			
			//�Լƻ��ݷõĿͻ���������ͬʱ�ۼӼƻ��ݷÿͻ���
			for(Object[] obj : vpList){
				WqClientInfo client = (WqClientInfo) obj[1];
				if(StringUtils.isBlank(client.getRegionIds())){
					continue;
				}
				WqVisitPlan plan = (WqVisitPlan) obj[0];
				String staffId = plan.getStaffId();
				String[] rids = StringUtils.split(client.getRegionIds(), ",");
				for(int i=0; i<rids.length; i++){
					staffRegionClientMap.put(staffId+" "+rids[i], client.getId());
					List<String> dates = (List<String>)staffRegionClientDateMap.get(staffId+" "+rids[i]);
					if(dates==null){
						staffRegionClientDateMap.put(staffId+" "+rids[i], dates=new ArrayList<String>());
					}
					dates.add(plan.getVisitDate());
				}
				clientMap.put(client.getId(), client);
				
				List<String> cIds = planVisitClientMap.get(staffId);
				if(!cIds.contains(client.getId())){
					visitMap.get(staffId).planVisitCount++;//�ƻ��ݷÿͻ����ۼ�
					cIds.add(client.getId());
				}
			}
			
			//��ѯ��ЩԱ�������ڷ�Χ�ڣ����������������¼
			List<Map<String,Object>> visitHisList = regionVisitDAO.findRegionVisitHisByStaffIds(staffIds, startDate, endDate);
			
			for(Map<String,Object> map : visitHisList){
				String staffId = (String)map.get("STAFF_ID");
				String regionId = (String)map.get("REGION_ID");
				
				//Ա���ݷõ�������ͨ���ƻ����ſͻ���
				String clientId = staffRegionClientMap.get(staffId+" "+regionId);
				if(clientId == null){
					continue;
				}
				String enterTime = (String)map.get("ENTER_TIME");
				String leaveTime = (String)map.get("LEAVE_TIME");
				if(StringUtils.isBlank(enterTime)||StringUtils.isBlank(leaveTime)){
					continue;
				}
				
				String enterDate = enterTime.substring(0,10);
				String leaveDate= leaveTime.substring(0,10);
				
				List<String> visitDates = staffRegionClientDateMap.get(staffId+" "+regionId);
				
				for(String visitDate : visitDates){
					//������������ڣ������ǰݷüƻ��ж��Ƶ�����
					if(visitDate.compareTo(enterDate)<0 || visitDate.compareTo(leaveDate)>0){
						continue;
					}
	
					WqClientInfo client = clientMap.get(clientId);
	
					String maxEnterTime = enterTime.compareTo(visitDate+" 00:00:00")>0?enterTime:(visitDate+" 00:00:00");
					String minLeaveTime = leaveTime.compareTo(visitDate+" 23:59:59")<0?leaveTime:(visitDate+" 23:59:59");
					
					Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
					Long validVisitLong = client.getVisitLong().longValue()*60;//������ת����
					
					VisitReportModel model = visitMap.get(staffId);
					if(stayLong >= validVisitLong){//��Ч�ݷ�
						model.validVisitTimes ++;//��Ч�ݷÿͻ������ۼ�
						model.validVisitLong += stayLong;//��Ч�ݷ���ʱ���ۼ�
						List<String> cIds = validVisitClientMap.get(staffId);
						if(!cIds.contains(clientId)){
							model.validVisitCount++;//��Ч�ݷÿͻ����ۼ�
							cIds.add(clientId);
						}
					}
					model.visitTotalLong += stayLong;//�ݷ���ʱ���ۼ�
					model.visitTimes ++;//�ݷÿͻ������ۼ�
					List<String> cIds = hadVisitClientMap.get(staffId);
					if(!cIds.contains(clientId)){
						model.actualVisitCount++;//ʵ�ʰݷÿͻ����ۼ�
						cIds.add(clientId);
					}
				}
	
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		List<VisitReportModel> list = new ArrayList<VisitReportModel>(visitMap.values());
		for(Iterator<VisitReportModel> it=list.iterator(); it.hasNext(); ){
			VisitReportModel map = it.next();
			if(map.visitTotalLong < visitLong*60){
				it.remove();
			}
		}
		return list;
	}
	

	//��Ա�����ƻ��ݷ������ѯ����
	public List<VisitReportModel> queryStaffVisitReport(String staffId,String startDate,String endDate,Long visitLong) throws Exception{
		Map<String,VisitReportModel> visitMap = new LinkedHashMap<String,VisitReportModel>();
		Map<String,List<String>> planVisitClientMap = new HashMap<String,List<String>>(),//Ա���ƻ����ʵĿͻ�ID
		hadVisitClientMap = new HashMap<String,List<String>>(),//Ա���Ѿ����ʵĿͻ�ID
		validVisitClientMap = new HashMap<String,List<String>>();//Ա���Ѿ����ʵ���Ч�ͻ�ID
		
		try{
			//��ѯԱ�������ڷ�Χ�ڵ����мƻ��ݷü�¼
			List<Object[]> vpList = visitReportDAO.findVisitPlan(staffId, startDate, endDate);
			for(Object[] obj : vpList){
				String visitDate = ((WqVisitPlan) obj[0]).getVisitDate();
				String staffName = DataCache.staffMap.get(staffId).getCnName();
				visitMap.put(visitDate, new VisitReportModel(staffId,staffName,visitDate));
				
				planVisitClientMap.put(visitDate, new ArrayList<String>());
				hadVisitClientMap.put(visitDate, new ArrayList<String>());
				validVisitClientMap.put(visitDate, new ArrayList<String>());
			}
			
			Map<String,WqClientInfo> regionClientMap = new HashMap<String,WqClientInfo>();//����ID�Ϳͻ�֮��Ķ�Ӧ
			//�Լƻ��ݷõĿͻ���������ͬʱ�ۼӼƻ��ݷÿͻ���
			for(Object[] obj : vpList){
				WqClientInfo client = (WqClientInfo) obj[1];
				if(StringUtils.isBlank(client.getRegionIds())){
					continue;
				}
				WqVisitPlan plan = (WqVisitPlan) obj[0];
				String visitDate = plan.getVisitDate();
				String[] rids = StringUtils.split(client.getRegionIds(), ",");
				for(int i=0; i<rids.length; i++){
					regionClientMap.put(rids[i], client);
				}
				
				VisitReportModel model = visitMap.get(visitDate);
				//���δ���ƻ��ݷõĿͻ�����������ݷ��ˣ��������ɾ����ʣ�µľ�������δ�ݷõ�
				model.addNotVisitClientRecord(client);
				
				List<String> cIds = planVisitClientMap.get(visitDate);
				if(!cIds.contains(client.getId())){
					model.planVisitCount++;//�ƻ��ݷÿͻ����ۼ�
					cIds.add(client.getId());
				}
			}
			
			//��ѯ��ЩԱ�������ڷ�Χ�ڣ����������������¼
			List<Map<String,Object>> visitHisList = regionVisitDAO.findRegionVisitHisByStaffId(staffId, startDate, endDate);
	
			for(Map<String,Object> map : visitHisList){
				String regionId = (String)map.get("REGION_ID");
	
				//Ա���ݷõ�������ͨ���ƻ����ſͻ���
				WqClientInfo client = regionClientMap.get(regionId);
				if(client == null){
					continue;
				}
				//�ݷõĿͻ�����
				map.put("clientName", client.getName());
				
				String enterTime = (String)map.get("ENTER_TIME");
				String leaveTime = (String)map.get("LEAVE_TIME");
				String enterDate = enterTime.substring(0,10);
				String leaveDate= leaveTime.substring(0,10);
				
				for(String visitDate : visitMap.keySet()){
					//������������ڣ������ǰݷüƻ��ж��Ƶ�����
					if(visitDate.compareTo(enterDate)<0 || visitDate.compareTo(leaveDate)>0){
						continue;
					}
					VisitReportModel model = visitMap.get(visitDate);
					
					//client�ͻ����ݷ��ˣ�Ҫ��δ���ݷ���ɾ��
					model.removeNotVisitClient(client.getId());
	
					//��Ӿ���������
					Map<String,Object> newMap = new HashMap<String,Object>(map);
					model.addVisitRecord(newMap);
					
					model.visitTimes ++;//�ݷÿͻ������ۼ�
	
					List<String> cIds = hadVisitClientMap.get(visitDate);
					if(!cIds.contains(client.getId())){
						model.actualVisitCount++;//ʵ�ʰݷÿͻ����ۼ�
						cIds.add(client.getId());
						model.addVisitClient(client);
					}
					
					Long validVisitLong = client.getVisitLong().longValue()*60;//������ת����
					String maxEnterTime = enterTime.compareTo(visitDate+" 00:00:00")>0?enterTime:(visitDate+" 00:00:00");
					String minLeaveTime = leaveTime.compareTo(visitDate+" 23:59:59")<0?leaveTime:(visitDate+" 23:59:59");
					Long stayLong = DateUtil.getBetweenSecond(maxEnterTime,minLeaveTime);
	
					newMap.put("STAY_LONG", stayLong);
					newMap.put("ENTER_TIME", maxEnterTime);
					newMap.put("LEAVE_TIME", minLeaveTime);
					
					if(stayLong >= validVisitLong){//��Ч�ݷ�
						model.validVisitTimes ++;//��Ч�ݷÿͻ������ۼ�
						model.validVisitLong += stayLong;//��Ч�ݷ���ʱ���ۼ�
						cIds = validVisitClientMap.get(visitDate);
						if(!cIds.contains(client.getId())){
							model.validVisitCount++;//��Ч�ݷÿͻ����ۼ�
							cIds.add(client.getId());
						}
						newMap.put("isValidVisit", true);//��Ч�ݷ�
					}else{
						newMap.put("isValidVisit", false);//��Ч�ݷ�
					}
					model.visitTotalLong += stayLong;//�ݷ���ʱ���ۼ�
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
		List<VisitReportModel> list = new ArrayList<VisitReportModel>(visitMap.values());
		for(Iterator<VisitReportModel> it=list.iterator(); it.hasNext(); ){
			VisitReportModel map = it.next();
			if(map.visitTotalLong < visitLong*60){
				it.remove();
			}
		}
		return list;
	}
	
	//ͨ������ID���ϣ�����������������ڵ�ͼ����ʾ������߹��Ŀͻ�����
	public Map<String,WqMapRegion> queryMapRegion(List<String> regionIds){
		Map<String,WqMapRegion> map = new HashMap<String,WqMapRegion>();
		for(String rid : regionIds){
			WqMapRegion region = DataCache.mapRegionMap.get(rid);
			if(region != null){
				map.put(rid, region);
			}
		}
		return map;
	}
}

