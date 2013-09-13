
package org.aigps.wqgps.common.log;

import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.module.sms.smgp.DxSmsSender;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-9-1上午11:02:44
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
public class LocateThread extends Thread{

	private Map<String,String> locate;
	private List<Map<String,String>> locateList;
	public LocateThread(Map<String,String> locate,List<Map<String,String>> locateList){
		this.locate = locate;
		this.locateList = locateList;
	}
	
	public void run() {
		String mobileNumber = locate.get("mobileNumber");
		String locateWay = locate.get("locateWay");
		int minSecond = Integer.parseInt(locate.get("minSecond"));
		int maxSecond = Integer.parseInt(locate.get("maxSecond"));
		int totalCount = Integer.parseInt(locate.get("totalCount"));
		int locateCount = Integer.parseInt(locate.get("locateCount"));
		int locateFailCount = Integer.parseInt(locate.get("locateFailCount"));
		
		String mnLast = mobileNumber.substring(3);
		String mnFirst = mobileNumber.substring(0,3);
		String msgContent = String.format(
				"//BREW:0109c274:V01#122.224.88.34:9016#CTLBS#%s#%s01#1#%s#0#0",
				mnLast, mnFirst,locateWay);
		
		while(locateList.contains(locate)){
			if(totalCount<=locateCount){
				break;
			}
			try{
				DxSmsSender smsSender = (DxSmsSender) AppUtil.getBean("smSender");
				boolean sendState=smsSender.send(new String[]{mobileNumber}, msgContent,"00","8");
	
				if(sendState == false){
					locate.put("locateFailCount", (++locateFailCount)+"");
				}
				locate.put("locateCount", (++locateCount)+"");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					long second = Math.round(Math.random()*(maxSecond-minSecond)+minSecond);
					Thread.sleep(second*1000);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

}

