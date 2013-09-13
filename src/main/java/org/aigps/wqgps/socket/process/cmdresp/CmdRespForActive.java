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
 * �ظ����ö�λ�������
 * @author admin
 *
 */
public class CmdRespForActive {
	protected static final Log log = LogFactory.getLog(CmdRespForActive.class);
	
	/**
	 * ����
	 * @param tmnCode �ն˺ż��ֻ���
	 * @param cmdState �ظ�״̬����0��ʧ�ܣ�0��ɹ���
	 */
	public static void process(String tmnCode,String cmdState){
		if(!CmdStateCache.activeTmnNumMap.containsKey(tmnCode)){
			return;
		}
		try{
			SysManagerService service = (SysManagerService) AppUtil.getBean("sysManagerService");
			if("0".equals(cmdState)){//�ɹ�
				CmdStateCache.activeTmnNumMap.remove(tmnCode);
				service.updateActiveState(tmnCode,"2");//�ɹ�
				log.error("����ɹ���"+tmnCode);
			}else{//ʧ��
				String[] numTime = CmdStateCache.activeTmnNumMap.get(tmnCode);
				if(numTime==null){
					return;
				}
				Integer nums = Integer.parseInt(numTime[0]);//����
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
					service.updateActiveState(tmnCode,"3");//ʧ��
				}
				log.error("����ʧ�ܣ�"+tmnCode);
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}
	
	
}
