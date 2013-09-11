
package org.sunleads.auth.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.auth.dao.AuthDAO;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.WqAuthObj;
import org.sunleads.common.util.AppUtil;

/**
 * @Title：用户客户资源权限实现类
 * @Description：对用户客户资源进行权限的控制
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
public class UserClientAuth implements IAuthObj{

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 返回当前用户可以看到的所有客户资源集合(注：用户也可以做为用户登录系统)
	 */
	@Override
	public List<Object> getResList(WqAuthObj obj) {
//		WqUserInfo user = AppUtil.getUserInfo();
//		List<String> clientIdList = AuthDAO.getResIdListByStaffId(user.getId(), obj.getId(), jdbcTemplate);
//		return publicDAO.findBy("id", clientIdList, WqClientInfo.class);
		return null;
	}

	/**
	 * 返回指定用户Id可以看到的所有客户资源集合
	 */
	@Override
	public List<Object> getResListByOwnerId(WqAuthObj obj, String userId) {
//		List<String> clientIdList = AuthDAO.getResIdListByStaffId(userId, obj.getId(), jdbcTemplate);
//		return publicDAO.findBy("id", clientIdList, WqClientInfo.class);
		return null;
	}

	/**
	 * 返回哪些用户有权限看到指定客户资源Id
	 */
	@Override
	public List<Object> getOwnerListByResId(WqAuthObj obj, String clientId) {
//		String companyId = AppUtil.getUserInfo().getCompanyId();
//		List<String> userIdList = AuthDAO.getStaffIdListByResId(clientId, obj.getId(), companyId, jdbcTemplate);
//		return publicDAO.findBy("id", userIdList, WqStaffInfo.class);
		return null;
	}

	/**
	 * 保存用户Id有权限看到的客户资源Id集合
	 */
	@Override
	public Boolean saveResListByOwnerId(WqAuthObj obj, List<String> clientIdList,String userId) {
//		String companyId = AppUtil.getUserInfo().getCompanyId();
//		
//		this.deleteResListByOwnerId(obj, userId);
//		return AuthDAO.saveStaffResList(clientIdList, obj.getId(), userId, companyId, jdbcTemplate);
		return null;
	}

	@Override
	public Boolean saveOwnerListByResId(WqAuthObj obj,List<String> ownerList,String resId){
		return null;
	}
	
	/**
	 * 删除指定用户Id所有客户资源权限
	 */
	@Override
	public Boolean deleteResListByOwnerId(WqAuthObj obj, String userId) {
		return AuthDAO.deleteUserResByUserId(userId, obj.getId(), jdbcTemplate);
	}

	/**
	 * 将资源resId的所有权限删除
	 */
	@Override
	public Boolean deleteResListByResId(WqAuthObj obj,String clientId){
		return AuthDAO.deleteUserResByResId(clientId, obj.getId(), jdbcTemplate);
	}

	/**
	 * 为多个用户添加多个客户资源
	 */
	@Override
	public Boolean addResList(WqAuthObj obj,List<String> userIdList,List<String> clientIdList){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		return AuthDAO.addUsersResList(obj.getId(), userIdList, clientIdList, companyId, jdbcTemplate);
	}
	
	/**
	 * 为多个用户移除多个客户资源
	 */
	@Override
	public Boolean deleteResList(WqAuthObj obj,List<String> userIdList,List<String> clientIdList){
		return AuthDAO.deleteUsersResList(obj.getId(), userIdList, clientIdList, jdbcTemplate);
	}
}

