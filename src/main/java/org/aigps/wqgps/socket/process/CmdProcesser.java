package org.sunleads.socket.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.ym.model.YmAccessMsg;
import org.sunleads.socket.process.cmd.AlarmProcesser;
import org.sunleads.socket.process.cmd.SmsProcesser;

/**
 * �ظ��·�ָ�����
 * @author admin
 *
 */
public class CmdProcesser implements MsgProcesser {

	protected static final Log log = LogFactory.getLog(CmdProcesser.class);
	
	/**
	 * �ظ��·�ָ�������
	 */
	public static final String PROCESS_TYPE = "CMD";
	
	@Override
	public void process(YmAccessMsg accessMsg) {
		if(accessMsg!=null && accessMsg.getData()!=null){
			String content = accessMsg.getData();
			String[] contentArr = content.split("\\|");
			
			if(contentArr!=null && contentArr.length>2){
				String cmdType = contentArr[1];//��������
				if("UploadSMS".equalsIgnoreCase(cmdType)){//������Ϣ����
					SmsProcesser.process(accessMsg);
				}else if("WqAlarm".equalsIgnoreCase(cmdType)){//����澯
					AlarmProcesser.process(accessMsg);
				}
			}
		}
	}
}
