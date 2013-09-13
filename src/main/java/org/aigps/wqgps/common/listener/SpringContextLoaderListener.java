/**
 * 
 */
package org.aigps.wqgps.common.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.TimingUtil;
import org.aigps.wqgps.socket.GpsClient;
import org.aigps.wqgps.timing.CacheAuthObjMap;
import org.aigps.wqgps.timing.CacheClientRegionIdMap;
import org.aigps.wqgps.timing.CacheClientStaffIdMap;
import org.aigps.wqgps.timing.CacheDcChinaAreaMap;
import org.aigps.wqgps.timing.CacheDepMap;
import org.aigps.wqgps.timing.CacheLogTypeMap;
import org.aigps.wqgps.timing.CacheMapRegionMap;
import org.aigps.wqgps.timing.CacheMenuList;
import org.aigps.wqgps.timing.CachePlanLocateMap;
import org.aigps.wqgps.timing.CachePositionMap;
import org.aigps.wqgps.timing.CacheRetrospect;
import org.aigps.wqgps.timing.CacheStaffMap;
import org.aigps.wqgps.timing.CacheSystemParamsMap;
import org.aigps.wqgps.timing.CacheTradeList;
import org.aigps.wqgps.timing.CacheTradeRoleMap;
import org.aigps.wqgps.timing.CmdStateJob;
import org.aigps.wqgps.timing.RefreshCompanyInfoMap;
import org.aigps.wqgps.timing.RefreshPhoneStateMap;
import org.aigps.wqgps.timing.SmsSendsJob;
import org.aigps.wqgps.timing.SynStaffRegionMap;
import org.springframework.web.context.ContextLoaderListener;

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
