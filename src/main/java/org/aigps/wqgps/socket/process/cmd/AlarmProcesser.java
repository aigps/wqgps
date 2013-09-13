package org.aigps.wqgps.socket.process.cmd;

import java.util.ArrayList;
import java.util.List;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.ym.model.YmAccessMsg;

/**
 * 回复下发指令处理器
 * @author admin
 *
 */

public class AlarmProcesser {

	protected static final Log log = LogFactory.getLog(AlarmProcesser.class);

	public static void process(YmAccessMsg msg) {
		if(StringUtils.isBlank(msg.getData())){
			return;
		}
		try{
			log.error("=============user_data:"+msg.getData());
			String[] contentArr = msg.getData().split("\\|");
			if(contentArr!=null && contentArr.length>3){
				String staffId = getStaffId(msg.getDeviceCode(),msg.getDeviceType());
				if(StringUtils.isBlank(staffId)){
					log.error("=============staffId:"+staffId);
					return;
				}
				String userData = contentArr[3];
				log.error("=============userData:"+userData);
				
				boolean isLeaveAlarm = isLeaveAlarm(userData), isSZMsg = isSZMsg(userData);
				if(!isLeaveAlarm && !isSZMsg){
					return;
				}

				String alarmTime = contentArr[2];
				alarmTime = DateUtil.dateNumberToDateStr(alarmTime);
				
				if(isLeaveAlarm == true){//脱离告警
					List<String[]> list = DataCache.staffAlarmMap.get("03");
					if(list == null){
						DataCache.staffAlarmMap.put("03", list=new ArrayList<String[]>());
					}
					list.add(new String[]{"03",staffId,alarmTime});
					log.error("=============add 脱离告警");
					return;
				}
				
				if(userData.startsWith("a5c001ffff07bb")||userData.startsWith("A5C001FFFF07BB")){//剪断信号
					List<String[]> list = DataCache.staffAlarmMap.get("01");
					if(list == null){
						DataCache.staffAlarmMap.put("01", list=new ArrayList<String[]>());
					}
					list.add(new String[]{"01",staffId,alarmTime});
					log.error("=============add 剪断信号");
					return;
				}
				
				if(userData.startsWith("a5c001ffff07ba")||userData.startsWith("A5C001FFFF07BA")){//心跳信号和低电压信号
					String flag = userData.substring(24, 26);
					flag=Integer.toBinaryString(Integer.parseInt(flag, 16));
					while(flag.length()<8){
						flag = "0"+flag;
					}
					if(flag.charAt(1) == '1'){//低电压信号
						List<String[]> list = DataCache.staffAlarmMap.get("02");
						if(list == null){
							DataCache.staffAlarmMap.put("02", list=new ArrayList<String[]>());
						}
						list.add(new String[]{"02",staffId,alarmTime});
						log.error("=============add 低电压信号");
					}
					return;
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}
	
	private static boolean isLeaveAlarm(String userData){
		return userData.startsWith("LEAVE_ALARM") || userData.startsWith("leave_alarm");
	}
	
	//是否是手镯信息
	private static boolean isSZMsg(String userData){
		return userData.startsWith("a5c001ffff07") || userData.startsWith("A5C001FFFF07");
	}
	
	public static void main(String[] args){
		String userData = "A5C001FFFF07BA00010000065DDCC27E";
		String flag = userData.substring(24, 26);
		flag=Integer.toBinaryString(Integer.parseInt(flag, 16));
		while(flag.length()<8){
			flag = "0"+flag;
		}
		System.out.println(flag.charAt(1) == '1');
	}
	
	private static String getStaffId(String code,String type){
		for(WqStaffInfo staff : DataCache.staffMap.values()){
			if("BJDX".equalsIgnoreCase(type)){
				if(code.equals(staff.getMobileNumber())){
					return staff.getId();
				}
			}
			if("IMSI".equalsIgnoreCase(type)){
				if(code.equals(staff.getMsid())){
					return staff.getId();
				}
			}
		}
		return null;
	}
}
