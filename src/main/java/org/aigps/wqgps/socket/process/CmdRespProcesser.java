package org.aigps.wqgps.socket.process;

import org.aigps.wqgps.socket.CmdTypeEnum;
import org.aigps.wqgps.socket.process.cmdresp.CmdRespForActive;
import org.aigps.wqgps.socket.process.cmdresp.CmdRespForCancelActive;
import org.aigps.wqgps.socket.process.cmdresp.CmdRespForLcsNow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.ym.model.YmAccessMsg;

/**
 * �ظ��·�ָ�����
 * @author admin
 *
 */
public class CmdRespProcesser implements MsgProcesser {

	protected static final Log log = LogFactory.getLog(CmdRespProcesser.class);
	
	/**
	 * �ظ��·�ָ�������
	 */
	public static final String PROCESS_TYPE = "CMD_RESP";
	
	public void process(YmAccessMsg accessMsg) {
		if(accessMsg!=null && accessMsg.getData()!=null){
			String content = accessMsg.getData();
			String[] contentArr = content.split("\\|");
			log.info("==================ָ��ظ���"+content);
			if(contentArr!=null && contentArr.length==3){
				String tmnCode = accessMsg.getDeviceCode();//�ֻ��ż��ն˺�
				String cmdType = contentArr[1];//��������
				String cmdState = contentArr[2];//����״̬
				if(CmdTypeEnum.LCSNow.getValue().equals(cmdType)){//��ȡ��ǰλ��
					CmdRespForLcsNow.process(tmnCode, cmdState);
				}else if(CmdTypeEnum.ActiveLCS.getValue().equals(cmdType)){//����Ա���ظ�
					CmdRespForActive.process(tmnCode, cmdState);
				}else if(CmdTypeEnum.CancelActiveLCS.getValue().equals(cmdType)){//ʧ��Ա���ظ�
					CmdRespForCancelActive.process(tmnCode, cmdState);
				}
				log.info("==================ָ��ظ���"+tmnCode+"  "+cmdState);
			}
		}
	}
}
