package org.sunleads.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
/**
 * 
 * <pre>
 * Title:���ڹ�����
 * Description: ���ڹ�����
 * </pre>
 * @author xiongwenbo  xiongwenbo@gmail.com
 * @version 1.00.00
 * <pre>
 * �޸ļ�¼
 *    �޸ĺ�汾:     �޸��ˣ�  �޸�����:     �޸�����: 
 * </pre>
 */
@SuppressWarnings({"rawtypes","unchecked"})
public final class ListUtil {
	
	public static List<Map<String,Object>> convertBeanListToMapList(List beanList) throws Exception{
		if(beanList == null){
			return null;
		}
		int index = 1;
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		for(Object bean : beanList){
			Map<String,Object> map = BeanUtils.describe(bean);
			map.put("index", index++);
			returnList.add(map);
		}
		return returnList;
	}
}

