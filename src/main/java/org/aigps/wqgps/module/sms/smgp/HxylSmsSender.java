package org.sunleads.module.sms.smgp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.sunleads.module.sms.meilin.MeiLinSmsServer;

@Component
public class HxylSmsSender implements ISmsSender{
	public final static Log log = LogFactory.getLog(HxylSmsSender.class);

	/**
	 * 发送一条消息，完成真正的消息发送。
	 * 
	 * @param revicePhone
	 *            短消息接收号码，小于100个字符
	 * @param msgContent
	 *            短消息内容
	 * @return true：发送成功。false：发送失败。
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

	@Override
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
