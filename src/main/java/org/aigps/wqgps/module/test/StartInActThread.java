
package org.sunleads.module.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sunleads.common.cache.CmdStateCache;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.util.AppUtil;
import org.sunleads.module.sysmanager.service.SysManagerService;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2011-9-13����02:00:51
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
 */
public class StartInActThread  extends Thread{
	private String staffId;
	private String mobileNumber;
	private String fixModels;
	private Map<String,Object> map;
	
	public StartInActThread(String staffId,String fixModels,String mobileNumber,Map<String,Object> map){
		this.staffId = staffId;
		this.fixModels = fixModels;
		this.mobileNumber = mobileNumber;
		this.map = map;
	}
	
	public void run(){
		List<String> ids = new ArrayList<String>();
		ids.add(staffId);
		Boolean hadInit = false;
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		
		while(MobileTestService.running){
			try{
				map.put("inActState", MobileTestService.getActStateName(staff.getActivateState()));
				String[] numTime = CmdStateCache.cancelActiveTmnNumMap.get(mobileNumber);
				if(numTime == null){//ʧ��п����ǵ�һ��ʧ�����ʧ��ɹ�
					if(hadInit == true){//ʧ��ɹ��ˣ�ִ�м���
						Thread t = new StartActThread(staffId,fixModels,mobileNumber);
						t.setDaemon(true);
						t.start();
						break;
					}
					hadInit = true;
					SysManagerService service = (SysManagerService)AppUtil.getBean("sysManagerService");
					service.cancelActivateStaffState(ids);
				}
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
		CmdStateCache.cancelActiveTmnNumMap.remove(mobileNumber);
	}
}