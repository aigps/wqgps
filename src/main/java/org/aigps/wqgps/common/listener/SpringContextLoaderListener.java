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
			
			//����Ӧ��������,Ӧ��·����
			AppUtil.startup(context);

			TimingUtil.startup(context);
			
			//ϵͳ��˾����
			RefreshCompanyInfoMap.startup();
			
			//ϵͳ��������
			CacheSystemParamsMap.startup();
			
			//��ҵ��ɫ����
			CacheTradeRoleMap.startup();
			
			//ϵͳ��־���ͻ���
			CacheLogTypeMap.startup();
			
			//ϵͳ�˵�����
			CacheMenuList.startup();
			
			//ϵͳ��ҵ����
			CacheTradeList.startup();
			
			//ϵͳȨ�޶��󻺴�
			CacheAuthObjMap.startup();

			//ϵͳ���пͻ���Ա���Ĺ�ϵ����
			CacheClientStaffIdMap.startup();
			
			//ϵͳ���пͻ���������Ķ��չ�ϵ
			CacheClientRegionIdMap.startup();
			
			//ϵͳ���Ż���
			CacheDepMap.startup();
			
			//ϵͳԱ������
			CacheStaffMap.startup();

			//ϵͳԱ���ֻ���״̬
			RefreshPhoneStateMap.startup();
			
			//���򻺴�
			CacheMapRegionMap.startup();
			
			//��ȡϵͳԱ����λ��Ϣ����
			CachePositionMap.startup();
			
			//�������򻺴�
			CacheDcChinaAreaMap.startup();
			
			//�ƻ���λ����
			CachePlanLocateMap.startup();
			
			//��������λ�����ӵ�socket
			GpsClient.getInstrance().startGpsClient();
			
			//�������ŷ���
			SmsSendsJob.startup();
			
			//�����ͻ�������֮��Ķ��չ�ϵ��ͬ�������ݻ�������
			SynStaffRegionMap.startup();
			
			//����ָ��״̬���JOB
			CmdStateJob.startup();
			
			//׷�����ݵĴ���
			CacheRetrospect.startup();
			
			//��ʼ�����ŵ�¼
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
