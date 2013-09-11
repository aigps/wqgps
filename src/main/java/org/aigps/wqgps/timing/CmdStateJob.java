package org.sunleads.timing;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.CmdStateCache;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.DateUtil;
import org.sunleads.common.util.TimingUtil;
import org.sunleads.module.sysmanager.service.SysManagerService;

/**
 * 扫描指令状态JOB
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
	 * 激活指令回传状态
	 * ******处理逻辑*******
	 * 1.循环所有发过激活指令的缓存
	 * 2.判断是否指发送过一次，因为如果发送过多次，表示该手机是能响应指令的
	 * 3.然后判断是否超过5分钟，5分钟没反应则表示彻底失败
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
					if("1".equals(nums)){//只发送过一次，且已经过了5分钟没反应
						String nowDateTime = DateUtil.getCurDate();//系统时间
						boolean lessThen5 = DateUtil.validateTimeByDiffValue(time, nowDateTime, 5);
						if(!lessThen5){
							CmdStateCache.activeTmnNumMap.remove(tmnCode);
							activeCmdStateJob.getSysManagerService().updateActiveState(tmnCode,"3");//激活失败
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
					if("1".equals(nums)){//只发送过一次，且已经过了5分钟没反应
						String nowDateTime = DateUtil.getCurDate();//系统时间
						boolean lessThen5 = DateUtil.validateTimeByDiffValue(time, nowDateTime, 5);
						if(!lessThen5){
							CmdStateCache.cancelActiveTmnNumMap.remove(tmnCode);
							activeCmdStateJob.getSysManagerService().updateActiveState(tmnCode,"6");//失活失败
						}
					}
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 扫描所有回传的指令状态
	 * ******处理逻辑******
	 * 1.设置超时5分钟的模型
	 * 2.删除掉所有超过60分钟的模型
	 */
	private static void scanCmdState(){
		try{
			CmdStateCache.updateOrRemoveTimeOutModel(5,60);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
}

