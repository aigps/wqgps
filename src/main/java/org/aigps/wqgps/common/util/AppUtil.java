package org.sunleads.common.util;

import java.io.File;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.sunleads.common.cache.SessionData;
import org.sunleads.common.entity.WqUserInfo;

public class AppUtil {
	
	// ����ע����ServletContext�е�Spring DI����
	private static ApplicationContext context;
	
	// ���������Ŀ¼
	private static String webAppPath;

	public static void startup(ServletContext context) {
		// ��DI����������context������
		AppUtil.setContext(WebApplicationContextUtils
				.getRequiredWebApplicationContext(context));

		// Ӧ�õ�������Ŀ¼
		String webAppPath = context.getRealPath("/");
		if (!webAppPath.endsWith(File.separator)) {
			AppUtil.setWebAppPath(webAppPath + File.separator);
		} else {
			AppUtil.setWebAppPath(webAppPath);
		}
	}

	/**
	 * ��ȡspring������
	 * 
	 * @return
	 */
	public static ApplicationContext getContext() {
		return context;
	}

	/**
	 * ����spring������
	 * 
	 * @param context
	 */
	public static void setContext(ApplicationContext context) {
		AppUtil.context = context;
	}

	/**
	 * ����beanName��ȡ����
	 * 
	 * @param beanName
	 * @return
	 */
	public static Object getBean(String beanName) {
		if (context == null || beanName == null || "".equals(beanName.trim())) {
			return null;
		}
		Object bean = context.getBean(beanName.trim());
		return bean;
	}

	/**
	 * ��ȡ��Ŀ��������Ŀ¼
	 * 
	 * @return
	 */
	public static String getWebAppPath() {
		return webAppPath;
	}

	public static void setWebAppPath(String webAppPath) {
		AppUtil.webAppPath = webAppPath;
	}

	public static SessionData getSessionData() {
		return (SessionData) AppUtil.getBean("sessionData");
	}

	public static WqUserInfo getUserInfo() {
		SessionData sd = (SessionData) AppUtil.getBean("sessionData");
		if (sd != null) {
			return sd.getUserInfo();
		}
		return null;
	}

}
