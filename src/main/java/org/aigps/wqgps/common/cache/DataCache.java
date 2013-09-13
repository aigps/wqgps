package org.aigps.wqgps.common.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.aigps.wqgps.common.entity.DcChinaArea;
import org.aigps.wqgps.common.entity.DcGpsReal;
import org.aigps.wqgps.common.entity.SmsModel;
import org.aigps.wqgps.common.entity.WqAuthObj;
import org.aigps.wqgps.common.entity.WqCompanyInfo;
import org.aigps.wqgps.common.entity.WqDepInfo;
import org.aigps.wqgps.common.entity.WqLogType;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.common.entity.WqPlanLocateDetail;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.entity.WqTradeInfo;
import org.aigps.wqgps.common.entity.WqTradeRole;

/**
 * 数据缓存
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class DataCache {

	//系统参数配置信息,<key,value>
	public static Map<String, String> systemParamsMap = new HashMap<String, String>();

	//系统所有公司的对象,<公司ID,公司>
	public static Map<String, WqCompanyInfo> companyInfoMap = new HashMap<String,WqCompanyInfo>();
	
	//系统所有权限对象集合,<权限对象ID,权限对象>
	public static Map<String, WqAuthObj> authObjMap = new HashMap<String, WqAuthObj>();

	//日志类型缓存,<日志类型ID,日志类型>
	public static Map<String,WqLogType> logTypeMap = new LinkedHashMap<String,WqLogType>();
	
	//系统所有菜单集合<WqMenuInfo>
	public static List menuList;
	
	//系统所有行业集合<WqMenuInfo>
	public static List<WqTradeInfo> tradeList;

	//系统所有部门集合,<公司ID,公司的所有部门>
	public static Map<String,List<WqDepInfo>> comDepMap = new HashMap<String,List<WqDepInfo>>();
	
	//系统所有部门集合,<部门ID,部门>
	public static Map<String,WqDepInfo> depMap = new HashMap<String,WqDepInfo>();
	
	//系统所有员工集合,<部门ID,部门的所有员工>
	public static Map<String,List<WqStaffInfo>> depStaffMap = new HashMap<String,List<WqStaffInfo>>();
	
	//系统所有员工集合,<员工ID,员工>
	public static Map<String,WqStaffInfo> staffMap = new ConcurrentHashMap<String,WqStaffInfo>();

	//系统所有员工告警集合,<告警类型,[[员工ID,时间]...]>
	public static Map<String,List<String[]>> staffAlarmMap = new ConcurrentHashMap<String,List<String[]>>();
	
	//系统所有员工手机的当前最新状态如关机开机等,<员工ID,状态>
	public static Map<String,String[]> phoneStateMap = new HashMap<String,String[]>();
	
	//系统所有员工最新定位信息集合,<员工ID,定位信息>
	public static Map<String,DcGpsReal> staffPostionMap = new ConcurrentHashMap<String,DcGpsReal>();

	//系统所有客户和员工的关系,<客户ID,所有员工ID>
	public static Map<String,Set<String>> clientStaffIdMap = new ConcurrentHashMap<String,Set<String>>();

	//系统所有客户及其区域的对照关系,<客户ID,所在区域ID>
	public static Map<String,Set<String>> clientRegionIdMap = new ConcurrentHashMap<String,Set<String>>();
	
	//所有区域集合,<区域ID,区域>
	public static Map<String,WqMapRegion> mapRegionMap = new HashMap<String, WqMapRegion>();

	//手机员工集合,<手机号,员工ID>
	public static Map<String,String> phoneStaffIdMap = new HashMap<String, String>();

	//行政区域树缓存
	public static List<DcChinaArea>  dcChinaAreaList = new ArrayList<DcChinaArea>();
	
	//行政区域树键值,<行政区域号,行政区域>
	public static Map<String,DcChinaArea>  dcChinaAreaMap = new HashMap<String,DcChinaArea>();
	
	//计划定位任务详细记录缓存（key为staffId,[key为planLocateTime,value为<WqPlanLocateDetail>]）
	public static Map<String,Map<String,WqPlanLocateDetail>> planLocateDetailStaffIdMap = new ConcurrentHashMap<String, Map<String,WqPlanLocateDetail>>();
	
	//行业角色缓存
	public static Map<String,WqTradeRole> tradeRoleMap = new LinkedHashMap<String,WqTradeRole>();
	
	//接收短信缓存,<手机号,短信集合>
	public static Map<String,List<SmsModel>> smsDeliverMap = new ConcurrentHashMap<String, List<SmsModel>>();

}
