package org.aigps.wqgps.socket.process;

import org.gps.ym.model.YmAccessMsg;
/**
 * ��Ϣ������
 * @author admins
 *
 */
public interface MsgProcesser {
	
	public void process(YmAccessMsg accessMsg);

}
