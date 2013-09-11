/**
 * 
 */
package org.sunleads.common.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.TimingUtil;
import org.sunleads.socket.GpsClient;
import org.sunleads.timing.CacheAuthObjMap;
import org.sunleads.timing.CacheClientRegionIdMap;
import org.sunleads.timing.CacheClientStaffIdMap;
import org.sunleads.timing.CacheDcChinaAreaMap;
import org.sunleads.timing.CacheDepMap;
import org.sunleads.timing.CacheLogTypeMap;
import org.sunleads.timing.CacheMapRegionMap;
import org.sunleads.timing.CacheMenuList;
import org.sunleads.timing.CachePlanLocateMap;
import org.sunleads.timing.CachePositionMap;
import org.sunleads.timing.CacheRetrospect;
import org.sunleads.timing.CacheStaffMap;
import org.sunleads.timing.CacheSystemParamsMap;
import org.sunleads.timing.CacheTradeList;
import org.sunleads.timing.CacheTradeRoleMap;
import org.sunleads.timing.CmdStateJob;
import org.sunleads.timing.RefreshCompanyInfoMap;
import org.sunleads.timing.RefreshPhoneStateMap;
import org.sunleads.timing.SmsSendsJob;
import org.sunleads.timing.SynStaffRegionMap;

/**
 * @author Administrator
 *
 */
public class SpringContextLoaderListener extends ContextLoaderListener {
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		
		ServletContext context = event.getServletContext();
		
		try{
			
			//设置应用上下文,应用路径等
			AppUtil.startup(context);

			TimingUtil.startup(context);
			
			//系统公司缓存
			RefreshCompanyInfoMap.startup();
			
			//系统参数缓存
			CacheSystemParamsMap.startup();
			
			//行业角色缓存
			CacheTradeRoleMap.startup();
			
			//系统日志类型缓存
			CacheLogTypeMap.startup();
			
			//系统菜单缓存
			CacheMenuList.startup();
			
			//系统行业缓存
			CacheTradeList.startup();
			
			//系统权限对象缓存
			CacheAuthObjMap.startup();

			//系统所有客户和员工的关系缓存
			CacheClientStaffIdMap.startup();
			
			//系统所有客户及其区域的对照关系
			CacheClientRegionIdMap.startup();
			
			//系统部门缓存
			CacheDepMap.startup();
			
			//系统员工缓存
			CacheStaffMap.startup();

			//系统员工手机的状态
			RefreshPhoneStateMap.startup();
			
			//区域缓存
			CacheMapRegionMap.startup();
			
			//获取系统员工定位信息缓存
			CachePositionMap.startup();
			
			//行政区域缓存
			CacheDcChinaAreaMap.startup();
			
			//计划定位任务
			CachePlanLocateMap.startup();
			
			//启动和上位机连接的socket
			GpsClient.getInstrance().startGpsClient();
			
			//启动短信服务
			SmsSendsJob.startup();
			
			//启动客户和区域之间的对照关系，同步到数据缓存中心
			SynStaffRegionMap.startup();
			
			//启动指令状态监控JOB
			CmdStateJob.startup();
			
			//追溯数据的处理
			CacheRetrospect.startup();
			
			//初始化短信登录
			String smsSender = (String)AppUtil.getBean("smsSender");
			if("dxSmsSender".equals(smsSender)){
				AppUtil.getBean("smsCmdHandle");
				AppUtil.getBean(smsSender);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
