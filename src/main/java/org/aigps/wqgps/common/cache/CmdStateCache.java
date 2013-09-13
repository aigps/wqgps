package org.aigps.wqgps.common.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aigps.wqgps.common.entity.CmdModel;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 指令状态cache
 * @author admin
 *
 */
public class CmdStateCache {
	public final static Log log = LogFactory.getLog(CmdStateCache.class);
	/**
	 * 等待回复状态
	 */
	public static String CMD_PROCESS_CODE = "0";
	
	/**
	 * 激活员工记录缓存
	 * key:终端号,value:0,发送激活次数；1,上次发送指令时间
	 */
	public static Map<String, String[]> activeTmnNumMap = new ConcurrentHashMap<String, String[]>();

	/**
	 * 失活员工记录缓存
	 * key:终端号,value:0,发送失活次数；1,上次发送指令时间
	 */
	public static Map<String, String[]> cancelActiveTmnNumMap = new ConcurrentHashMap<String, String[]>();
	
	/**
	 * 指令状态缓存
	 * key:发送指令用户名_指令类型,[key:员工编号，value:指令模型]
	 */
	private static Map<String,Map<String,CmdModel>> cmdStateMap = new HashMap<String,Map<String,CmdModel>>();
	
	/**
	 * 初始化发送命令状态
	 * @param cmdModel 发送的命令模型
	 */
	public static void initSendCmdStateMap(CmdModel cmdModel){
		try{
			String key = cmdModel.getUserName().concat("_").concat(cmdModel.getCmdTypeCode());
			String staffId = cmdModel.getStaffId();
			if(cmdStateMap.containsKey(key)){
				Map<String,CmdModel> staffIdCmdMap = cmdStateMap.get(key);
				if(staffIdCmdMap.containsKey(staffId)){
					CmdModel tmpModel = staffIdCmdMap.get(staffId);
					if(!CmdModel.CMD_PROCESS_CODE.equals(tmpModel.getCmdStateCode())){//不处于等待回复状态时
						staffIdCmdMap.put(staffId, cmdModel);
					}
				}else{
					staffIdCmdMap.put(staffId, cmdModel);
				}
			}else{
				Map<String,CmdModel> staffIdCmdMap =  new HashMap<String, CmdModel>();
				staffIdCmdMap.put(staffId, cmdModel);
				cmdStateMap.put(key, staffIdCmdMap);
			}
		}catch(Exception e){
			log.error("initSendCmdStateMap fail", e);
		}
	}
	
	/**
	 * 更新或移除指令模型
	 * 超过updateTimeNum时间的改为超时状态
	 * 超过remoteTimeNum时间的移除该模型
	 */
	public static void updateOrRemoveTimeOutModel(long updateTimeNum,long remoteTimeNum){
		try {
			String nowTime = DateUtil.getCurDate();
			List<CmdModel> removeList = new ArrayList<CmdModel>();
			for (Map.Entry<String, Map<String, CmdModel>> entry : cmdStateMap.entrySet()) {
				removeList.clear();
				Map<String, CmdModel> staffIdCmdMap = entry.getValue();
				Collection<CmdModel> values = staffIdCmdMap.values();
				for (CmdModel tmpModel : values) {
					String sendTime = tmpModel.getSendTime();
					if(CmdModel.CMD_PROCESS_CODE.equals(tmpModel.getCmdStateCode())){//处于等待回复状态时
						boolean updateTimeLessThen = DateUtil.validateTimeByDiffValue(sendTime, nowTime, updateTimeNum);
						if(!updateTimeLessThen){//更新为超时
							tmpModel.setCmdStateCode(CmdModel.CMD_TIMEOUT_CODE);
						}
					}
					boolean remoteTimeLessThen = DateUtil.validateTimeByDiffValue(sendTime, nowTime, remoteTimeNum);
					if (!remoteTimeLessThen) {
						removeList.add(tmpModel);
					}
				}
				values.removeAll(removeList);
				log.error("removeTimeOutModel size:"+removeList.size());
			}
		} catch (Exception e) {
			log.error("removeTimeOutModel fail", e);
		}
	}
	
	/**
	 * 验证是否处于“等待回复”状态
	 * @param cmdModel
	 * @return 返回false表示否，否则表示是
	 */
	public static boolean validateCmdProcessCode(String cmdTypeCode,String staffId){
		boolean flag = false;
		try {
			String cmdStateCode = getCmdStateCode(cmdTypeCode, staffId);
			if (CmdModel.CMD_PROCESS_CODE.equals(cmdStateCode)) {
				flag = true;
			}
		} catch (Exception e) {
			log.error("validateCmdProcessCode fail", e);
		}
		return flag;
	}
	
	/**
	 * 设置指令状态码
	 * @param cmdTypeCode 指令类型
	 * @param staffId 员工编号
	 * @param cmdStateCode 设置的命令状态
	 */
	public static void setCmdStateCode(String cmdTypeCode,String staffId,String cmdStateCode){
		try {
			for (Map.Entry<String, Map<String, CmdModel>> entry : cmdStateMap
					.entrySet()) {
				String key = entry.getKey();
				Map<String, CmdModel> values = entry.getValue();
				if (key.contains(cmdTypeCode)) {
					if (values.containsKey(staffId)) {
						CmdModel cmdModel = values.get(staffId);
						cmdModel.setCmdStateCode(cmdStateCode);
					}
				}
			}
		} catch (Exception e) {
			log.error("setCmdStateCode fail", e);
		}
	}
	
	/**
	 * 获取指令状态码
	 * @param cmdTypeCode 指令类型
	 * @param staffId 员工编号
	 */
	public static String getCmdStateCode(String cmdTypeCode,String staffId){
		String state = null;
		try{
			String userName = AppUtil.getUserInfo().getUserName();
			String key = userName.concat("_").concat(cmdTypeCode);
			if(cmdStateMap.containsKey(key)){
				Map<String,CmdModel> staffIdCmdMap = cmdStateMap.get(key);
				if(staffIdCmdMap.containsKey(staffId)){
					CmdModel cmdModel = staffIdCmdMap.get(staffId);
					state = cmdModel.getCmdStateCode();
				}
			}
		}catch(Exception e){
			log.error("getCmdStateCode fail", e);
		}
		return state;
	}
}
