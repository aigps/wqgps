
package org.aigps.wqgps.auth;

import java.util.ArrayList;
import java.util.List;

import org.aigps.wqgps.auth.impl.IAuthObj;
import org.aigps.wqgps.auth.impl.UserDepAuth;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.WqAuthObj;
import org.aigps.wqgps.common.entity.WqDepInfo;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.AppUtil;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-6-20下午04:47:45
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
@SuppressWarnings("rawtypes")
public class AuthUtil {

	//获取部门下的所有员工
	public static List<WqStaffInfo> getStaffList(List<WqDepInfo> deps){
		List<WqStaffInfo> staffList = new ArrayList<WqStaffInfo>();
		for(WqDepInfo dep : deps) {
			List<WqStaffInfo> l = DataCache.depStaffMap.get(dep.getId());
			if(l != null){
				staffList.addAll(l);
			}
		}
		return staffList;
	}
	
	//获取部门下的所有员工
	public static List<WqStaffInfo> getStaffList(WqDepInfo dep){
		List<WqDepInfo> deps = new ArrayList<WqDepInfo>();
		deps.add(dep);
		deps.addAll(UserDepAuth.getSubDep(dep));
		return getStaffList(deps);
	}
	
	
	//获取当前用户/员工有权限看到的资源列表
	public static List getResList(String authType){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.getResList(obj);
	}

	//获取指定用户/员工有权限看到的资源列表
	public static List getResListByOwnerId(String authType,String ownerId){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.getResListByOwnerId(obj,ownerId);
	}
	
	//获取有权限看到资源ID的所有用户/员工
	public static List getOwnerListByResId(String authType,String resId){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.getOwnerListByResId(obj,resId);
	}
	
	//保存用户/员工ownerId有权限的资源列表
	public static Boolean saveResListByOwnerId(String authType,List<String> resList,String ownerId){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.saveResListByOwnerId(obj,resList,ownerId);
	}

	//保存资源resId被哪些用户/员工ownerList赋权限的资源列表
	public static Boolean saveOwnerListByResId(String authType,List<String> ownerList,String resId){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.saveOwnerListByResId(obj, ownerList, resId);
	}
	
	//将用户/员工ownerId的权限删除
	public static Boolean deleteResListByOwnerId(String authType,String ownerId){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.deleteResListByOwnerId(obj,ownerId);
	}

	//将资源resId的所有权限删除
	public static Boolean deleteResListByResId(String authType,String resId){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.deleteResListByResId(obj,resId);
	}
	
	//为多个用户/员工添加多个资源
	public static Boolean addResList(String authType,List<String> ownerList,List<String> resList){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.addResList(obj,ownerList,resList);
	}
	
	//为多个用户/员工移除多个资源
	public static Boolean deleteResList(String authType,List<String> ownerList,List<String> resList){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.deleteResList(obj,ownerList,resList);
	}
}

