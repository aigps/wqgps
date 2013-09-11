
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
 * @Title��Ա���ͻ���ԴȨ��ʵ����
 * @Description����Ա���ͻ���Դ����Ȩ�޵Ŀ���
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
	 * ���ص�ǰԱ�����Կ��������пͻ���Դ����(ע��Ա��Ҳ������Ϊ�û���¼ϵͳ)
	 */
	@Override
	public List<Object> getResList(WqAuthObj obj) {
		WqUserInfo user = AppUtil.getUserInfo();
		List<String> clientIdList = AuthDAO.getResIdListByStaffId(user.getId(), obj.getId(), jdbcTemplate);
		return publicDAO.findBy("id", clientIdList, WqClientInfo.class);
	}

	/**
	 * ����ָ��Ա��Id���Կ��������пͻ���Դ����
	 */
	@Override
	public List<Object> getResListByOwnerId(WqAuthObj obj, String staffId) {
		List<String> clientIdList = AuthDAO.getResIdListByStaffId(staffId, obj.getId(), jdbcTemplate);
		return publicDAO.findBy("id", clientIdList, WqClientInfo.class);
	}

	/**
	 * ������ЩԱ����Ȩ�޿���ָ���ͻ���ԴId
	 */
	@Override
	public List<Object> getOwnerListByResId(WqAuthObj obj, String clientId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		List<String> staffIdList = AuthDAO.getStaffIdListByResId(clientId, obj.getId(), companyId, jdbcTemplate);
		return publicDAO.findBy("id", staffIdList, WqStaffInfo.class);
	}

	/**
	 * ����Ա��Id��Ȩ�޿����Ŀͻ���ԴId����
	 */
	@Override
	public Boolean saveResListByOwnerId(WqAuthObj obj, List<String> clientIdList,String staffId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByOwnerId(obj, staffId);
		return AuthDAO.saveStaffResListByStaffId(clientIdList, obj.getId(), staffId, companyId, jdbcTemplate);
	}

	/**
	 * ����ͻ���ԴId����ЩԱ�������ļ���
	 */
	@Override
	public Boolean saveOwnerListByResId(WqAuthObj obj,List<String> staffIdList,String clientId){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByResId(obj, clientId);
		return AuthDAO.saveStaffResListByResId(staffIdList, obj.getId(), clientId, companyId, jdbcTemplate);
	}
	
	/**
	 * ɾ��ָ��Ա��Id���пͻ���ԴȨ��
	 */
	@Override
	public Boolean deleteResListByOwnerId(WqAuthObj obj, String staffId) {
		return AuthDAO.deleteStaffResByStaffId(staffId, obj.getId(), jdbcTemplate);
	}

	/**
	 * ɾ��ָ���ͻ���ԴId����Ȩ��
	 */
	@Override
	public Boolean deleteResListByResId(WqAuthObj obj, String clientId) {
		return AuthDAO.deleteStaffResByResId(clientId, obj.getId(), jdbcTemplate);
	}

	/**
	 * Ϊ���Ա����Ӷ���ͻ���Դ
	 */
	@Override
	public Boolean addResList(WqAuthObj obj,List<String> staffIdList,List<String> resList){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		return AuthDAO.addStaffsResList(obj.getId(), staffIdList, resList, companyId, jdbcTemplate);
	}
	
	/**
	 * Ϊ���Ա���Ƴ�����ͻ���Դ
	 */
	@Override
	public Boolean deleteResList(WqAuthObj obj,List<String> staffIdList,List<String> resList){
		return AuthDAO.deleteStaffsResList(obj.getId(), staffIdList, resList, jdbcTemplate);
	}
}

