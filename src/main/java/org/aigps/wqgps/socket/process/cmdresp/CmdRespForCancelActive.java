package org.aigps.wqgps.socket.process.cmdresp;

import org.aigps.wqgps.common.cache.CmdStateCache;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.module.sysmanager.service.SysManagerService;
import org.aigps.wqgps.socket.CmdUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 回复设置定位间隔命令
 * @author admin
 *
 */
public class CmdRespForCancelActive {

	protected static final Log log = LogFactory.getLog(CmdRespForCancelActive.class);
	
	/**
	 * 处理
	 * @param tmnCode 终端号即手机号
	 * @param cmdState 回复状态（非0则失败，0则成功）
	 */
	public static void process(String tmnCode,String cmdState){
		if(!CmdStateCache.cancelActiveTmnNumMap.containsKey(tmnCode)){
			return;
		}
		SysManagerService service = (SysManagerService) AppUtil.getBean("sysManagerService");
		try{
			if("0".equals(cmdState)){//成功
				CmdStateCache.cancelActiveTmnNumMap.remove(tmnCode);
				service.updateActiveState(tmnCode,"5");//失活成功
			}else{//失败
				String[] numTime = CmdStateCache.cancelActiveTmnNumMap.get(tmnCode);
				if(numTime == null){
					return;
				}
				Integer nums = Integer.parseInt(numTime[0]);//次数
				if(nums==null || nums<=3){
					WqStaffInfo staff = DataCache.staffMap.get(DataCache.phoneStaffIdMap.get(tmnCode));
					CmdUtil.sendCancelActiveCmd(staff.getMobileType(),tmnCode);
					numTime[0] = String.valueOf(nums==null?1:nums+1);
				}else{
					CmdStateCache.cancelActiveTmnNumMap.remove(tmnCode);
					service.updateActiveState(tmnCode,"6");//失活失败
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
			try{
				service.updateActiveState(tmnCode,"6");//失活失败
			}catch(Exception e1){
				log.error(e1.getMessage(), e1);
			}
		}
	}
	
	
}
