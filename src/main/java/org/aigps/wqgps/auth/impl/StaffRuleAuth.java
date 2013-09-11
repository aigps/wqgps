
package org.sunleads.auth.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.auth.dao.AuthDAO;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.WqAuthObj;
import org.sunleads.common.entity.WqRule;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.util.AppUtil;

/**
 * @Title��Ա��Χ������Ȩ��ʵ����
 * @Description����Ա��Χ���������Ȩ�޵Ŀ���
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
	 * ���ص�ǰԱ�����Կ���������Χ�����򼯺�(ע��Ա��Ҳ������Ϊ�û���¼ϵͳ)
	 */
	@Override
	public List<Object> getResList(WqAuthObj obj) {
		WqUserInfo user = AppUtil.getUserInfo();
		List<String> ruleIdList = AuthDAO.getResIdListByStaffId(user.getId(), obj.getId(), jdbcTemplate);
		return publicDAO.findBy("id", ruleIdList, WqRule.class);
	}

	/**
	 * ����ָ��Ա��Id���Կ���������Χ�����򼯺�
	 */
	@Override
	public List<Object> getResListByOwnerId(WqAuthObj obj, String staffId) {
		List<String> ruleIdList = AuthDAO.getResIdListByStaffId(staffId, obj.getId(), jdbcTemplate);
		return publicDAO.findBy("id", ruleIdList, WqRule.class);
	}

	/**
	 * ������ЩԱ����Ȩ�޿���ָ��Χ������Id
	 */
	@Override
	public List<Object> getOwnerListByResId(WqAuthObj obj, String ruleId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		List<String> staffIdList = AuthDAO.getStaffIdListByResId(ruleId, obj.getId(), companyId, jdbcTemplate);
		return publicDAO.findBy("id", staffIdList, WqStaffInfo.class);
	}

	/**
	 * ����Ա��Id��Ȩ�޿�����Χ������Id����
	 */
	@Override
	public Boolean saveResListByOwnerId(WqAuthObj obj, List<String> ruleIdList,String staffId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByOwnerId(obj, staffId);
		return AuthDAO.saveStaffResListByStaffId(ruleIdList, obj.getId(), staffId, companyId, jdbcTemplate);
	}

	/**
	 * ����ͻ���ԴId����ЩԱ�������ļ���
	 */
	@Override
	public Boolean saveOwnerListByResId(WqAuthObj obj,List<String> staffIdList,String ruleId){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByResId(obj, ruleId);
		return AuthDAO.saveStaffResListByResId(staffIdList, obj.getId(), ruleId, companyId, jdbcTemplate);
	}
	
	/**
	 * ɾ��ָ��Ա��Id����Χ������Ȩ��
	 */
	@Override
	public Boolean deleteResListByOwnerId(WqAuthObj obj, String staffId) {
		return AuthDAO.deleteStaffResByStaffId(staffId, obj.getId(), jdbcTemplate);
	}

	/**
	 * ɾ��ָ��Χ������Id������Ȩ��
	 */
	@Override
	public Boolean deleteResListByResId(WqAuthObj obj, String ruleId) {
		return AuthDAO.deleteStaffResByResId(ruleId, obj.getId(), jdbcTemplate);
	}
	
	/**
	 * Ϊ���Ա����Ӷ��Χ������
	 */
	@Override
	public Boolean addResList(WqAuthObj obj,List<String> staffIdList,List<String> ruleIdList){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		return AuthDAO.addStaffsResList(obj.getId(), staffIdList, ruleIdList, companyId, jdbcTemplate);
	}
	
	/**
	 * Ϊ���Ա���Ƴ����Χ������
	 */
	@Override
	public Boolean deleteResList(WqAuthObj obj,List<String> staffIdList,List<String> ruleIdList){
		return AuthDAO.deleteStaffsResList(obj.getId(), staffIdList, ruleIdList, jdbcTemplate);
	}
}

