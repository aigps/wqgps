package org.sunleads.socket.process.cmdresp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sunleads.common.cache.CmdStateCache;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.util.AppUtil;
import org.sunleads.module.sysmanager.service.SysManagerService;
import org.sunleads.socket.CmdUtil;

/**
 * �ظ����ö�λ�������
 * @author admin
 *
 */
public class CmdRespForCancelActive {

	protected static final Log log = LogFactory.getLog(CmdRespForCancelActive.class);
	
	/**
	 * ����
	 * @param tmnCode �ն˺ż��ֻ���
	 * @param cmdState �ظ�״̬����0��ʧ�ܣ�0��ɹ���
	 */
	public static void process(String tmnCode,String cmdState){
		if(!CmdStateCache.cancelActiveTmnNumMap.containsKey(tmnCode)){
			return;
		}
		SysManagerService service = (SysManagerService) AppUtil.getBean("sysManagerService");
		try{
			if("0".equals(cmdState)){//�ɹ�
				CmdStateCache.cancelActiveTmnNumMap.remove(tmnCode);
				service.updateActiveState(tmnCode,"5");//ʧ��ɹ�
			}else{//ʧ��
				String[] numTime = CmdStateCache.cancelActiveTmnNumMap.get(tmnCode);
				if(numTime == null){
					return;
				}
				Integer nums = Integer.parseInt(numTime[0]);//����
				if(nums==null || nums<=3){
					WqStaffInfo staff = DataCache.staffMap.get(DataCache.phoneStaffIdMap.get(tmnCode));
					CmdUtil.sendCancelActiveCmd(staff.getMobileType(),tmnCode);
					numTime[0] = String.valueOf(nums==null?1:nums+1);
				}else{
					CmdStateCache.cancelActiveTmnNumMap.remove(tmnCode);
					service.updateActiveState(tmnCode,"6");//ʧ��ʧ��
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
			try{
				service.updateActiveState(tmnCode,"6");//ʧ��ʧ��
			}catch(Exception e1){
				log.error(e1.getMessage(), e1);
			}
		}
	}
	
	
}
