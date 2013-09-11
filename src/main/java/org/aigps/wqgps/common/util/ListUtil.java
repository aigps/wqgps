package org.sunleads.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
/**
 * 
 * <pre>
 * Title:日期工具类
 * Description: 日期工具类
 * </pre>
 * @author xiongwenbo  xiongwenbo@gmail.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
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

