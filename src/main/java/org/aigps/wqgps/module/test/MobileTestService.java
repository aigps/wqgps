package org.aigps.wqgps.module.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.cache.CmdStateCache;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.CmdModel;
import org.aigps.wqgps.common.entity.WqPlanLocate;
import org.aigps.wqgps.common.entity.WqPlanLocateDetail;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.module.cmd.CmdService;
import org.aigps.wqgps.module.location.LocationService;
import org.aigps.wqgps.module.sysmanager.service.SysManagerService;
import org.aigps.wqgps.socket.CmdTypeEnum;
import org.aigps.wqgps.socket.CmdUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class MobileTestService {
	public final static Log log = LogFactory.getLog(MobileTestService.class);
	
	public static List<Map<String,Object>> actList = new ArrayList<Map<String,Object>>();
	public static List<Map<String,Object>> onceLocList = new ArrayList<Map<String,Object>>();
	public static List<Map<String,Object>> periodLocList = new ArrayList<Map<String,Object>>();
	public static String planLoc = "δִ��";
	public static Boolean running = false;

	public Map<String,Object> getTestResult(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("actList", actList);
		map.put("onceLocList", onceLocList);
		map.put("periodLocList", periodLocList);
		map.put("planLoc", planLoc);
		return map;
	}
	
	public void stopTest(){
		running = false;
	}
	
	public void startTest(String staffId,String fixModels,String periodLocTime,String intervalTime,boolean isAct,boolean isOnce,boolean isPeriod,boolean isPlan) throws Exception{
		running = true;
		actList = new ArrayList<Map<String,Object>>();
		onceLocList = new ArrayList<Map<String,Object>>();
		periodLocList = new ArrayList<Map<String,Object>>();
		planLoc = "δִ��";
		
		//���Լ���ʧ��
		if(isAct){
			startTestAct(staffId,fixModels);
		}
		//���Ե��ζ�λ
		if(isOnce){
			startTestOnceLoc(staffId,fixModels);
		}
		//�������ڶ�λ
		if(isPeriod){
			startTestPeriodLoc(staffId,fixModels,periodLocTime,intervalTime);
		}
		//���Լƻ���λ
		if(isPlan){
			startTestPlanLoc(staffId);
		}
		running = false;
	}
	
	//���Լ���ʧ��
	public void startTestAct(String staffId,final String fixModels) throws Exception{
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		actList = new ArrayList<Map<String,Object>>();
		Thread t = new StartActThread(staffId,fixModels,staff.getMobileNumber());
		t.setDaemon(true);
		t.start();
		try{
			while(!StartActThread.finish && running){
				Thread.sleep(1000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		inAct(staff);
	}
	
	
	//���Ե��ζ�λ
	public void startTestOnceLoc(String staffId,final String fixModels) throws Exception{
		onceLocList = new ArrayList<Map<String,Object>>();
		
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		
		//������ʧ��
		inAct(staff);
		
		//10��
		try{Thread.sleep(10000);}catch(Exception e){e.printStackTrace();}
		
		try{
			List<WqStaffInfo> staffs = new ArrayList<WqStaffInfo>();
			staff = (WqStaffInfo)BeanUtils.cloneBean(staff);
			staffs.add(staff);
			
			//ÿ�ֶ�λ���ͣ�������һ�ε��ζ�λ
			for(int i=0; i<fixModels.length()&&running; i++){
				String fixModel = fixModels.charAt(i)+"";
				CmdService cmdService = (CmdService)AppUtil.getBean("cmdService");
				Map<String,Object> locData = new HashMap<String,Object>();
				onceLocList.add(locData);
				locData.put("locType", getLocType(fixModel));
				try{
					staff.setFixModel(fixModel);
					//��״̬����Ϊ�ɹ��Ķ�λ�����ٴ����¶�λ
					CmdStateCache.setCmdStateCode(CmdTypeEnum.LCSNow.getValue(), staffId, CmdModel.CMD_SUCCESS_CODE);
					cmdService.sendGetPosCmd(staffs);
					
					while(running){
						try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
						
						String cmdState = CmdStateCache.getCmdStateCode(CmdTypeEnum.LCSNow.getValue(), staffId);
						locData.put("stateName", getStateName(cmdState));
						if(!CmdModel.CMD_PROCESS_CODE.equals(cmdState)){
							break;
						}
					}
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
	}
	
	
	

	//�������ڶ�λ
	public void startTestPeriodLoc(String staffId,String fixModels,String periodLocTime,String intervalTime) throws Exception{
		periodLocList = new ArrayList<Map<String,Object>>();

		List<String> ids = new ArrayList<String>();
		ids.add(staffId);
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		BigDecimal interval = new BigDecimal(intervalTime);
		int periodLocT = Integer.parseInt(periodLocTime);
		
		//ÿ�ֶ�λ���ͣ�������һ�����ڶ�λ
		for(int i=0; i<fixModels.length()&&running; i++){
			String fixModel = fixModels.charAt(i)+"";

			//���ͼ���
			act(staff,fixModel,interval);
			
			Map<String,Object> data = new HashMap<String,Object>();
			periodLocList.add(data);
			data.put("locType", getLocType(fixModel));
			data.put("locNum", 0);
			
			CountLocThread thread = new CountLocThread(staffId,data);
			thread.setDaemon(true);
			thread.start();
			
			try{
				for(int j=0; j<60 && running;j++){
					Thread.sleep(periodLocT*1000);
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				thread.stop = true;
			}
		}
		
		inAct(staff);
	}
	
	
	

	//���Լƻ���λ
	public void startTestPlanLoc(String staffId) throws Exception{
		planLoc = "��ʼִ��";
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		act(staff,"1", new BigDecimal(1));
		
		LocationService service = (LocationService)AppUtil.getBean("locationService");
		try{
			//���µĲ�������
			WqPlanLocate plan = new WqPlanLocate();
			List<WqPlanLocateDetail> details = new ArrayList<WqPlanLocateDetail>();
			WqPlanLocateDetail detail = new WqPlanLocateDetail();
			detail.setStaffId(staffId);
			details.add(detail);
			
			plan.setName("ϵͳ�Զ����Լƻ���λ");
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, 1);//1����֮���ܼƻ���λ
			String planTime = DateUtil.format.format(calendar.getTime());
			plan.setPlanLocateTime(DateUtil.format.format(calendar.getTime()));
			
			service.saveOrUpdateWqPlanLocate(plan, details);
			planLoc = "�����˲�������";
			boolean start = false;
			while(running){
				Thread.sleep(1000);
				Map<String,WqPlanLocateDetail> updateMap = DataCache.planLocateDetailStaffIdMap.get(staffId);
				if(updateMap == null){
					if(start == true){
						planLoc = "���Գɹ������յ���λ��Ϣ";
						break;
					}
				}else{
					start = true;
					for(Map.Entry<String,WqPlanLocateDetail> entry : updateMap.entrySet()){
						String planLocateTime = entry.getKey();
						if(planLocateTime.equals(planTime)){
							planLoc = "�����˲������ݣ�������";
						}
					}
				}
			}
			
			service.deleteWqPlanLocate(plan);
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		
		inAct(staff);
	}
	
	
	
	
	
	
	
	
	public static void act(WqStaffInfo staff,String fixModel,BigDecimal interval){
		//���ͼ�����漤�����
		CmdUtil.sendActiveCmd(staff.getMobileType(),staff.getMobileNumber(),fixModel,interval.longValue(),"000001","235959",staff.getWorkWeekDays());
		CmdStateCache.activeTmnNumMap.put(staff.getMobileNumber(),new String[]{"1",DateUtil.getCurDate()});
		
		while(running){
			try{
				try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
				String[] numTime = CmdStateCache.activeTmnNumMap.get(staff.getMobileNumber());
				if(numTime == null){//����ɹ�
					break;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void inAct(WqStaffInfo staff) throws Exception{
		List<String> ids = new ArrayList<String>();
		ids.add(staff.getId());
		SysManagerService service = (SysManagerService)AppUtil.getBean("sysManagerService");
		service.cancelActivateStaffState(ids);
		
		int waiting = 0;
		while(running){
			try{
				String[] numTime = CmdStateCache.cancelActiveTmnNumMap.get(staff.getMobileNumber());
				//ʧ��ɹ�
				if(numTime == null){break;}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
				waiting += 500;
				if(waiting > 60*1000){
					break;
				}
			}
		}
		
	}
	
	
	public static String getLocType(String code){
		if("0".equals(code)){
			return "MSA";
		}
		if("1".equals(code)){
			return "Google";
		}
		if("2".equals(code)){
			return "GPS";
		}
		if("3".equals(code)){
			return "MSA->GPS";
		}
		if("4".equals(code)){
			return "GPS->Google";
		}
		if("5".equals(code)){
			return "GPS->MSA";
		}
		if("6".equals(code)){
			return "GPS->MSA->Google";
		}
		if("7".equals(code)){
			return "Google";
		}
		if("8".equals(code)){
			return "Hybrid";
		}
		if("9".equals(code)){
			return "GPS->Google";
		}
		return "δ֪��λ����";
	}
	private static String getStateName(String code){
		if(CmdModel.CMD_FAIL_CODE.equals(code)){
			return CmdModel.CMD_FAIL;
		}
		if(CmdModel.CMD_PROCESS_CODE.equals(code)){
			return CmdModel.CMD_PROCESS;
		}
		if(CmdModel.CMD_SUCCESS_CODE.equals(code)){
			return CmdModel.CMD_SUCCESS;
		}
		if(CmdModel.CMD_TIMEOUT_CODE.equals(code)){
			return CmdModel.CMD_TIMEOUT;
		}
		return "δ֪״̬";
	}
	public static String getActStateName(String state){
		if("0".equals(state)){
			return "δ����";
		}
		if("1".equals(state)){
			return "������";
		}
		if("2".equals(state)){
			return "�Ѽ���";
		}
		if("3".equals(state)){
			return "����ʧ��";
		}
		if("4".equals(state)){
			return "ʧ����";
		}
		if("5".equals(state)){
			return "��ʧ��";
		}
		if("6".equals(state)){
			return "ʧ��ʧ��";
		}
		return "δ֪״̬";
	}

}

