package org.aigps.wqgps.common.util;

import java.io.File;

import javax.servlet.ServletContext;

import org.aigps.wqgps.common.cache.SessionData;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class AppUtil {
	
	// 缓存注册在ServletContext中的Spring DI容器
	private static ApplicationContext context;
	
	// 部署的物理目录
	private static String webAppPath;

	public static void startup(ServletContext context) {
		// 将DI容器缓存在context变量中
		AppUtil.setContext(WebApplicationContextUtils
				.getRequiredWebApplicationContext(context));

		// 应用的物理部署目录
		String webAppPath = context.getRealPath("/");
		if (!webAppPath.endsWith(File.separator)) {
			AppUtil.setWebAppPath(webAppPath + File.separator);
		} else {
			AppUtil.setWebAppPath(webAppPath);
		}
	}

	/**
	 * 获取spring上下文
	 * 
	 * @return
	 */
	public static ApplicationContext getContext() {
		return context;
	}

	/**
	 * 设置spring上下文
	 * 
	 * @param context
	 */
	public static void setContext(ApplicationContext context) {
		AppUtil.context = context;
	}

	/**
	 * 根据beanName获取对象
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
	 * 获取项目部署物理目录
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
