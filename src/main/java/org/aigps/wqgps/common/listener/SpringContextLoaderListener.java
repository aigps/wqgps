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
