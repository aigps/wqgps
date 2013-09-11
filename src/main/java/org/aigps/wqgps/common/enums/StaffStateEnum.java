package org.sunleads.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Ա��״̬����ö��
 * @author admin
 *
 */
public enum StaffStateEnum {
	/**
	 * OFF_SITE("OFF_SITE","�Ѹ�")
	 */
	OFF_SITE("OFF_SITE","�Ѹ�"),
	/**
	 * ON_WORK_AREA("ON_WORK_AREA","���ڹ�������")
	 */
	ON_WORK_AREA("ON_WORK_AREA","���ڹ�������"),
	/**
	 * ON PASSAGE("ON PASSAGE","��;��")
	 */
	ON_PASSAGE("ON_PASSAGE","��;��");
	
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
	
	 //����ö��
    public static StaffStateEnum getEnumByValue(String value){   
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
