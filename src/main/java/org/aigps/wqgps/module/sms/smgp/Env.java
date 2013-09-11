package org.sunleads.module.sms.smgp;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;
import org.sunleads.common.util.AppUtil;

import com.huawei.smproxy.util.Cfg;
import com.huawei.smproxy.util.Resource;

/**
 * 提供系统运行环境信息。
 */
@Component
public class Env {

  /** 配置读写类。*/
  static Cfg config;

 /**消息配置类*/
  static Cfg msgConfig;

  
  static String configPath=AppUtil.getWebAppPath()+File.separator+"WEB-INF/cfg/sms-config.xml";//"WebContent/WEB-INF/cfg/sms-config.xml";//
  
  static String messagePath=AppUtil.getWebAppPath()+File.separator+"WEB-INF/cfg/sms-message.xml";//"WebContent/WEB-INF/cfg/sms-message.xml";//

  
  /** 配置读写类(CMD)。*/
  static Cfg cmdConfig;

 /**消息配置类CMD*/
  static Cfg cmdMsgConfig;
  
  static String cmdConfigPath=AppUtil.getWebAppPath()+File.separator+"WEB-INF/cfg/sms-config-cmd.xml";//"WebContent/WEB-INF/cfg/sms-config.xml";//
  
  static String cmdMessagePath=AppUtil.getWebAppPath()+File.separator+"WEB-INF/cfg/sms-message-cmd.xml";//"WebContent/WEB-INF/cfg/sms-message.xml";//

  /**
   * 取得配置读写类。
   */
  public static Cfg getConfig() {

    //如果未初始化，则说明系统正处在安装过程中，则配置的读取
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
   * 取得配置读写类CMD。
   */
  public static Cfg getCmdConfig() {

    //如果未初始化，则说明系统正处在安装过程中，则配置的读取
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
   * 取得消息配置类。
   */
  public static Cfg getMsgConfig() {

    //如果未初始化，则说明系统正处在安装过程中，则配置的读取
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
   * 取得消息配置类CMD。
   */
  public static Cfg getCmdMsgConfig() {

    //如果未初始化，则说明系统正处在安装过程中，则配置的读取
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
