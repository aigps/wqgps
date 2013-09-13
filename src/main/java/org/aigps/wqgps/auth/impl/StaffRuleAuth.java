
package org.aigps.wqgps.auth.impl;

import java.util.List;

import org.aigps.wqgps.auth.dao.AuthDAO;
import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.WqAuthObj;
import org.aigps.wqgps.common.entity.WqRule;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Title：员工围栏规则权限实现类
 * @Description：对员工围栏规则进行权限的控制
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
public class StaffRuleAuth implements IAuthObj{

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
	 * 返回当前员工可以看到的所有围栏规则集合(注：员工也可以做为用户登录系统)
	 */
	public List<Object> getResList(WqAuthObj obj) {
		WqUserInfo user = AppUtil.getUserInfo();
		List<String> ruleIdList = AuthDAO.getResIdListByStaffId(user.getId(), obj.getId(), jdbcTemplate);
		return publicDAO.findBy("id", ruleIdList, WqRule.class);
	}

	/**
	 * 返回指定员工Id可以看到的所有围栏规则集合
	 */
	public List<Object> getResListByOwnerId(WqAuthObj obj, String staffId) {
		List<String> ruleIdList = AuthDAO.getResIdListByStaffId(staffId, obj.getId(), jdbcTemplate);
		return publicDAO.findBy("id", ruleIdList, WqRule.class);
	}

	/**
	 * 返回哪些员工有权限看到指定围栏规则Id
	 */
	public List<Object> getOwnerListByResId(WqAuthObj obj, String ruleId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		List<String> staffIdList = AuthDAO.getStaffIdListByResId(ruleId, obj.getId(), companyId, jdbcTemplate);
		return publicDAO.findBy("id", staffIdList, WqStaffInfo.class);
	}

	/**
	 * 保存员工Id有权限看到的围栏规则Id集合
	 */
	public Boolean saveResListByOwnerId(WqAuthObj obj, List<String> ruleIdList,String staffId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByOwnerId(obj, staffId);
		return AuthDAO.saveStaffResListByStaffId(ruleIdList, obj.getId(), staffId, companyId, jdbcTemplate);
	}

	/**
	 * 保存客户资源Id被哪些员工看到的集合
	 */
	public Boolean saveOwnerListByResId(WqAuthObj obj,List<String> staffIdList,String ruleId){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByResId(obj, ruleId);
		return AuthDAO.saveStaffResListByResId(staffIdList, obj.getId(), ruleId, companyId, jdbcTemplate);
	}
	
	/**
	 * 删除指定员工Id所有围栏规则权限
	 */
	public Boolean deleteResListByOwnerId(WqAuthObj obj, String staffId) {
		return AuthDAO.deleteStaffResByStaffId(staffId, obj.getId(), jdbcTemplate);
	}

	/**
	 * 删除指定围栏规则Id的所有权限
	 */
	public Boolean deleteResListByResId(WqAuthObj obj, String ruleId) {
		return AuthDAO.deleteStaffResByResId(ruleId, obj.getId(), jdbcTemplate);
	}
	
	/**
	 * 为多个员工添加多个围栏规则
	 */
	public Boolean addResList(WqAuthObj obj,List<String> staffIdList,List<String> ruleIdList){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		return AuthDAO.addStaffsResList(obj.getId(), staffIdList, ruleIdList, companyId, jdbcTemplate);
	}
	
	/**
	 * 为多个员工移除多个围栏规则
	 */
	public Boolean deleteResList(WqAuthObj obj,List<String> staffIdList,List<String> ruleIdList){
		return AuthDAO.deleteStaffsResList(obj.getId(), staffIdList, ruleIdList, jdbcTemplate);
	}
}

