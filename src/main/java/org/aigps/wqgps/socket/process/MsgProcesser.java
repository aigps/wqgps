package org.aigps.wqgps.socket.process;

import org.gps.ym.model.YmAccessMsg;
/**
 * 消息处理者
 * @author admins
 *
 */
public interface MsgProcesser {
	
	public void process(YmAccessMsg accessMsg);

}
