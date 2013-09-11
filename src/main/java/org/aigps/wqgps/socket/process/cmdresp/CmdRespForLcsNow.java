package org.sunleads.socket.process.cmdresp;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.CmdStateCache;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.entity.CmdModel;
import org.sunleads.socket.CmdTypeEnum;

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
