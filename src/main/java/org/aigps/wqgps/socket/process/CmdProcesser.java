package org.sunleads.socket.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.ym.model.YmAccessMsg;
import org.sunleads.socket.process.cmd.AlarmProcesser;
import org.sunleads.socket.process.cmd.SmsProcesser;

/**
 * 回复下发指令处理器
 * @author admin
 *
 */
public class CmdProcesser implements MsgProcesser {

	protected static final Log log = LogFactory.getLog(CmdProcesser.class);
	
	/**
	 * 回复下发指令处理类型
	 */
	public static final String PROCESS_TYPE = "CMD";
	
	@Override
	public void process(YmAccessMsg accessMsg) {
		if(accessMsg!=null && accessMsg.getData()!=null){
			String content = accessMsg.getData();
			String[] contentArr = content.split("\\|");
			
			if(contentArr!=null && contentArr.length>2){
				String cmdType = contentArr[1];//命令类型
				if("UploadSMS".equalsIgnoreCase(cmdType)){//短信信息处理
					SmsProcesser.process(accessMsg);
				}else if("WqAlarm".equalsIgnoreCase(cmdType)){//手镯告警
					AlarmProcesser.process(accessMsg);
				}
			}
		}
	}
}
