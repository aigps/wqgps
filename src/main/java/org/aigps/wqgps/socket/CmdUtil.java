package org.sunleads.socket;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.ym.model.YmAccessMsg;
import org.sunleads.common.util.AppUtil;

/**
 * 命令工具类
 * @author admin
 *
 */
public class CmdUtil {
	protected static final Log log = LogFactory.getLog(CmdUtil.class);
	private static String CMD_TYPE = "CMD";
	private static String DEVICE_TYPE ="BJDX";
	
	
	/**
	 * 随时定位（点名GetPos）
	 * 
	 * 指令内容:0|LCSNow|定位类型
	 * @param deviceCode 终端号码即手机号码
	 * @param fixModel 定位类型
	 * @return
	 */
	public static void sendLcsNowCmd(String mobileType,String deviceCode,String fixModel){
		try{
			if(StringUtils.isNotBlank(deviceCode)){
				String smsSender = (String) AppUtil.getBean("smsSender");
				YmAccessMsg ymAccessMsg = new YmAccessMsg(CMD_TYPE, DEVICE_TYPE, deviceCode, "0|"+CmdTypeEnum.LCSNow+"|"+mobileType+"|"+fixModel+"|"+smsSender);
				GpsClient.getInstrance().sendCmd(ymAccessMsg);
			}
		}catch(Exception e){
			log.error("sendGetPosCmd fail", e);
		}
	}
	
	/**
	 * 激活员工(激活ActiveLCS)
	 * 
	 * 指令内容:0|ActiveLCS|间隔秒钟数|开始时间|结束时间|定位类型
	 * @param deviceCode 终端号码即手机号码
	 * @param fixModel 定位类型
	 * @param minuteIntervalNum 采集间隔分钟数
	 * @param startWorkTime 开始时间
	 * @param endWorkTime 结束时间
	 */
	public static void sendActiveCmd(String mobileType,String deviceCode,String fixModel,long secondInterval,String startWorkTime,String endWorkTime,String workWeekDays){
		try{
			if(StringUtils.isNotBlank(deviceCode)){
				workWeekDays = workWeekDays.replaceAll(",", "");
				String smsSender = (String) AppUtil.getBean("smsSender");
				//后面加类型
				YmAccessMsg ymAccessMsg = new YmAccessMsg(CMD_TYPE, DEVICE_TYPE, deviceCode, "0|"+CmdTypeEnum.ActiveLCS+"|"+mobileType+"|"+secondInterval+"|"+startWorkTime+"|"+endWorkTime+"|"+fixModel+"|"+workWeekDays+"|"+smsSender);
				GpsClient.getInstrance().sendCmd(ymAccessMsg);
			}
		}catch(Exception e){
			log.error("sendActiveCmd fail", e);
		}
	}
	
	/**
	 *失活员工
	 *
	 *指令内容：0|CancelActiveLCS|
	 * @param deviceCode
	 */
	public static void sendCancelActiveCmd(String mobileType,String deviceCode){
		try{
			if(StringUtils.isNotBlank(deviceCode)){
				String smsSender = (String) AppUtil.getBean("smsSender");
				YmAccessMsg ymAccessMsg = new YmAccessMsg(CMD_TYPE, DEVICE_TYPE, deviceCode, "0|"+CmdTypeEnum.CancelActiveLCS+"|"+mobileType+"|"+smsSender);
				GpsClient.getInstrance().sendCmd(ymAccessMsg);
			}
		}catch(Exception e){
			log.error("sendGetPosCmd fail", e);
		}
	}
}
