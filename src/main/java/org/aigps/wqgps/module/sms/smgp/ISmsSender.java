
package org.aigps.wqgps.module.sms.smgp;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2012-6-1����09:40:26
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
 */
public interface ISmsSender {
	public boolean send(String[] recivePhone, String msgContent,String sendType,String smsformat) ;//����Ҫ��ִ�Ķ���
	
	public boolean send(String fNeedRead, String fTakeKey,String fReceivers,String fContent) ;//��Ҫ��ִ�Ķ���
}

