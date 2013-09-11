
package org.sunleads.module.test;

import java.util.ArrayList;
import java.util.HashMap;
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
 * Create Date��  2011-9-13����02:00:22
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
 */
public class StartActThread  extends Thread{
	private String staffId;
	private String fixModels;
	private String mobileNumber;
	private static int index = 0;
	public static boolean finish = false;
	
	public StartActThread(String staffId,String fixModels,String mobileNumber){
		this.staffId = staffId;
		this.fixModels = fixModels;
		this.mobileNumber = mobileNumber;
		finish = false;
	}
	
	public void run(){
		WqStaffInfo staff = DataCache.staffMap.get(staffId);
		List<String> ids = new ArrayList<String>();
		ids.add(staffId);
		Boolean hadInit = false;
		Map<String,Object> map = null;
		
		while(MobileTestService.running){
			try{
				String[] numTime = CmdStateCache.activeTmnNumMap.get(mobileNumber);
				if(numTime == null){//����п����ǵ�һ�Σ��򼤻�ɹ�
					if(hadInit == true){//����ɹ��ˣ�ִ��ȥ����
						map.put("actState", MobileTestService.getActStateName(staff.getActivateState()));
						Thread t = new StartInActThread(staffId,fixModels,mobileNumber,map);
						t.setDaemon(true);
						t.start();
						break;
					}else{//��һ�μ���
						hadInit = true;
						SysManagerService service = (SysManagerService)AppUtil.getBean("sysManagerService");
						if(fixModels.length()<=index){
							finish = true;
							index = 0;
							break;
						}
						String fixModel = fixModels.charAt(index++)+"";
						service.activateStaffState(ids,fixModel);

						map = new HashMap<String,Object>();
						map.put("fixModel", MobileTestService.getLocType(fixModel));
						map.put("actState", MobileTestService.getActStateName(staff.getActivateState()));
						MobileTestService.actList.add(map);
					}
				}
				map.put("actState", MobileTestService.getActStateName(staff.getActivateState()));
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
		CmdStateCache.activeTmnNumMap.remove(mobileNumber);
	}

}

