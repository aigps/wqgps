
package org.aigps.wqgps.auth;

import java.util.ArrayList;
import java.util.List;

import org.aigps.wqgps.auth.impl.IAuthObj;
import org.aigps.wqgps.auth.impl.UserDepAuth;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.WqAuthObj;
import org.aigps.wqgps.common.entity.WqDepInfo;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.util.AppUtil;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2011-6-20����04:47:45
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
 */
@SuppressWarnings("rawtypes")
public class AuthUtil {

	//��ȡ�����µ�����Ա��
	public static List<WqStaffInfo> getStaffList(List<WqDepInfo> deps){
		List<WqStaffInfo> staffList = new ArrayList<WqStaffInfo>();
		for(WqDepInfo dep : deps) {
			List<WqStaffInfo> l = DataCache.depStaffMap.get(dep.getId());
			if(l != null){
				staffList.addAll(l);
			}
		}
		return staffList;
	}
	
	//��ȡ�����µ�����Ա��
	public static List<WqStaffInfo> getStaffList(WqDepInfo dep){
		List<WqDepInfo> deps = new ArrayList<WqDepInfo>();
		deps.add(dep);
		deps.addAll(UserDepAuth.getSubDep(dep));
		return getStaffList(deps);
	}
	
	
	//��ȡ��ǰ�û�/Ա����Ȩ�޿�������Դ�б�
	public static List getResList(String authType){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.getResList(obj);
	}

	//��ȡָ���û�/Ա����Ȩ�޿�������Դ�б�
	public static List getResListByOwnerId(String authType,String ownerId){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.getResListByOwnerId(obj,ownerId);
	}
	
	//��ȡ��Ȩ�޿�����ԴID�������û�/Ա��
	public static List getOwnerListByResId(String authType,String resId){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.getOwnerListByResId(obj,resId);
	}
	
	//�����û�/Ա��ownerId��Ȩ�޵���Դ�б�
	public static Boolean saveResListByOwnerId(String authType,List<String> resList,String ownerId){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.saveResListByOwnerId(obj,resList,ownerId);
	}

	//������ԴresId����Щ�û�/Ա��ownerList��Ȩ�޵���Դ�б�
	public static Boolean saveOwnerListByResId(String authType,List<String> ownerList,String resId){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.saveOwnerListByResId(obj, ownerList, resId);
	}
	
	//���û�/Ա��ownerId��Ȩ��ɾ��
	public static Boolean deleteResListByOwnerId(String authType,String ownerId){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.deleteResListByOwnerId(obj,ownerId);
	}

	//����ԴresId������Ȩ��ɾ��
	public static Boolean deleteResListByResId(String authType,String resId){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.deleteResListByResId(obj,resId);
	}
	
	//Ϊ����û�/Ա����Ӷ����Դ
	public static Boolean addResList(String authType,List<String> ownerList,List<String> resList){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.addResList(obj,ownerList,resList);
	}
	
	//Ϊ����û�/Ա���Ƴ������Դ
	public static Boolean deleteResList(String authType,List<String> ownerList,List<String> resList){
		WqAuthObj obj = DataCache.authObjMap.get(authType);
		IAuthObj auth = (IAuthObj) AppUtil.getBean(obj.getBean());
		return auth.deleteResList(obj,ownerList,resList);
	}
}

