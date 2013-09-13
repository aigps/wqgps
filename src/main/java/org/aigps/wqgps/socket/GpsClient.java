package org.aigps.wqgps.socket;

import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.netty.util.ChannelUtil;
import org.gps.protocol.net.client.DcNettyClient;
import org.gps.protocol.net.client.DcRecMsgPool;
import org.gps.ym.model.YmAccessMsg;

/**
 * gpsClient�ͻ���
 * @author admins
 *
 */
public class GpsClient {
	protected static final Log log = LogFactory.getLog(GpsClient.class);
	private DcNettyClient dcNettyClient;
	private DcRecMsgHandler observer;
	private static GpsClient gpsClient;
	
	private GpsClient(){
		observer = new DcRecMsgHandler();
		DcRecMsgPool.getInstrance().addObserver(observer);
	}
	
	public static GpsClient getInstrance(){
		if(gpsClient==null){
			gpsClient = new GpsClient();
		}
		return gpsClient;
	}
	
	/**
	 * ����gps�ͻ��˼���
	 * @param host ip
	 * @param port �˿�
	 * @param userName ��¼�û���
	 * @param pwd ��¼�û�����
	 */
	public void startGpsClient(){
		try {
			String ip = AppUtil.getBean("serverIp").toString();
			String port = AppUtil.getBean("serverPort").toString();
			dcNettyClient = new DcNettyClient(ip, port, "gps_admin", "password");
			dcNettyClient.connect();
		} catch (Exception e) {
			log.error("startGpsClient fail", e);
		}
	}
	
	/**
	 * �ر�gps�ͻ��˼���
	 */
	public void stopGpsClient(){
		try {
			if(dcNettyClient!=null){
				dcNettyClient.disConnect();
				dcNettyClient = null;
			}
		} catch (Exception e) {
			log.error("stopGpsClient fail", e);
		}
	}

	/**
	 * �����Ƿ�������״̬
	 */
	public boolean isConnected(){
		try {
			return dcNettyClient.isConnected();
		} catch (Exception e) {
			log.error("isConnected fail", e);
		}
		return false;
	}
	
	public DcNettyClient getDcClient() {
		return dcNettyClient;
	}
	
	/**
	 * ����ָ��
	 * @param cmd
	 */
	public void sendCmd(YmAccessMsg ymAccessMsg){
		try {
			if(ymAccessMsg!=null){
				log.info("sendCmd---->YmString:"+ymAccessMsg.toYmString());
				ChannelUtil.sendMsg(dcNettyClient.getChannel(), ymAccessMsg.toYmString().getBytes());
			}
		} catch (Exception e) {
			log.error("sendCmd fail", e);
		}
	}
}
