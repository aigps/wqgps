package org.aigps.wqgps.socket;

import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.ym.model.YmAccessMsg;

/**
 * �������
 * @author admin
 *
 */
public class CmdUtil {
	protected static final Log log = LogFactory.getLog(CmdUtil.class);
	private static String CMD_TYPE = "CMD";
	private static String DEVICE_TYPE ="BJDX";
	
	
	/**
	 * ��ʱ��λ������GetPos��
	 * 
	 * ָ������:0|LCSNow|��λ����
	 * @param deviceCode �ն˺��뼴�ֻ�����
	 * @param fixModel ��λ����
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
	 * ����Ա��(����ActiveLCS)
	 * 
	 * ָ������:0|ActiveLCS|���������|��ʼʱ��|����ʱ��|��λ����
	 * @param deviceCode �ն˺��뼴�ֻ�����
	 * @param fixModel ��λ����
	 * @param minuteIntervalNum �ɼ����������
	 * @param startWorkTime ��ʼʱ��
	 * @param endWorkTime ����ʱ��
	 */
	public static void sendActiveCmd(String mobileType,String deviceCode,String fixModel,long secondInterval,String startWorkTime,String endWorkTime,String workWeekDays){
		try{
			if(StringUtils.isNotBlank(deviceCode)){
				workWeekDays = workWeekDays.replaceAll(",", "");
				String smsSender = (String) AppUtil.getBean("smsSender");
				//���������
				YmAccessMsg ymAccessMsg = new YmAccessMsg(CMD_TYPE, DEVICE_TYPE, deviceCode, "0|"+CmdTypeEnum.ActiveLCS+"|"+mobileType+"|"+secondInterval+"|"+startWorkTime+"|"+endWorkTime+"|"+fixModel+"|"+workWeekDays+"|"+smsSender);
				GpsClient.getInstrance().sendCmd(ymAccessMsg);
			}
		}catch(Exception e){
			log.error("sendActiveCmd fail", e);
		}
	}
	
	/**
	 *ʧ��Ա��
	 *
	 *ָ�����ݣ�0|CancelActiveLCS|
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
