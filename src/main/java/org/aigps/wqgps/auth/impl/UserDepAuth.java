
package org.aigps.wqgps.auth.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aigps.wqgps.auth.dao.AuthDAO;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.WqAuthObj;
import org.aigps.wqgps.common.entity.WqDepInfo;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Title���û�����Ȩ��ʵ����
 * @Description�����û����Ž���Ȩ�޵Ŀ���
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
	 * ���ص�ǰ�û����Կ��������в��ż���
	 */
	public List<Object> getResList(WqAuthObj obj) {
		WqUserInfo user = AppUtil.getUserInfo();
		if(user.getIsAdmin()){
			return getCompanyDepList();
		}
		List<String> depIdList = AuthDAO.getResIdListByUserId(user.getId(), obj.getId(), jdbcTemplate);
		return getDepListFromCache(depIdList);
	}
	
	/**
	 * ����ָ���û�Id���Կ��������в��ż���
	 */
	public List<Object> getResListByOwnerId(WqAuthObj obj, String userId) {
		List<String> depIdList = AuthDAO.getResIdListByUserId(userId, obj.getId(), jdbcTemplate);
		return getDepListFromCache(depIdList);
	}
	
	//��ȡ��ǰ�û����ڹ�˾�����в���
	private List getCompanyDepList(){
		WqUserInfo user = AppUtil.getUserInfo();
		List<WqDepInfo> depList = DataCache.comDepMap.get(user.getCompanyId());
		if(depList == null){
			return new ArrayList();
		}
		return depList;
	}

	//�ӻ�����ͨ������ID��ȡ���Ŷ���
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

	//��ȡ�����µ������Ӳ���
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
	 * ������Щ�û���Ȩ�޿���ָ������Id
	 */
	public List<Object> getOwnerListByResId(WqAuthObj obj, String depId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		List<String> userIdList = AuthDAO.getUserIdListByResId(depId, obj.getId(), companyId, jdbcTemplate);
		return publicDAO.findBy("id", userIdList, WqUserInfo.class);
	}

	/**
	 * �����û�Id��Ȩ�޿����Ĳ���Id����
	 */
	public Boolean saveResListByOwnerId(WqAuthObj obj, List<String> depIdList,String userId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByOwnerId(obj, userId);
		return AuthDAO.saveUserResListByUserId(depIdList, obj.getId(), userId, companyId, jdbcTemplate);
	}

	/**
	 * ���沿��Id�������û�Ȩ��
	 */
	public Boolean saveOwnerListByResId(WqAuthObj obj,List<String> userIdList,String depId) {
		String companyId = AppUtil.getUserInfo().getCompanyId();
		
		this.deleteResListByResId(obj, depId);
		return AuthDAO.saveUserResListByResId(userIdList, obj.getId(), depId, companyId, jdbcTemplate);
	}
	
	/**
	 * ɾ��ָ���û�Id���в���Ȩ��
	 */
	public Boolean deleteResListByOwnerId(WqAuthObj obj, String userId) {
		return AuthDAO.deleteUserResByUserId(userId, obj.getId(), jdbcTemplate);
	}

	/**
	 * ɾ��ָ������Id����Ȩ��
	 */
	public Boolean deleteResListByResId(WqAuthObj obj,String depId){
		return AuthDAO.deleteUserResByResId(depId, obj.getId(), jdbcTemplate);
	}

	/**
	 * Ϊ����û���Ӷ������
	 */
	public Boolean addResList(WqAuthObj obj,List<String> userIdList,List<String> depIdList){
		String companyId = AppUtil.getUserInfo().getCompanyId();
		return AuthDAO.addUsersResList(obj.getId(), userIdList, depIdList, companyId, jdbcTemplate);
	}
	
	/**
	 * Ϊ����û��Ƴ��������
	 */
	public Boolean deleteResList(WqAuthObj obj,List<String> userIdList,List<String> depIdList){
		return AuthDAO.deleteUsersResList(obj.getId(), userIdList, depIdList, jdbcTemplate);
	}
}

