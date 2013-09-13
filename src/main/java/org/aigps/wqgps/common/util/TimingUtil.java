package org.aigps.wqgps.common.util;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ��ȡ��ʱ�����ļ���ֵ,����ϵͳ�Ķ�ʱ������.�����ȡtiming.properties�ļ�Ҳ�Ƕ�ʱ
 */
public class TimingUtil implements Runnable{
	private static final Log log = LogFactory.getLog(TimingUtil.class);
	
	private static Properties props = new Properties();
	private static String propFile;
	private static int defaultInterval = 10000;
	private static Map<String,String> backupMap = new HashMap<String,String>();
	
	/**
	 * ��һ������ʱ��ʼ������,ͬʱ�����̲߳�ͣ���¸���
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
	 * ��ȡ��������ֵ
	 * @param name ��������
	 * @return ����ֵ
	 */
	public static String get(String name){
		String value = props.getProperty(name);
		TimingUtil.backupMap.put(name, value);
		return value;
	}
	
	/**
	 * ��ȡ��������ֵ,��ֵת������ֵ
	 * @param name ��������
	 * @return ��ֵ����ֵ
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
	 * �ж�����ֵ���ϴλ�ȡ�����Ƿ��б仯
	 * @param name ��������
	 * @return �Ƿ�仯
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
