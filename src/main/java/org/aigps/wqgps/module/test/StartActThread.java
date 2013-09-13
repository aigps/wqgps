
package org.aigps.wqgps.module.test;

import java.util.ArrayList;
import java.util.HashMap;
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
 * Create Date：  2011-9-13下午02:00:22
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
public class StartActThread  extends Thread{
	private String staffId;
	private String fixModels;
	private String mobileNumber;
	private static int index = 0;
	public static boolean finish = false;
	
	public StartActThread(String staffId,String fixModels,String mobileNumber){
		this.staffId = staffId;
		this.fixModels = fixModels;
		this.mobileNumber = mobileNumber;
		finish = false;
	}
	
	public void run(){
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		List<String> ids = new ArrayList<String>();
		ids.add(staffId);
		Boolean hadInit = false;
		Map<String,Object> map = null;
		
		while(MobileTestService.running){
			try{
				String[] numTime = CmdStateCache.activeTmnNumMap.get(mobileNumber);
				if(numTime == null){//激活，有可能是第一次，或激活成功
					if(hadInit == true){//激活成功了，执行去激活
						map.put("actState", MobileTestService.getActStateName(staff.getActivateState()));
						Thread t = new StartInActThread(staffId,fixModels,mobileNumber,map);
						t.setDaemon(true);
						t.start();
						break;
					}else{//第一次激活
						hadInit = true;
						SysManagerService service = (SysManagerService)AppUtil.getBean("sysManagerService");
						if(fixModels.length()<=index){
							finish = true;
							index = 0;
							break;
						}
						String fixModel = fixModels.charAt(index++)+"";
						service.activateStaffState(ids,fixModel);

						map = new HashMap<String,Object>();
						map.put("fixModel", MobileTestService.getLocType(fixModel));
						map.put("actState", MobileTestService.getActStateName(staff.getActivateState()));
						MobileTestService.actList.add(map);
					}
				}
				map.put("actState", MobileTestService.getActStateName(staff.getActivateState()));
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
		CmdStateCache.activeTmnNumMap.remove(mobileNumber);
	}

}

