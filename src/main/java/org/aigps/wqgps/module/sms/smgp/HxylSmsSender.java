package org.aigps.wqgps.module.sms.smgp;

import org.aigps.wqgps.module.sms.meilin.MeiLinSmsServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class HxylSmsSender implements ISmsSender{
	public final static Log log = LogFactory.getLog(HxylSmsSender.class);

	/**
	 * ����һ����Ϣ�������������Ϣ���͡�
	 * 
	 * @param revicePhone
	 *            ����Ϣ���պ��룬С��100���ַ�
	 * @param msgContent
	 *            ����Ϣ����
	 * @return true�����ͳɹ���false������ʧ�ܡ�
	 */
	public boolean send(String[] recivePhone, String msgContent,String sendType,String smsformat) {
		try {
			StringBuffer telphones = new StringBuffer();
			for(String tel:recivePhone){
				telphones.append(tel+";");
			}
			telphones.delete(telphones.length()-1, telphones.length());
			
			MeiLinSmsServer server = MeiLinSmsServer.getInstance();
			server.sendMsg7bit(telphones.toString(), msgContent);
			
		} catch (Exception e) {
			log.error("", e);
			return false;
		}
		return true;
	}

	public boolean send(String fNeedRead, String fTakeKey, String fReceivers,
			String fContent) {
		try {
			MeiLinSmsServer server = MeiLinSmsServer.getInstance();
			return server.sendMsg7bit(fNeedRead, fTakeKey,fReceivers,fContent);
		} catch (Exception e) {
			log.error("", e);
		}
		return false;
	}

}
