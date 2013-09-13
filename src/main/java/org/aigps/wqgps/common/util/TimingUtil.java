package org.aigps.wqgps.common.util;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 获取定时属性文件的值,用于系统的定时调度中.本身读取timing.properties文件也是定时
 */
public class TimingUtil implements Runnable{
	private static final Log log = LogFactory.getLog(TimingUtil.class);
	
	private static Properties props = new Properties();
	private static String propFile;
	private static int defaultInterval = 10000;
	private static Map<String,String> backupMap = new HashMap<String,String>();
	
	/**
	 * 第一次启动时初始化属性,同时启动线程不停更新更改
	 * @param context
	 */
	public static void startup(ServletContext context){
		TimingUtil.propFile = context.getRealPath("WEB-INF/cfg/timing.properties");
		try{
	        props.load(new FileInputStream(propFile));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Thread t = new Thread(new TimingUtil());
		t.setDaemon(true);
		t.start();
	}
	
	/**
	 * 获取具体属性值
	 * @param name 属性名称
	 * @return 属性值
	 */
	public static String get(String name){
		String value = props.getProperty(name);
		TimingUtil.backupMap.put(name, value);
		return value;
	}
	
	/**
	 * 获取具体属性值,将值转换成数值
	 * @param name 属性名称
	 * @return 数值属性值
	 */
	public static int getForInt(String name){
		try{
			String value = TimingUtil.get(name);
			TimingUtil.backupMap.put(name, value);
			return (value==null ? TimingUtil.defaultInterval : Integer.parseInt(value.trim()));
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		return TimingUtil.defaultInterval;
	}
	
	/**
	 * 判断属性值从上次获取至今是否有变化
	 * @param name 属性名称
	 * @return 是否变化
	 */
	public static boolean timingChange(String name){
		String oldValue = TimingUtil.backupMap.get(name);
		String value = TimingUtil.get(name);
		if(value==null)
			return false;
		return !value.equals(oldValue);
	}

	public void run() {
		while(true){
			try{
				Thread.sleep(TimingUtil.getForInt("self.interval"));
				
				Properties props = new Properties();
				props.load(new FileInputStream(propFile));
				
				TimingUtil.props = props;
			}catch(Exception e){
				log.error(e.getMessage(), e);
			}
		}
	}
}
