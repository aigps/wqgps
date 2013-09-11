
package org.sunleads.auth.impl;

import java.util.List;

import org.sunleads.common.entity.WqAuthObj;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2011-6-20����04:19:38
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
 */
public interface IAuthObj {

	//��ȡ��ǰ�û�/Ա����Ȩ�޿�������Դ�б�
	public List<Object> getResList(WqAuthObj obj);

	//��ȡָ���û�/Ա����Ȩ�޿�������Դ�б�
	public List<Object> getResListByOwnerId(WqAuthObj obj,String ownerId);

	//��ȡ��Ȩ�޿�����ԴID�������û�/Ա��
	public List<Object> getOwnerListByResId(WqAuthObj obj,String resId);
	
	//�����û�/Ա��ownerId��Ȩ�޵���Դ�б�
	public Boolean saveResListByOwnerId(WqAuthObj obj,List<String> resList,String ownerId);

	//������ԴresId����Щ�û�/Ա��ownerList��Ȩ�޵���Դ�б�
	public Boolean saveOwnerListByResId(WqAuthObj obj,List<String> ownerList,String resId);
	
	//���û�/Ա��ownerId��Ȩ��ɾ��
	public Boolean deleteResListByOwnerId(WqAuthObj obj,String ownerId);

	//����ԴresId������Ȩ��ɾ��
	public Boolean deleteResListByResId(WqAuthObj obj,String resId);

	//Ϊ����û�/Ա����Ӷ����Դ
	public Boolean addResList(WqAuthObj obj,List<String> ownerList,List<String> resList);
	
	//Ϊ����û�/Ա���Ƴ������Դ
	public Boolean deleteResList(WqAuthObj obj,List<String> ownerList,List<String> resList);
	
}

