package org.sunleads.socket;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * ����ö��
 * @author admin
 *
 */
public enum CmdTypeEnum {
	/**
	 * "LCSNow","��ȡ��ǰλ��"
	 */
	LCSNow("LCSNow","��ȡ��ǰλ��"),
	/**
	 * "SetInterval","���ö�λ���"
	 */
	SetInterval("SetInterval","���ö�λ���"),
	/**
	 * "ActiveLCS","������ҵ��λ����"
	 */
	ActiveLCS("ActiveLCS","������ҵ��λ����"),
	/**
	 * "CancelActiveLCS","������ҵ��λȡ������"
	 */
	CancelActiveLCS("CancelActiveLCS","������ҵ��λȡ������");

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
	
	 //����ö��
    public static CmdTypeEnum getEnumByValue(String value){   
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
