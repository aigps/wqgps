package org.sunleads.socket.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.ym.model.YmAccessMsg;
import org.sunleads.socket.CmdTypeEnum;
import org.sunleads.socket.process.cmdresp.CmdRespForActive;
import org.sunleads.socket.process.cmdresp.CmdRespForCancelActive;
import org.sunleads.socket.process.cmdresp.CmdRespForLcsNow;

/**
 * 回复下发指令处理器
 * @author admin
 *
 */
public class CmdRespProcesser implements MsgProcesser {

	protected static final Log log = LogFactory.getLog(CmdRespProcesser.class);
	
	/**
	 * 回复下发指令处理类型
	 */
	public static final String PROCESS_TYPE = "CMD_RESP";
	
	@Override
	public void process(YmAccessMsg accessMsg) {
		if(accessMsg!=null && accessMsg.getData()!=null){
			String content = accessMsg.getData();
			String[] contentArr = content.split("\\|");
			log.info("==================指令回复："+content);
			if(contentArr!=null && contentArr.length==3){
				String tmnCode = accessMsg.getDeviceCode();//手机号即终端号
				String cmdType = contentArr[1];//命令类型
				String cmdState = contentArr[2];//命令状态
				if(CmdTypeEnum.LCSNow.getValue().equals(cmdType)){//获取当前位置
					CmdRespForLcsNow.process(tmnCode, cmdState);
				}else if(CmdTypeEnum.ActiveLCS.getValue().equals(cmdType)){//激活员工回复
					CmdRespForActive.process(tmnCode, cmdState);
				}else if(CmdTypeEnum.CancelActiveLCS.getValue().equals(cmdType)){//失活员工回复
					CmdRespForCancelActive.process(tmnCode, cmdState);
				}
				log.info("==================指令回复："+tmnCode+"  "+cmdState);
			}
		}
	}
}
