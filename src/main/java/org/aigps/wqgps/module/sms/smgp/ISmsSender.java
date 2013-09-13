
package org.aigps.wqgps.module.sms.smgp;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2012-6-1上午09:40:26
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
public interface ISmsSender {
	public boolean send(String[] recivePhone, String msgContent,String sendType,String smsformat) ;//不需要回执的短信
	
	public boolean send(String fNeedRead, String fTakeKey,String fReceivers,String fContent) ;//需要回执的短信
}

