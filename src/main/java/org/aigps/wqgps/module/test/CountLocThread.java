
package org.sunleads.module.test;

import java.util.Map;

import org.sunleads.common.cache.DataCache;
import org.sunleads.common.entity.DcGpsReal;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-9-13下午02:00:22
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
public class CountLocThread extends Thread{
	private String staffId;
	private Map<String,Object> data;
	private String reportTime = null;
	public boolean stop = false;
	
	public CountLocThread(String staffId,Map<String,Object> data){
		this.staffId = staffId;
		this.data = data;
	}
	
	public void run(){
		while(!stop && MobileTestService.running){
			try{
				DcGpsReal gps = DataCache.staffPostionMap.get(staffId);
				if(gps != null){
					if(reportTime == null){
						reportTime = gps.getReportTime();
					}else if(!reportTime.equals(gps.getReportTime())){
						Integer locNum = (Integer)data.get("locNum");
						data.put("locNum",++locNum);
						reportTime = gps.getReportTime();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
					Thread.sleep(500);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

}

