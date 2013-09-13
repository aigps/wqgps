package org.aigps.wqgps.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * ��λ����ö��
 * @author admin
 *
 */
public enum LocateTypeEnum {
	/**
	 * GET_POS("01","��ʱ��λ")
	 */
	GET_POS("01","��ʱ��λ"),
	/**
	 * ACTIVE_LCS_POS("02","���λ")
	 */
	ACTIVE_LCS_POS("02","���λ"),
	/**
	 * TIMING_INTERVAL("03","���ڶ�λ")
	 */
	TIMING_INTERVAL("03","���ڶ�λ");
	
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
	
	 //����ö��
    public static LocateTypeEnum getEnumByValue(String value){   
        return search.get(value);   
    }   
	//����ֵ��ȡö���е�����
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
