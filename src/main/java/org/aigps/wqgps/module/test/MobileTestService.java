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
	public static String planLoc = "未执行";
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
		planLoc = "未执行";
		
		//测试激活失活
		if(isAct){
			startTestAct(staffId,fixModels);
		}
		//测试单次定位
		if(isOnce){
			startTestOnceLoc(staffId,fixModels);
		}
		//测试周期定位
		if(isPeriod){
			startTestPeriodLoc(staffId,fixModels,periodLocTime,intervalTime);
		}
		//测试计划定位
		if(isPlan){
			startTestPlanLoc(staffId);
		}
		running = false;
	}
	
	//测试激活失活
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
	
	
	//测试单次定位
	public void startTestOnceLoc(String staffId,final String fixModels) throws Exception{
		onceLocList = new ArrayList<Map<String,Object>>();
		
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		
		//先另其失活
		inAct(staff);
		
		//10秒
		try{Thread.sleep(10000);}catch(Exception e){e.printStackTrace();}
		
		try{
			List<WqStaffInfo> staffs = new ArrayList<WqStaffInfo>();
			staff = (WqStaffInfo)BeanUtils.cloneBean(staff);
			staffs.add(staff);
			
			//每种定位类型，都进行一次单次定位
			for(int i=0; i<fixModels.length()&&running; i++){
				String fixModel = fixModels.charAt(i)+"";
				CmdService cmdService = (CmdService)AppUtil.getBean("cmdService");
				Map<String,Object> locData = new HashMap<String,Object>();
				onceLocList.add(locData);
				locData.put("locType", getLocType(fixModel));
				try{
					staff.setFixModel(fixModel);
					//将状态设置为成功的定位，可再次重新定位
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
	
	
	

	//测试周期定位
	public void startTestPeriodLoc(String staffId,String fixModels,String periodLocTime,String intervalTime) throws Exception{
		periodLocList = new ArrayList<Map<String,Object>>();

		List<String> ids = new ArrayList<String>();
		ids.add(staffId);
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		BigDecimal interval = new BigDecimal(intervalTime);
		int periodLocT = Integer.parseInt(periodLocTime);
		
		//每种定位类型，都进行一次周期定位
		for(int i=0; i<fixModels.length()&&running; i++){
			String fixModel = fixModels.charAt(i)+"";

			//发送激活
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
	
	
	

	//测试计划定位
	public void startTestPlanLoc(String staffId) throws Exception{
		planLoc = "开始执行";
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		act(staff,"1", new BigDecimal(1));
		
		LocationService service = (LocationService)AppUtil.getBean("locationService");
		try{
			//建新的测试数据
			WqPlanLocate plan = new WqPlanLocate();
			List<WqPlanLocateDetail> details = new ArrayList<WqPlanLocateDetail>();
			WqPlanLocateDetail detail = new WqPlanLocateDetail();
			detail.setStaffId(staffId);
			details.add(detail);
			
			plan.setName("系统自动测试计划定位");
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, 1);//1分钟之后，跑计划定位
			String planTime = DateUtil.format.format(calendar.getTime());
			plan.setPlanLocateTime(DateUtil.format.format(calendar.getTime()));
			
			service.saveOrUpdateWqPlanLocate(plan, details);
			planLoc = "创建了测试数据";
			boolean start = false;
			while(running){
				Thread.sleep(1000);
				Map<String,WqPlanLocateDetail> updateMap = DataCache.planLocateDetailStaffIdMap.get(staffId);
				if(updateMap == null){
					if(start == true){
						planLoc = "测试成功，接收到定位信息";
						break;
					}
				}else{
					start = true;
					for(Map.Entry<String,WqPlanLocateDetail> entry : updateMap.entrySet()){
						String planLocateTime = entry.getKey();
						if(planLocateTime.equals(planTime)){
							planLoc = "创建了测试数据，测试中";
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
		//发送激活，保存激活次数
		CmdUtil.sendActiveCmd(staff.getMobileType(),staff.getMobileNumber(),fixModel,interval.longValue(),"000001","235959",staff.getWorkWeekDays());
		CmdStateCache.activeTmnNumMap.put(staff.getMobileNumber(),new String[]{"1",DateUtil.getCurDate()});
		
		while(running){
			try{
				try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
				String[] numTime = CmdStateCache.activeTmnNumMap.get(staff.getMobileNumber());
				if(numTime == null){//激活成功
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
				//失活成功
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
		return "未知定位类型";
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
		return "未知状态";
	}
	public static String getActStateName(String state){
		if("0".equals(state)){
			return "未激活";
		}
		if("1".equals(state)){
			return "激活中";
		}
		if("2".equals(state)){
			return "已激活";
		}
		if("3".equals(state)){
			return "激活失败";
		}
		if("4".equals(state)){
			return "失活中";
		}
		if("5".equals(state)){
			return "已失活";
		}
		if("6".equals(state)){
			return "失活失败";
		}
		return "未知状态";
	}

}

