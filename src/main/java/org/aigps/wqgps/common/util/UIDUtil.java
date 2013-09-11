package org.sunleads.common.util;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.UUIDHexGenerator;

public class UIDUtil extends UUIDHexGenerator{

	public static String getId(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	

	public Serializable generate(SessionImplementor session, Object object) {
		return new StringBuilder(16).append(formatShort(getAppId())).append(format(getHiTime())).append(
				format(getLoTime())).append(formatShort(getCount())).toString();
	}

	/**
	 * 可重载子类实现从System Properties, Spring ApplicationContext等地方获得值.
	 */
	protected short getAppId() {
		return 0;
	}

	/**
	 * 格式化最大值为255的数值成长度为2的字符串.
	 */
	protected String formatShort(short value) {
		String formatted = Integer.toHexString(value);
		int len = formatted.length();
		if (len < 2) {
			formatted = "0" + formatted;
		}else if(len > 2){
			formatted = formatted.substring(len-2,len);
		}
		return formatted;
	}
	
}
