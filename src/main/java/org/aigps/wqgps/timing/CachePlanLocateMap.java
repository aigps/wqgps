package org.aigps.wqgps.timing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.WqPlanLocate;
import org.aigps.wqgps.common.entity.WqPlanLocateDetail;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.common.util.TimingUtil;
import org.aigps.wqgps.socket.CmdUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * �ƻ���λ
 * @author admin
 *
 */
@Component
@Transactional
public class CachePlanLocateMap extends HibernateDAO<Object, String> {
	public final static Log log = LogFactory.getLog(CachePlanLocateMap.class);
	
	public static void startup(){
		Thread t = new Thread(new Runnable(){
			public void run() { 
				while(true){
					try{
						CachePlanLocateMap refresh = (CachePlanLocateMap) AppUtil.getBean("cachePlanLocateMap");
						List<WqPlanLocate> list = refresh.findAllWqPlanLocate();
						List<WqPlanLocateDetail> detailList = refresh.findAllWqPlanLocateDetail();
						Map<String,List<WqPlanLocateDetail>> planIdMap = createWqPlanLocateDetailMap(list,detailList);
					
						String nowDateTime = DateUtil.getCurDate();//ϵͳʱ��
						for(WqPlanLocate wqPlanLocate:list){
							if(!wqPlanLocate.getProcessFlag()){//ûִ�й��ġ�
								String planId = wqPlanLocate.getId();
								String planLocateTime = wqPlanLocate.getPlanLocateTime();
								if(nowDateTime.compareTo(planLocateTime+":00")>=0){//����
									List<WqPlanLocateDetail> cmdList = planIdMap.get(planId);
									for(WqPlanLocateDetail wqPlanLocateDetail:cmdList){
										String staffId = wqPlanLocateDetail.getStaffId();
										WqStaffInfo staff = DataCache.staffMap.get(staffId);
										if(staff != null){
											CmdUtil.sendLcsNowCmd(staff.getMobileType(),staff.getMobileNumber(),staff.getFixModel());//���͵���
										}
//										String tmnCode = DataCache.staffIdTmnCodeMap.get(staffId);
//										CmdUtil.sendGetPosCmd(tmnCode);//���͵���
										Thread.sleep(100);
									}
									refresh.updateWqPlanLocateProcessFlag(planId);
								}
							}
						}
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}finally{
						try{
							Thread.sleep(TimingUtil.getForInt("refresh.planlocate.interval"));
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
	
	//������ϸ�ƻ����񣬼�ֵ��ϵ(keyΪ�ƻ�����id,valueΪ���ڸüƻ��������ϸ����)
	private static Map<String,List<WqPlanLocateDetail>> createWqPlanLocateDetailMap(List<WqPlanLocate> planLocateList,List<WqPlanLocateDetail> wqPlanLocateDetailList){
		Map<String,String> planLocateTimeMap = new HashMap<String, String>();
		for(WqPlanLocate wqPlanLocate:planLocateList){
			planLocateTimeMap.put(wqPlanLocate.getId(),wqPlanLocate.getPlanLocateTime());
		}
		
		Map<String,List<WqPlanLocateDetail>> planIdMap = new HashMap<String, List<WqPlanLocateDetail>>();
		for(WqPlanLocateDetail wqPlanLocateDetail:wqPlanLocateDetailList){
			String planLocateId = wqPlanLocateDetail.getPlanLocateId();
			String staffId = wqPlanLocateDetail.getStaffId();
			if(planIdMap.containsKey(planLocateId)){//����ƻ���Ӧ��ϸ
				List<WqPlanLocateDetail> tempList = planIdMap.get(planLocateId);
				tempList.add(wqPlanLocateDetail);
			}else{
				List<WqPlanLocateDetail> tempList = new ArrayList<WqPlanLocateDetail>();
				tempList.add(wqPlanLocateDetail);
				planIdMap.put(planLocateId, tempList);
			}
			if(DataCache.planLocateDetailStaffIdMap.containsKey(staffId)){//����Ա����Ӧ��ϸ
				Map<String,WqPlanLocateDetail> tempMap = DataCache.planLocateDetailStaffIdMap.get(staffId);
				String planLocateTime = planLocateTimeMap.get(planLocateId);
				if(StringUtils.isNotBlank(planLocateTime)){
					tempMap.put(planLocateTime,wqPlanLocateDetail);
				}
			}else{
				Map<String,WqPlanLocateDetail> tempMap = new ConcurrentHashMap<String,WqPlanLocateDetail>();
				String planLocateTime = planLocateTimeMap.get(planLocateId);
				if(StringUtils.isNotBlank(planLocateTime)){
					tempMap.put(planLocateTime,wqPlanLocateDetail);
					DataCache.planLocateDetailStaffIdMap.put(staffId, tempMap);
				}
			}
		}
		
		
		return planIdMap;
	}
	
	
	//���¼ƻ���λִ�б�־
	public void updateWqPlanLocateProcessFlag(String planId){
		this.batchExecute("update WqPlanLocate wpl set wpl.processFlag ='1' where wpl.id =?", planId);
	}
	
	//���¼ƻ���λ��ϸ����
	public void updateWqPlanLocateDetail(WqPlanLocateDetail wqPlanLocateDetail){
		this.save(wqPlanLocateDetail);
	}
	
	/**
	 * ��ȡ����δִ�еļƻ���λ
	 * @return
	 */
	public List<WqPlanLocate> findAllWqPlanLocate(){
		return find("from WqPlanLocate wpl where wpl.processFlag<>'1'");
	}
	
	/**
	 * ��ȡ����δ��¼����ϸ�ƻ���λ
	 * @return
	 */
	public List<WqPlanLocateDetail> findAllWqPlanLocateDetail(){
		return find("from WqPlanLocateDetail wpld where wpld.logit is null or wpld.lat is null or wpld.logitOffset is null or wpld.latOffset is null");
	}
}

