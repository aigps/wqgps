package org.aigps.wqgps.socket.process.cmdresp;

import org.aigps.wqgps.common.cache.CmdStateCache;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.CmdModel;
import org.aigps.wqgps.socket.CmdTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 回复获取当前位置命令
 * @author admin
 *
 */
@Component
@Transactional
public class CmdRespForLcsNow {

	/**
	 * 处理
	 * @param tmnCode 终端号即手机号
	 * @param cmdState 回复状态（非0则失败，0则成功）
	 */
	public static void process(String tmnCode,String cmdState){
		String staffId = DataCache.phoneStaffIdMap.get(tmnCode);
		if(StringUtils.isNotBlank(staffId)){
			String returnState = "0".equals(cmdState)?CmdModel.CMD_SUCCESS_CODE:CmdModel.CMD_FAIL_CODE;
			CmdStateCache.setCmdStateCode(CmdTypeEnum.LCSNow.getValue(), staffId, returnState);
		}
	}
	
}
