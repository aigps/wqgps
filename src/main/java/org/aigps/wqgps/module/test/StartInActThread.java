
package org.aigps.wqgps.module.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.cache.CmdStateCache;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.module.sysmanager.service.SysManagerService;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-9-13下午02:00:51
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
public class StartInActThread  extends Thread{
	private String staffId;
	private String mobileNumber;
	private String fixModels;
	private Map<String,Object> map;
	
	public StartInActThread(String staffId,String fixModels,String mobileNumber,Map<String,Object> map){
		this.staffId = staffId;
		this.fixModels = fixModels;
		this.mobileNumber = mobileNumber;
		this.map = map;
	}
	
	public void run(){
		List<String> ids = new ArrayList<String>();
		ids.add(staffId);
		Boolean hadInit = false;
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		
		while(MobileTestService.running){
			try{
				map.put("inActState", MobileTestService.getActStateName(staff.getActivateState()));
				String[] numTime = CmdStateCache.cancelActiveTmnNumMap.get(mobileNumber);
				if(numTime == null){//失活，有可能是第一次失活，或者失活成功
					if(hadInit == true){//失活成功了，执行激活
						Thread t = new StartActThread(staffId,fixModels,mobileNumber);
						t.setDaemon(true);
						t.start();
						break;
					}
					hadInit = true;
					SysManagerService service = (SysManagerService)AppUtil.getBean("sysManagerService");
					service.cancelActivateStaffState(ids);
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
		CmdStateCache.cancelActiveTmnNumMap.remove(mobileNumber);
	}
}