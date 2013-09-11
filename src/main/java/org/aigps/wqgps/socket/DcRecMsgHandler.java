package org.sunleads.socket;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.protocol.net.client.DcRecMsgPool;
import org.gps.ym.model.YmAccessMsg;
import org.sunleads.socket.process.CmdProcesser;
import org.sunleads.socket.process.CmdRespProcesser;
import org.sunleads.socket.process.GpsPostionProcesser;
import org.sunleads.socket.process.MsgProcesser;

/**
 * 接收消息处理类
 * @author Administrator
 *
 */
public class DcRecMsgHandler implements Observer {
	private static final Log log = LogFactory.getLog(DcRecMsgHandler.class);
	
	@Override
	public void update(Observable obs, Object args) {
		try {
			if(obs!=null && obs instanceof DcRecMsgPool){
				if(args!=null && args instanceof YmAccessMsg){
					YmAccessMsg ymMsg = (YmAccessMsg)args ;
					MsgProcesser msgProcesser = getProcesser(ymMsg.getDataType());
					if(msgProcesser!=null){
						msgProcesser.process(ymMsg);
					}
				}
			}
		} catch (Exception e) {
			log.error("update fail",e);
		}
	}
	
	//获取相应的处理器
	public MsgProcesser getProcesser(String processType){
		if(GpsPostionProcesser.PROCESS_TYPE.equals(processType)){//定位信息
			return new GpsPostionProcesser();
		}else if(CmdRespProcesser.PROCESS_TYPE.equals(processType)){//回复指令执行状况处理者
			return new CmdRespProcesser();
		}else if(CmdProcesser.PROCESS_TYPE.equals(processType)){//指令处理者
			return new CmdProcesser();
		}
		return null;
	}
}
