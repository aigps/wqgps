package org.aigps.wqgps.module.sms.smgp;

import java.io.File;

import org.aigps.wqgps.common.util.AppUtil;
import org.springframework.stereotype.Component;

import com.huawei.smproxy.util.Cfg;

/**
 * �ṩϵͳ���л�����Ϣ��
 */
@Component
public class Env {

  /** ���ö�д�ࡣ*/
  static Cfg config;

 /**��Ϣ������*/
  static Cfg msgConfig;

  
  static String configPath=AppUtil.getWebAppPath()+File.separator+"WEB-INF/cfg/sms-config.xml";//"WebContent/WEB-INF/cfg/sms-config.xml";//
  
  static String messagePath=AppUtil.getWebAppPath()+File.separator+"WEB-INF/cfg/sms-message.xml";//"WebContent/WEB-INF/cfg/sms-message.xml";//

  
  /** ���ö�д��(CMD)��*/
  static Cfg cmdConfig;

 /**��Ϣ������CMD*/
  static Cfg cmdMsgConfig;
  
  static String cmdConfigPath=AppUtil.getWebAppPath()+File.separator+"WEB-INF/cfg/sms-config-cmd.xml";//"WebContent/WEB-INF/cfg/sms-config.xml";//
  
  static String cmdMessagePath=AppUtil.getWebAppPath()+File.separator+"WEB-INF/cfg/sms-message-cmd.xml";//"WebContent/WEB-INF/cfg/sms-message.xml";//

  /**
   * ȡ�����ö�д�ࡣ
   */
  public static Cfg getConfig() {

    //���δ��ʼ������˵��ϵͳ�����ڰ�װ�����У������õĶ�ȡ
    if (config == null) {
      try {
        config = new Cfg(configPath);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return config;
  }
  
  /**
   * ȡ�����ö�д��CMD��
   */
  public static Cfg getCmdConfig() {

    //���δ��ʼ������˵��ϵͳ�����ڰ�װ�����У������õĶ�ȡ
    if (cmdConfig == null) {
      try {
    	 cmdConfig = new Cfg(cmdConfigPath);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return cmdConfig;
  }

  /**
   * ȡ����Ϣ�����ࡣ
   */
  public static Cfg getMsgConfig() {

    //���δ��ʼ������˵��ϵͳ�����ڰ�װ�����У������õĶ�ȡ
    if (msgConfig == null) {
      try {
        msgConfig = new Cfg(messagePath);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return msgConfig;
  }
  
  /**
   * ȡ����Ϣ������CMD��
   */
  public static Cfg getCmdMsgConfig() {

    //���δ��ʼ������˵��ϵͳ�����ڰ�װ�����У������õĶ�ȡ
    if (cmdMsgConfig == null) {
      try {
    	  cmdMsgConfig = new Cfg(cmdMessagePath);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return cmdMsgConfig;
  }
}
