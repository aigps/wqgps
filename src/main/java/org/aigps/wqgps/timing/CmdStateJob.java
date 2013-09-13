package org.aigps.wqgps.timing;

import java.util.Map;

import org.aigps.wqgps.common.cache.CmdStateCache;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.common.util.TimingUtil;
import org.aigps.wqgps.module.sysmanager.service.SysManagerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ɨ��ָ��״̬JOB
 * @author admin
 *
 */
@Component
@Transactional
public class CmdStateJob{
	public final static Log log = LogFactory.getLog(CmdStateJob.class);
	
	private SysManagerService sysManagerService;
	
	@Autowired
	public void setSysManagerService(SysManagerService sysManagerService) {
		this.sysManagerService = sysManagerService;
	}
	public SysManagerService getSysManagerService() {
		return sysManagerService;
	}
	
	public static void startup(){
		final CmdStateJob activeCmdStateJob = (CmdStateJob) AppUtil.getBean("cmdStateJob");
		Thread t = new Thread(new Runnable(){
			public void run() { 
				while(true){
					try{
						scanCmdState();
						scanActiveCmdState(activeCmdStateJob);
						scanCanelActiveCmdState(activeCmdStateJob);
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}finally{
						try{
							Thread.sleep(TimingUtil.getForInt("refresh.cmdstate.interval"));
						} catch (Exception e) {
							log.error(e.getMessage(),e);
						}
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}
	
	
	/**
	 * ����ָ��ش�״̬
	 * ******�����߼�*******
	 * 1.ѭ�����з�������ָ��Ļ���
	 * 2.�ж��Ƿ�ָ���͹�һ�Σ���Ϊ������͹���Σ���ʾ���ֻ�������Ӧָ���
	 * 3.Ȼ���ж��Ƿ񳬹�5���ӣ�5����û��Ӧ���ʾ����ʧ��
	 * *********************
	 * @param activeCmdStateJob
	 */
	private static void scanActiveCmdState(final CmdStateJob activeCmdStateJob){
		try{
			for(Map.Entry<String, String[]> entry :CmdStateCache.activeTmnNumMap.entrySet()){
				String tmnCode = entry.getKey();
				String[] numTimeStrs = entry.getValue();
				if(numTimeStrs.length==2){
					String nums = numTimeStrs[0];
					String time = numTimeStrs[1];
					if("1".equals(nums)){//ֻ���͹�һ�Σ����Ѿ�����5����û��Ӧ
						String nowDateTime = DateUtil.getCurDate();//ϵͳʱ��
						boolean lessThen5 = DateUtil.validateTimeByDiffValue(time, nowDateTime, 5);
						if(!lessThen5){
							CmdStateCache.activeTmnNumMap.remove(tmnCode);
							activeCmdStateJob.getSysManagerService().updateActiveState(tmnCode,"3");//����ʧ��
						}
					}
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
	}
	
	private static void scanCanelActiveCmdState(final CmdStateJob activeCmdStateJob){
		try{
			for(Map.Entry<String, String[]> entry :CmdStateCache.cancelActiveTmnNumMap.entrySet()){
				String tmnCode = entry.getKey();
				String[] numTimeStrs = entry.getValue();
				if(numTimeStrs.length==2){
					String nums = numTimeStrs[0];
					String time = numTimeStrs[1];
					if("1".equals(nums)){//ֻ���͹�һ�Σ����Ѿ�����5����û��Ӧ
						String nowDateTime = DateUtil.getCurDate();//ϵͳʱ��
						boolean lessThen5 = DateUtil.validateTimeByDiffValue(time, nowDateTime, 5);
						if(!lessThen5){
							CmdStateCache.cancelActiveTmnNumMap.remove(tmnCode);
							activeCmdStateJob.getSysManagerService().updateActiveState(tmnCode,"6");//ʧ��ʧ��
						}
					}
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
	}
	
	/**
	 * ɨ�����лش���ָ��״̬
	 * ******�����߼�******
	 * 1.���ó�ʱ5���ӵ�ģ��
	 * 2.ɾ�������г���60���ӵ�ģ��
	 */
	private static void scanCmdState(){
		try{
			CmdStateCache.updateOrRemoveTimeOutModel(5,60);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
}

