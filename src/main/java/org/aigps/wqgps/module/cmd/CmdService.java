package org.aigps.wqgps.module.cmd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.cache.CmdStateCache;
import org.aigps.wqgps.common.entity.CmdModel;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.socket.CmdTypeEnum;
import org.aigps.wqgps.socket.CmdUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 定位服务
 * @author admin
 *
 */
@Component
@Transactional
public class CmdService {
	public final static Log log = LogFactory.getLog(CmdService.class);
	
	/**
	 * 发送点名
	 * @param wqStaffInfoList
	 * @throws Exception
	 */
	public void sendGetPosCmd(List<WqStaffInfo> wqStaffInfoList) throws Exception {
		try{
			String userName = null;
			try{
				userName = AppUtil.getUserInfo().getUserName();
			}catch(Exception e){
			}
			String nowDate = DateUtil.getCurDate();
			for(WqStaffInfo staff :wqStaffInfoList){
				CmdUtil.sendLcsNowCmd(staff.getMobileType(),staff.getMobileNumber(),staff.getFixModel());
				
				boolean sendingflag = CmdStateCache.validateCmdProcessCode(CmdTypeEnum.LCSNow.getValue(), staff.getId());
				if(!sendingflag){//不处于发送状态，则保存发送的指令到缓存
					CmdModel cmdModel = new CmdModel();
					cmdModel.setStaffId(staff.getId());
					cmdModel.setUserName(userName);
					cmdModel.setCmdStateCode(CmdModel.CMD_PROCESS_CODE);
					cmdModel.setCmdTypeCode(CmdTypeEnum.LCSNow.getValue());
					cmdModel.setSendTime(nowDate);
					CmdStateCache.initSendCmdStateMap(cmdModel);
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	/**
	 * 获取点名指令回传状态
	 */
	public Map<String,String> searchGetPosCmdState(List<String> staffIdList){
		Map<String,String> resultMap = new HashMap<String,String>();
		for(String staffId:staffIdList){
			String stateCode = CmdStateCache.getCmdStateCode(CmdTypeEnum.LCSNow.getValue(), staffId);
			if(StringUtils.isNotBlank(stateCode)){
				resultMap.put(staffId, stateCode);
			}
		}
		return resultMap;
	}
}
