
package org.sunleads.module.webservice;

import javax.jws.WebService;

@WebService
public interface WqService {

	/**
	 * ���������޸Ĺ�˾
	 * 
	 * @param adminName			��˾����Ա�û���
	 * @param adminPassword��	��˾����Ա����
	 * @param companyName��		��˾����
	 * @param companyLinkMan	��˾��ϵ��
	 * @param companyPhone��		��˾�绰
	 * 
	 * @return true:�������޸ĳɹ�;����:�������ʾ��Ϣ
	 */
	public String createOrUpdateCompany(String adminName,String adminPassword,String companyName,String companyLinkMan,String companyPhone);
	
	/**
	 * ��������Ա��
	 * 
	 * @param adminName 		��˾����Ա�û���
	 * @param adminPassword��	��˾����Ա����
	 * @param staffName��		Ա������
	 * @param staffPhone��		Ա���ֻ���
	 * @param startWorkTime��	�ϰ�ʱ��(09:00)
	 * @param endWorkTime��		�°�ʱ��(18:00)
	 * @param locateInterval��	��λ���(��)
	 * @param locateModel��		��λ��ʽ(0:MSA;1:GOOGLE;2:GPS;3:MSA->GPS;4:GPS->GOOGLE;5:GPS->MSA;6:GPS->MSA->GOOGLE;)
	 *
	 * @return true:�����ɹ�;����:�������ʾ��Ϣ
	 */
	public String createStaff(String adminName,String adminPassword,String staffName,String staffPhone,String startWorkTime,String endWorkTime,String locateInterval,String locateModel);
	
	/**
	 * ɾ������Ա��
	 * @param adminName ��˾����Ա�û���
	 * @param adminPassword����˾����Ա����
	 * @param staffPhone ɾ����Ա���ֻ���
	 * @return true:�����ɹ�;����:�������ʾ��Ϣ
	 */
	public String deleteStaff(String adminName,String adminPassword,String staffPhone);

	/**
	 * ��ȡ���Ա���Ķ�λ��Ϣ
	 * 
	 * @param adminName ��˾����Ա�û���
	 * @param adminPassword ��˾����Ա����
	 * @param staffPhones Ա���ֻ��ţ����Ա���Զ��ŷֿ�  13438764746,13438764748,13438764749
	 * @param second ��ȡ���second��Ķ�λ��û������м�ʱ��λ��secondΪ0ʱֱ�ӽ��м�ʱ��λ
	 * @param responseHttpUrl ��λ���ͨ��HTTP URL����
	 * ���ظ�ʽ: responseHttpUrl?time=ʱ����(20120514160059)&lng=������(121.600939)&lat=γ����(30.03622949)&precision=������(19)&height=������(54)&direction=������(90)&speed=�ٶ���(0)&addr=��ַ��(�����к�����)
	 *
	 * @return true:��λ�����ɹ�;����:�������ʾ��Ϣ
	 */
	public String getStaffsLocation(String adminName,String adminPassword,String staffPhones,long second,String responseHttpUrl);

	/**
	 * ͨ����ַ����ѯ�����õ�ַλ�õĵ���Χ����Ϣ
	 * @param userName ��¼ϵͳ���û���
	 * @param password ��¼ϵͳ������
	 * @param addr ��ѯ�ĵ�ַ
	 * @param customer ��ѯ�Ŀͻ�����
	 * @return	��ѯ�쳣��-1
	 * 			�û���¼ʧ�ܣ�0
	 * 			��ѯ�����ݣ�1
	 * 			�����ݣ���������|������|������|��������|Χ������|��γ��|�뾶
	 * 			Χ������:1���Σ�2����Σ�3Բ��
	 * 			��γ��:lng1,lat1;lng2,lat2...
	 * 			�뾶:��Χ��������Բ����ֵ������Ϊ0
	 */
	public String getRegionByAddr(String userName, String password, String addr, String customer);
}

