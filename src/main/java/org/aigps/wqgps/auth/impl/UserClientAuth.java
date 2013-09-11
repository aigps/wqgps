
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
 * @Title���û��ͻ���ԴȨ��ʵ����
 * @Description�����û��ͻ���Դ����Ȩ�޵Ŀ���
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
public class UserClientAuth implements IAuthObj{

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * ���ص�ǰ�û����Կ��������пͻ���Դ����(ע���û�Ҳ������Ϊ�û���¼ϵͳ)
	 */
	@Override
	public List<Object> getResList(WqAuthObj obj) {
//		WqUserInfo user = AppUtil.getUserInfo();
//		List<String> clientIdList = AuthDAO.getResIdListByStaffId(user.getId(), obj.getId(), jdbcTemplate);
//		return publicDAO.findBy("id", clientIdList, WqClientInfo.class);
		return null;
	}

	/**
	 * ����ָ���û�Id���Կ��������пͻ���Դ����
	 */
	@Override
	public List<Object> getResListByOwnerId(WqAuthObj obj, String userId) {
//		List<String> clientIdList = AuthDAO.getResIdListByStaffId(userId, obj.getId(), jdbcTemplate);
//		return publicDAO.findBy("id", clientIdList, WqClientInfo.class);
		return null;
	}

	/**
	 * ������Щ�û���Ȩ�޿���ָ���ͻ���ԴId
	 */
	@Override
	public List<Object> getOwnerListByResId(WqAuthObj obj, String clientId) {
//		String companyId = AppUtil.getUserInfo().getCompanyId();
//		List<String> userIdList = AuthDAO.getStaffIdListByResId(clientId, obj.getId(), companyId, jdbcTemplate);
//		return publicDAO.findBy("id", userIdList, WqStaffInfo.class);
		return null;
	}

	/**
	 * �����û�Id��Ȩ�޿����Ŀͻ���ԴId����
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
	 * ɾ��ָ���û�Id���пͻ���ԴȨ��
	 */
	@Override
	public Boolean deleteResListByOwnerId(WqAuthObj obj, String userId) {
		return AuthDAO.deleteUserResByUserId(userId, obj.getId(), jdbcTemplate);
	}

	/**
	 * ����ԴresId������Ȩ��ɾ��
	 */
	@Override
	public Boolean deleteResListByResId(WqAuthObj obj,String clientId){
		return AuthDAO.deleteUserResByResId(clientId, obj.getId(), jdbcTemplate);
	}

	/**
	 * Ϊ����û���Ӷ���ͻ���Դ
	 */
	@Override
	public Boolean addResList(WqAuthObj obj,List<String> userIdList,List<String> clientIdList){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		return AuthDAO.addUsersResList(obj.getId(), userIdList, clientIdList, companyId, jdbcTemplate);
	}
	
	/**
	 * Ϊ����û��Ƴ�����ͻ���Դ
	 */
	@Override
	public Boolean deleteResList(WqAuthObj obj,List<String> userIdList,List<String> clientIdList){
		return AuthDAO.deleteUsersResList(obj.getId(), userIdList, clientIdList, jdbcTemplate);
	}
}

