
package org.aigps.wqgps.auth.impl;

import java.util.List;

import org.aigps.wqgps.common.entity.WqAuthObj;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-6-20下午04:19:38
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
public interface IAuthObj {

	//获取当前用户/员工有权限看到的资源列表
	public List<Object> getResList(WqAuthObj obj);

	//获取指定用户/员工有权限看到的资源列表
	public List<Object> getResListByOwnerId(WqAuthObj obj,String ownerId);

	//获取有权限看到资源ID的所有用户/员工
	public List<Object> getOwnerListByResId(WqAuthObj obj,String resId);
	
	//保存用户/员工ownerId有权限的资源列表
	public Boolean saveResListByOwnerId(WqAuthObj obj,List<String> resList,String ownerId);

	//保存资源resId被哪些用户/员工ownerList赋权限的资源列表
	public Boolean saveOwnerListByResId(WqAuthObj obj,List<String> ownerList,String resId);
	
	//将用户/员工ownerId的权限删除
	public Boolean deleteResListByOwnerId(WqAuthObj obj,String ownerId);

	//将资源resId的所有权限删除
	public Boolean deleteResListByResId(WqAuthObj obj,String resId);

	//为多个用户/员工添加多个资源
	public Boolean addResList(WqAuthObj obj,List<String> ownerList,List<String> resList);
	
	//为多个用户/员工移除多个资源
	public Boolean deleteResList(WqAuthObj obj,List<String> ownerList,List<String> resList);
	
}

