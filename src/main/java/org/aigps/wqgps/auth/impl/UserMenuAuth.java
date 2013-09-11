
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
 * @Title���û��˵�Ȩ��ʵ����
 * @Description�����û��˵�����Ȩ�޵Ŀ���
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2011-6-20����05:16:43
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
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
	 * ���ص�ǰ�û����Կ��������в˵�����
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
	 * ����ָ���û�Id���Կ��������в˵�����
	 */
	@Override
	public List<Object> getResListByOwnerId(WqAuthObj obj, String userId) {
		List<String> menuIdList = AuthDAO.getResIdListByUserId(userId, obj.getId(), jdbcTemplate);
		return getMenuListFromCache(menuIdList);
	}

	//�ӻ�����ͨ���˵�ID��ȡ�˵�����
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
	 * ������Щ�û���Ȩ�޿���ָ���˵�Id
	 */
	@Override
	public List<Object> getOwnerListByResId(WqAuthObj obj, String menuId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		List<String> userIdList = AuthDAO.getUserIdListByResId(menuId, obj.getId(), companyId, jdbcTemplate);
		return publicDAO.findBy("id", userIdList, WqUserInfo.class);
	}

	/**
	 * �����û�Id��Ȩ�޿����Ĳ˵�Id����
	 */
	@Override
	public Boolean saveResListByOwnerId(WqAuthObj obj, List<String> menuIdList,String userId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByOwnerId(obj, userId);
		return AuthDAO.saveUserResListByUserId(menuIdList, obj.getId(), userId, companyId, jdbcTemplate);
	}
	
	/**
	 * ����˵�Id�������û�Ȩ��
	 */
	@Override
	public Boolean saveOwnerListByResId(WqAuthObj obj,List<String> userIdList,String menuId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByResId(obj, menuId);
		return AuthDAO.saveUserResListByResId(userIdList, obj.getId(), menuId, companyId, jdbcTemplate);
	}
	
	/**
	 * ɾ��ָ���û�Id���в˵�Ȩ��
	 */
	@Override
	public Boolean deleteResListByOwnerId(WqAuthObj obj, String userId) {
		return AuthDAO.deleteUserResByUserId(userId, obj.getId(), jdbcTemplate);
	}

	/**
	 * ɾ��ָ���˵�Id����Ȩ��
	 */
	@Override
	public Boolean deleteResListByResId(WqAuthObj obj,String menuId){
		return AuthDAO.deleteUserResByResId(menuId, obj.getId(), jdbcTemplate);
	}

	/**
	 * Ϊ����û���Ӷ���˵�
	 */
	@Override
	public Boolean addResList(WqAuthObj obj,List<String> userIdList,List<String> menuIdList){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		return AuthDAO.addUsersResList(obj.getId(), userIdList, menuIdList, companyId, jdbcTemplate);
	}
	
	/**
	 * Ϊ����û��Ƴ�����˵�
	 */
	@Override
	public Boolean deleteResList(WqAuthObj obj,List<String> userIdList,List<String> menuIdList){
		return AuthDAO.deleteUsersResList(obj.getId(), userIdList, menuIdList, jdbcTemplate);
	}
}

