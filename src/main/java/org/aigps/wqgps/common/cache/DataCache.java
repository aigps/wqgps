package org.sunleads.common.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.sunleads.common.entity.DcChinaArea;
import org.sunleads.common.entity.DcGpsReal;
import org.sunleads.common.entity.SmsModel;
import org.sunleads.common.entity.WqAuthObj;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqDepInfo;
import org.sunleads.common.entity.WqLogType;
import org.sunleads.common.entity.WqMapRegion;
import org.sunleads.common.entity.WqPlanLocateDetail;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.entity.WqTradeInfo;
import org.sunleads.common.entity.WqTradeRole;

/**
 * ���ݻ���
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class DataCache {

	//ϵͳ����������Ϣ,<key,value>
	public static Map<String, String> systemParamsMap = new HashMap<String, String>();

	//ϵͳ���й�˾�Ķ���,<��˾ID,��˾>
	public static Map<String, WqCompanyInfo> companyInfoMap = new HashMap<String,WqCompanyInfo>();
	
	//ϵͳ����Ȩ�޶��󼯺�,<Ȩ�޶���ID,Ȩ�޶���>
	public static Map<String, WqAuthObj> authObjMap = new HashMap<String, WqAuthObj>();

	//��־���ͻ���,<��־����ID,��־����>
	public static Map<String,WqLogType> logTypeMap = new LinkedHashMap<String,WqLogType>();
	
	//ϵͳ���в˵�����<WqMenuInfo>
	public static List menuList;
	
	//ϵͳ������ҵ����<WqMenuInfo>
	public static List<WqTradeInfo> tradeList;

	//ϵͳ���в��ż���,<��˾ID,��˾�����в���>
	public static Map<String,List<WqDepInfo>> comDepMap = new HashMap<String,List<WqDepInfo>>();
	
	//ϵͳ���в��ż���,<����ID,����>
	public static Map<String,WqDepInfo> depMap = new HashMap<String,WqDepInfo>();
	
	//ϵͳ����Ա������,<����ID,���ŵ�����Ա��>
	public static Map<String,List<WqStaffInfo>> depStaffMap = new HashMap<String,List<WqStaffInfo>>();
	
	//ϵͳ����Ա������,<Ա��ID,Ա��>
	public static Map<String,WqStaffInfo> staffMap = new ConcurrentHashMap<String,WqStaffInfo>();

	//ϵͳ����Ա���澯����,<�澯����,[[Ա��ID,ʱ��]...]>
	public static Map<String,List<String[]>> staffAlarmMap = new ConcurrentHashMap<String,List<String[]>>();
	
	//ϵͳ����Ա���ֻ��ĵ�ǰ����״̬��ػ�������,<Ա��ID,״̬>
	public static Map<String,String[]> phoneStateMap = new HashMap<String,String[]>();
	
	//ϵͳ����Ա�����¶�λ��Ϣ����,<Ա��ID,��λ��Ϣ>
	public static Map<String,DcGpsReal> staffPostionMap = new ConcurrentHashMap<String,DcGpsReal>();

	//ϵͳ���пͻ���Ա���Ĺ�ϵ,<�ͻ�ID,����Ա��ID>
	public static Map<String,Set<String>> clientStaffIdMap = new ConcurrentHashMap<String,Set<String>>();

	//ϵͳ���пͻ���������Ķ��չ�ϵ,<�ͻ�ID,��������ID>
	public static Map<String,Set<String>> clientRegionIdMap = new ConcurrentHashMap<String,Set<String>>();
	
	//�������򼯺�,<����ID,����>
	public static Map<String,WqMapRegion> mapRegionMap = new HashMap<String, WqMapRegion>();

	//�ֻ�Ա������,<�ֻ���,Ա��ID>
	public static Map<String,String> phoneStaffIdMap = new HashMap<String, String>();

	//��������������
	public static List<DcChinaArea>  dcChinaAreaList = new ArrayList<DcChinaArea>();
	
	//������������ֵ,<���������,��������>
	public static Map<String,DcChinaArea>  dcChinaAreaMap = new HashMap<String,DcChinaArea>();
	
	//�ƻ���λ������ϸ��¼���棨keyΪstaffId,[keyΪplanLocateTime,valueΪ<WqPlanLocateDetail>]��
	public static Map<String,Map<String,WqPlanLocateDetail>> planLocateDetailStaffIdMap = new ConcurrentHashMap<String, Map<String,WqPlanLocateDetail>>();
	
	//��ҵ��ɫ����
	public static Map<String,WqTradeRole> tradeRoleMap = new LinkedHashMap<String,WqTradeRole>();
	
	//���ն��Ż���,<�ֻ���,���ż���>
	public static Map<String,List<SmsModel>> smsDeliverMap = new ConcurrentHashMap<String, List<SmsModel>>();

}
