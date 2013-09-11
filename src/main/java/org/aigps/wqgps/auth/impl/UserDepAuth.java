
package org.sunleads.auth.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.auth.dao.AuthDAO;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.WqAuthObj;
import org.sunleads.common.entity.WqDepInfo;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.util.AppUtil;

/**
 * @Title：用户部门权限实现类
 * @Description：对用户部门进行权限的控制
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-6-20下午05:16:43
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */

@Component
@Transactional
@SuppressWarnings({"rawtypes","unchecked"})
public class UserDepAuth implements IAuthObj{

	private PublicDAO publicDAO;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}

	/**
	 * 返回当前用户可以看到的所有部门集合
	 */
	@Override
	public List<Object> getResList(WqAuthObj obj) {
		WqUserInfo user = AppUtil.getUserInfo();
		if(user.getIsAdmin()){
			return getCompanyDepList();
		}
		List<String> depIdList = AuthDAO.getResIdListByUserId(user.getId(), obj.getId(), jdbcTemplate);
		return getDepListFromCache(depIdList);
	}
	
	/**
	 * 返回指定用户Id可以看到的所有部门集合
	 */
	@Override
	public List<Object> getResListByOwnerId(WqAuthObj obj, String userId) {
		List<String> depIdList = AuthDAO.getResIdListByUserId(userId, obj.getId(), jdbcTemplate);
		return getDepListFromCache(depIdList);
	}
	
	//获取当前用户所在公司的所有部门
	private List getCompanyDepList(){
		WqUserInfo user = AppUtil.getUserInfo();
		List<WqDepInfo> depList = DataCache.comDepMap.get(user.getCompanyId());
		if(depList == null){
			return new ArrayList();
		}
		return depList;
	}

	//从缓存中通过部门ID，取部门对象
	private List<Object> getDepListFromCache(List<String> depIdList){
		List<WqDepInfo> depList = getCompanyDepList();
		List<Object> list = new ArrayList<Object>();
		for(WqDepInfo dep : depList){
			if(depIdList.contains(dep.getId())){
				list.add(dep);
				list.addAll(getSubDep(dep));
			}
		}
		return list;
	}

	//获取部门下的所有子部门
	public static Set<WqDepInfo> getSubDep(WqDepInfo dep){
		Set<WqDepInfo> set = new HashSet<WqDepInfo>();
		List<WqDepInfo> deps = DataCache.comDepMap.get(dep.getCompanyId());
		if(deps!=null){
			for(WqDepInfo d : deps){
				if(dep.getId().equals(d.getParentId())){
					set.add(d);
					set.addAll(getSubDep(d));
				}
			}
		}
		return set;
	}
	
	/**
	 * 返回哪些用户有权限看到指定部门Id
	 */
	@Override
	public List<Object> getOwnerListByResId(WqAuthObj obj, String depId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		List<String> userIdList = AuthDAO.getUserIdListByResId(depId, obj.getId(), companyId, jdbcTemplate);
		return publicDAO.findBy("id", userIdList, WqUserInfo.class);
	}

	/**
	 * 保存用户Id有权限看到的部门Id集合
	 */
	@Override
	public Boolean saveResListByOwnerId(WqAuthObj obj, List<String> depIdList,String userId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByOwnerId(obj, userId);
		return AuthDAO.saveUserResListByUserId(depIdList, obj.getId(), userId, companyId, jdbcTemplate);
	}

	/**
	 * 保存部门Id的所有用户权限
	 */
	@Override
	public Boolean saveOwnerListByResId(WqAuthObj obj,List<String> userIdList,String depId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByResId(obj, depId);
		return AuthDAO.saveUserResListByResId(userIdList, obj.getId(), depId, companyId, jdbcTemplate);
	}
	
	/**
	 * 删除指定用户Id所有部门权限
	 */
	@Override
	public Boolean deleteResListByOwnerId(WqAuthObj obj, String userId) {
		return AuthDAO.deleteUserResByUserId(userId, obj.getId(), jdbcTemplate);
	}

	/**
	 * 删除指定部门Id所有权限
	 */
	@Override
	public Boolean deleteResListByResId(WqAuthObj obj,String depId){
		return AuthDAO.deleteUserResByResId(depId, obj.getId(), jdbcTemplate);
	}

	/**
	 * 为多个用户添加多个部门
	 */
	@Override
	public Boolean addResList(WqAuthObj obj,List<String> userIdList,List<String> depIdList){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		return AuthDAO.addUsersResList(obj.getId(), userIdList, depIdList, companyId, jdbcTemplate);
	}
	
	/**
	 * 为多个用户移除多个部门
	 */
	@Override
	public Boolean deleteResList(WqAuthObj obj,List<String> userIdList,List<String> depIdList){
		return AuthDAO.deleteUsersResList(obj.getId(), userIdList, depIdList, jdbcTemplate);
	}
}

