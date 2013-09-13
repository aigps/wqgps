package org.aigps.wqgps.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工状态类型枚举
 * @author admin
 *
 */
public enum StaffStateEnum {
	/**
	 * OFF_SITE("OFF_SITE","脱岗")
	 */
	OFF_SITE("OFF_SITE","脱岗"),
	/**
	 * ON_WORK_AREA("ON_WORK_AREA","正在工作区域")
	 */
	ON_WORK_AREA("ON_WORK_AREA","正在工作区域"),
	/**
	 * ON PASSAGE("ON PASSAGE","在途中")
	 */
	ON_PASSAGE("ON_PASSAGE","在途中");
	
	private final String value;
	private final String name;
	private StaffStateEnum(String value,String name){
		this.value = value;
		this.name = name;
	}
	
	private static final Map<String,StaffStateEnum> search=new HashMap<String,StaffStateEnum>();    
	static{   
		for(StaffStateEnum tempEnum:EnumSet.allOf(StaffStateEnum.class)){   
			search.put(tempEnum.getValue(), tempEnum);   
		}   
	}   
	
	 //查找枚举
    public static StaffStateEnum getEnumByValue(String value){   
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
