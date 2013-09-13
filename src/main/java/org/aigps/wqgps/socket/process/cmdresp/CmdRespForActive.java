package org.aigps.wqgps.socket.process.cmdresp;

import org.aigps.wqgps.common.cache.CmdStateCache;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.module.sysmanager.service.SysManagerService;
import org.aigps.wqgps.socket.CmdUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 回复设置定位间隔命令
 * @author admin
 *
 */
public class CmdRespForActive {
	protected static final Log log = LogFactory.getLog(CmdRespForActive.class);
	
	/**
	 * 处理
	 * @param tmnCode 终端号即手机号
	 * @param cmdState 回复状态（非0则失败，0则成功）
	 */
	public static void process(String tmnCode,String cmdState){
		if(!CmdStateCache.activeTmnNumMap.containsKey(tmnCode)){
			return;
		}
		try{
			SysManagerService service = (SysManagerService) AppUtil.getBean("sysManagerService");
			if("0".equals(cmdState)){//成功
				CmdStateCache.activeTmnNumMap.remove(tmnCode);
				service.updateActiveState(tmnCode,"2");//成功
				log.error("激活成功："+tmnCode);
			}else{//失败
				String[] numTime = CmdStateCache.activeTmnNumMap.get(tmnCode);
				if(numTime==null){
					return;
				}
				Integer nums = Integer.parseInt(numTime[0]);//次数
				if(nums==null || nums<=3){
					String staffId = DataCache.phoneStaffIdMap.get(tmnCode);
					if(StringUtils.isNotBlank(staffId)){
						WqStaffInfo staff = DataCache.staffMap.get(staffId);
						if(staff!=null){
							String startWorkTime = staff.getStartWorkTime().replaceAll(":", "").concat("00");
							String endWorkTime = staff.getEndWorkTime().replaceAll(":", "").concat("59");
							CmdUtil.sendActiveCmd(staff.getMobileType(),tmnCode,staff.getFixModel(),staff.getGpsInterval(),startWorkTime,endWorkTime,staff.getWorkWeekDays());
						}
						numTime[0] = String.valueOf(nums==null?1:nums+1);
					}
				}else{
					CmdStateCache.activeTmnNumMap.remove(tmnCode);
					service.updateActiveState(tmnCode,"3");//失败
				}
				log.error("激活失败："+tmnCode);
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}
	
	
}
