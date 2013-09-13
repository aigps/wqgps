package org.aigps.wqgps.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 定位类型枚举
 * @author admin
 *
 */
public enum LocateTypeEnum {
	/**
	 * GET_POS("01","随时定位")
	 */
	GET_POS("01","随时定位"),
	/**
	 * ACTIVE_LCS_POS("02","激活定位")
	 */
	ACTIVE_LCS_POS("02","激活定位"),
	/**
	 * TIMING_INTERVAL("03","周期定位")
	 */
	TIMING_INTERVAL("03","周期定位");
	
	private final String value;
	private final String name;
	private LocateTypeEnum(String value,String name){
		this.value = value;
		this.name = name;
	}
	
	private static final Map<String,LocateTypeEnum> search=new HashMap<String,LocateTypeEnum>();    
	static{   
		for(LocateTypeEnum tempEnum:EnumSet.allOf(LocateTypeEnum.class)){   
			search.put(tempEnum.getValue(), tempEnum);   
		}   
	}   
	
	 //查找枚举
    public static LocateTypeEnum getEnumByValue(String value){   
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
