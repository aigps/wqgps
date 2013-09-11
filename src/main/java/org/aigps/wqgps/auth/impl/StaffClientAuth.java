
package org.sunleads.auth.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.auth.dao.AuthDAO;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.WqAuthObj;
import org.sunleads.common.entity.WqClientInfo;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.util.AppUtil;

/**
 * @Title：员工客户资源权限实现类
 * @Description：对员工客户资源进行权限的控制
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
public class StaffClientAuth implements IAuthObj{

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
	 * 返回当前员工可以看到的所有客户资源集合(注：员工也可以做为用户登录系统)
	 */
	@Override
	public List<Object> getResList(WqAuthObj obj) {
		WqUserInfo user = AppUtil.getUserInfo();
		List<String> clientIdList = AuthDAO.getResIdListByStaffId(user.getId(), obj.getId(), jdbcTemplate);
		return publicDAO.findBy("id", clientIdList, WqClientInfo.class);
	}

	/**
	 * 返回指定员工Id可以看到的所有客户资源集合
	 */
	@Override
	public List<Object> getResListByOwnerId(WqAuthObj obj, String staffId) {
		List<String> clientIdList = AuthDAO.getResIdListByStaffId(staffId, obj.getId(), jdbcTemplate);
		return publicDAO.findBy("id", clientIdList, WqClientInfo.class);
	}

	/**
	 * 返回哪些员工有权限看到指定客户资源Id
	 */
	@Override
	public List<Object> getOwnerListByResId(WqAuthObj obj, String clientId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		List<String> staffIdList = AuthDAO.getStaffIdListByResId(clientId, obj.getId(), companyId, jdbcTemplate);
		return publicDAO.findBy("id", staffIdList, WqStaffInfo.class);
	}

	/**
	 * 保存员工Id有权限看到的客户资源Id集合
	 */
	@Override
	public Boolean saveResListByOwnerId(WqAuthObj obj, List<String> clientIdList,String staffId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByOwnerId(obj, staffId);
		return AuthDAO.saveStaffResListByStaffId(clientIdList, obj.getId(), staffId, companyId, jdbcTemplate);
	}

	/**
	 * 保存客户资源Id被哪些员工看到的集合
	 */
	@Override
	public Boolean saveOwnerListByResId(WqAuthObj obj,List<String> staffIdList,String clientId){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByResId(obj, clientId);
		return AuthDAO.saveStaffResListByResId(staffIdList, obj.getId(), clientId, companyId, jdbcTemplate);
	}
	
	/**
	 * 删除指定员工Id所有客户资源权限
	 */
	@Override
	public Boolean deleteResListByOwnerId(WqAuthObj obj, String staffId) {
		return AuthDAO.deleteStaffResByStaffId(staffId, obj.getId(), jdbcTemplate);
	}

	/**
	 * 删除指定客户资源Id所有权限
	 */
	@Override
	public Boolean deleteResListByResId(WqAuthObj obj, String clientId) {
		return AuthDAO.deleteStaffResByResId(clientId, obj.getId(), jdbcTemplate);
	}

	/**
	 * 为多个员工添加多个客户资源
	 */
	@Override
	public Boolean addResList(WqAuthObj obj,List<String> staffIdList,List<String> resList){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		return AuthDAO.addStaffsResList(obj.getId(), staffIdList, resList, companyId, jdbcTemplate);
	}
	
	/**
	 * 为多个员工移除多个客户资源
	 */
	@Override
	public Boolean deleteResList(WqAuthObj obj,List<String> staffIdList,List<String> resList){
		return AuthDAO.deleteStaffsResList(obj.getId(), staffIdList, resList, jdbcTemplate);
	}
}

