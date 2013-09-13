package org.aigps.wqgps.socket;

import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.netty.util.ChannelUtil;
import org.gps.protocol.net.client.DcNettyClient;
import org.gps.protocol.net.client.DcRecMsgPool;
import org.gps.ym.model.YmAccessMsg;

/**
 * gpsClient客户端
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
	 * 启动gps客户端监听
	 * @param host ip
	 * @param port 端口
	 * @param userName 登录用户名
	 * @param pwd 登录用户密码
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
	 * 关闭gps客户端监听
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
	 * 返回是否处于链接状态
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
	 * 发送指令
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
