package org.sunleads.common.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sunleads.common.entity.CmdModel;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.DateUtil;

/**
 * ָ��״̬cache
 * @author admin
 *
 */
public class CmdStateCache {
	public final static Log log = LogFactory.getLog(CmdStateCache.class);
	/**
	 * �ȴ��ظ�״̬
	 */
	public static String CMD_PROCESS_CODE = "0";
	
	/**
	 * ����Ա����¼����
	 * key:�ն˺�,value:0,���ͼ��������1,�ϴη���ָ��ʱ��
	 */
	public static Map<String, String[]> activeTmnNumMap = new ConcurrentHashMap<String, String[]>();

	/**
	 * ʧ��Ա����¼����
	 * key:�ն˺�,value:0,����ʧ�������1,�ϴη���ָ��ʱ��
	 */
	public static Map<String, String[]> cancelActiveTmnNumMap = new ConcurrentHashMap<String, String[]>();
	
	/**
	 * ָ��״̬����
	 * key:����ָ���û���_ָ������,[key:Ա����ţ�value:ָ��ģ��]
	 */
	private static Map<String,Map<String,CmdModel>> cmdStateMap = new HashMap<String,Map<String,CmdModel>>();
	
	/**
	 * ��ʼ����������״̬
	 * @param cmdModel ���͵�����ģ��
	 */
	public static void initSendCmdStateMap(CmdModel cmdModel){
		try{
			String key = cmdModel.getUserName().concat("_").concat(cmdModel.getCmdTypeCode());
			String staffId = cmdModel.getStaffId();
			if(cmdStateMap.containsKey(key)){
				Map<String,CmdModel> staffIdCmdMap = cmdStateMap.get(key);
				if(staffIdCmdMap.containsKey(staffId)){
					CmdModel tmpModel = staffIdCmdMap.get(staffId);
					if(!CmdModel.CMD_PROCESS_CODE.equals(tmpModel.getCmdStateCode())){//�����ڵȴ��ظ�״̬ʱ
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
	 * ���»��Ƴ�ָ��ģ��
	 * ����updateTimeNumʱ��ĸ�Ϊ��ʱ״̬
	 * ����remoteTimeNumʱ����Ƴ���ģ��
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
					if(CmdModel.CMD_PROCESS_CODE.equals(tmpModel.getCmdStateCode())){//���ڵȴ��ظ�״̬ʱ
						boolean updateTimeLessThen = DateUtil.validateTimeByDiffValue(sendTime, nowTime, updateTimeNum);
						if(!updateTimeLessThen){//����Ϊ��ʱ
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
	 * ��֤�Ƿ��ڡ��ȴ��ظ���״̬
	 * @param cmdModel
	 * @return ����false��ʾ�񣬷����ʾ��
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
	 * ����ָ��״̬��
	 * @param cmdTypeCode ָ������
	 * @param staffId Ա�����
	 * @param cmdStateCode ���õ�����״̬
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
	 * ��ȡָ��״̬��
	 * @param cmdTypeCode ָ������
	 * @param staffId Ա�����
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
