
package org.sunleads.auth.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.auth.dao.AuthDAO;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.cache.SessionData;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.WqAuthObj;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqMenuInfo;
import org.sunleads.common.entity.WqTradeRole;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.util.AppUtil;

/**
 * @Title：用户菜单权限实现类
 * @Description：对用户菜单进行权限的控制
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
@SuppressWarnings("unchecked")
public class UserMenuAuth implements IAuthObj{

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
	 * 返回当前用户可以看到的所有菜单集合
	 */
	@Override
	public List<Object> getResList(WqAuthObj obj) {
		SessionData sd = AppUtil.getSessionData();
		WqUserInfo user = sd.getUserInfo();
		if(user.getIsAdmin()){
			if(sd.getCompany()!=null){
				WqTradeRole trade = DataCache.tradeRoleMap.get(sd.getCompany().getTradeRoleId());
				if(trade != null && StringUtils.isNotBlank(trade.getMenuIds())){
					String[] menuIds = trade.getMenuIds().split(",");
					List<Object> menus = new ArrayList<Object>();
					for(String id:menuIds){
						for(WqMenuInfo m : (List<WqMenuInfo>)DataCache.menuList){
							if(m.getId().equals(id)){
								menus.add(m);
								break;
							}
						}
					}
					if(!menus.isEmpty()){
						return menus;
					}
				}
			}
			return DataCache.menuList;
		}
		List<String> menuIdList = AuthDAO.getResIdListByUserId(user.getId(), obj.getId(), jdbcTemplate);
		return getMenuListFromCache(menuIdList);
	}
	
	/**
	 * 返回指定用户Id可以看到的所有菜单集合
	 */
	@Override
	public List<Object> getResListByOwnerId(WqAuthObj obj, String userId) {
		List<String> menuIdList = AuthDAO.getResIdListByUserId(userId, obj.getId(), jdbcTemplate);
		return getMenuListFromCache(menuIdList);
	}

	//从缓存中通过菜单ID，取菜单对象
	private List<Object> getMenuListFromCache(List<String> menuIdList){
		List<Object> menuList = new ArrayList<Object>();
		for(WqMenuInfo menu : (List<WqMenuInfo>)DataCache.menuList){
			if(menuIdList.contains(menu.getId())){
				menuList.add(menu);
			}
		}
		return menuList;
	}
	
	/**
	 * 返回哪些用户有权限看到指定菜单Id
	 */
	@Override
	public List<Object> getOwnerListByResId(WqAuthObj obj, String menuId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		List<String> userIdList = AuthDAO.getUserIdListByResId(menuId, obj.getId(), companyId, jdbcTemplate);
		return publicDAO.findBy("id", userIdList, WqUserInfo.class);
	}

	/**
	 * 保存用户Id有权限看到的菜单Id集合
	 */
	@Override
	public Boolean saveResListByOwnerId(WqAuthObj obj, List<String> menuIdList,String userId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByOwnerId(obj, userId);
		return AuthDAO.saveUserResListByUserId(menuIdList, obj.getId(), userId, companyId, jdbcTemplate);
	}
	
	/**
	 * 保存菜单Id的所有用户权限
	 */
	@Override
	public Boolean saveOwnerListByResId(WqAuthObj obj,List<String> userIdList,String menuId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByResId(obj, menuId);
		return AuthDAO.saveUserResListByResId(userIdList, obj.getId(), menuId, companyId, jdbcTemplate);
	}
	
	/**
	 * 删除指定用户Id所有菜单权限
	 */
	@Override
	public Boolean deleteResListByOwnerId(WqAuthObj obj, String userId) {
		return AuthDAO.deleteUserResByUserId(userId, obj.getId(), jdbcTemplate);
	}

	/**
	 * 删除指定菜单Id所有权限
	 */
	@Override
	public Boolean deleteResListByResId(WqAuthObj obj,String menuId){
		return AuthDAO.deleteUserResByResId(menuId, obj.getId(), jdbcTemplate);
	}

	/**
	 * 为多个用户添加多个菜单
	 */
	@Override
	public Boolean addResList(WqAuthObj obj,List<String> userIdList,List<String> menuIdList){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		return AuthDAO.addUsersResList(obj.getId(), userIdList, menuIdList, companyId, jdbcTemplate);
	}
	
	/**
	 * 为多个用户移除多个菜单
	 */
	@Override
	public Boolean deleteResList(WqAuthObj obj,List<String> userIdList,List<String> menuIdList){
		return AuthDAO.deleteUsersResList(obj.getId(), userIdList, menuIdList, jdbcTemplate);
	}
}

