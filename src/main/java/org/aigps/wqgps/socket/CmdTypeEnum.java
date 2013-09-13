package org.aigps.wqgps.socket;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 命令枚举
 * @author admin
 *
 */
public enum CmdTypeEnum {
	/**
	 * "LCSNow","获取当前位置"
	 */
	LCSNow("LCSNow","获取当前位置"),
	/**
	 * "SetInterval","设置定位间隔"
	 */
	SetInterval("SetInterval","设置定位间隔"),
	/**
	 * "ActiveLCS","电信行业定位激活"
	 */
	ActiveLCS("ActiveLCS","电信行业定位激活"),
	/**
	 * "CancelActiveLCS","电信行业定位取消激活"
	 */
	CancelActiveLCS("CancelActiveLCS","电信行业定位取消激活");

	private final String value;
	private final String name;
	private CmdTypeEnum(String value,String name){
		this.value = value;
		this.name = name;
	}
	
	private static final Map<String,CmdTypeEnum> search=new HashMap<String,CmdTypeEnum>();    
	static{   
		for(CmdTypeEnum tempEnum:EnumSet.allOf(CmdTypeEnum.class)){   
			search.put(tempEnum.getValue(), tempEnum);   
		}   
	}   
	
	 //查找枚举
    public static CmdTypeEnum getEnumByValue(String value){   
        return search.get(value);   
    }   
	//根据值获取枚举中的名称
    public static String getEnumNameByValue(String value){   
        return getEnumByValue(value).getName();   
    }   
    
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}
}
