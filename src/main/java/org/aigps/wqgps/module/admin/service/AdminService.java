package org.sunleads.module.admin.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.DcChinaArea;
import org.sunleads.common.entity.WqAdvice;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqLogInfo;
import org.sunleads.common.entity.WqMenuInfo;
import org.sunleads.common.entity.WqPhoneTime;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.entity.WqTradeInfo;
import org.sunleads.common.entity.WqTradeRole;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.log.LogType;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.DateUtil;
import org.sunleads.common.util.FileUtil;
import org.sunleads.module.sysmanager.service.SysManagerService;
import org.sunleads.timing.CacheTradeRoleMap;

import edu.emory.mathcs.backport.java.util.Collections;

@Component
@Transactional
@SuppressWarnings({"rawtypes","unchecked"})
public class AdminService {
	public final static Log log = LogFactory.getLog(AdminService.class);
	
	private PublicDAO publicDAO;
	
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}

	//==========================================公司管理=====================================================
	
	//查询有权限看到的公司
	public List<Map<String,Object>> findAllCompany() throws Exception{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		//所有公司管理员
		List<WqUserInfo> userAdminList = publicDAO.findBy("isAdmin", true, WqUserInfo.class);
		
		WqUserInfo me = AppUtil.getUserInfo();
		List<Object> rightSuperAdmin = getSubSuperAdmin(me,false,null);
		
		for(WqCompanyInfo company : DataCache.companyInfoMap.values()){
			//能看到自己创建的和子管理员创建的公司
			if("yd_admin".equals(me.getUserName()) || "lt_admin".equals(me.getUserName()) || "dx_admin".equals(me.getUserName()) || rightSuperAdmin.contains(company.getCreater())){
				Map<String,Object> map = BeanUtils.describe(company);
				for(WqUserInfo user : userAdminList){
					//公司管理员
					if(user.getIsEnable() && company.getId().equals(user.getCompanyId())){
						map.put("username", user.getUserName());
						map.put("password", user.getPassword());
						map.put("userid", user.getId());
						map.put("cnName", user.getCnName());
						break;
					}
				}
				//公司所在区域名称
				map.put("zcodeName", getZcodeName(company.getLocation()));
				//公司的员工数
				int staffCount = 0;
				for(WqStaffInfo staff : DataCache.staffMap.values()){
					if(company.getId().equals(staff.getCompanyId())){
						staffCount ++;
					}
				}
				map.put("staffCount", staffCount);
				list.add(map);
			}
		}
		return list;
	}
	
	//获取admin及其创建的所有子超级管理员
	private List<Object> getSubSuperAdmin(WqUserInfo admin,boolean isBean,List<WqUserInfo> superAdminList){
		if(superAdminList == null){
			superAdminList = publicDAO.find("from WqUserInfo where superAdmin in ('1','2') order by userName");
		}
		List<Object> subList = new ArrayList<Object>();
		subList.add(isBean?admin:admin.getUserName());
		for(WqUserInfo user : superAdminList){
			if(admin.getId().equals(user.getParentId())){
				subList.addAll(getSubSuperAdmin(user,isBean,superAdminList));
			}
		}
		return subList;
	}
	
	//保存公司
	public Map<String,String> saveCompany(Map<String,String> company) throws Exception{
		WqCompanyInfo com = new WqCompanyInfo();
		try {
			for(Iterator<String> it=company.keySet().iterator();it.hasNext();){
				if(company.get(it.next()) == null){
					it.remove();
				}
			}
			BeanUtils.populate(com, company);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		if(com.getCreateTime() == null){
			com.setCreateTime(new Date());
		}
		WqUserInfo me = AppUtil.getUserInfo();
		if(StringUtils.isBlank(com.getCreater())){
			com.setCreater(me.getUserName());
		}
		com.setIsEnable(true);
		publicDAO.save(com);
		if(company.get("id") == null){
			WqUserInfo user = new WqUserInfo();
			user.setIsAdmin(true);
			user.setCompanyId(com.getId());
			user.setCreater(me.getUserName());
			user.setCreateTime(new Date());
			user.setUserName((String)company.get("username"));
			user.setCnName(user.getUserName());
			user.setPassword((String)company.get("password"));
			user.setParentId(me.getId());
			user.setIsEnable(true);
			publicDAO.save(user);
			company.put("userid", user.getId());
			company.put("id", com.getId());
			
			if(StringUtils.isNotBlank(com.getUrlFlag())){
				String sDir = AppUtil.getWebAppPath()+File.separator+"company";
				String tDir = AppUtil.getWebAppPath() +File.separator + com.getUrlFlag();
				FileUtil.copyDirectiory(sDir,tDir);
			}
		}else{
			List<WqUserInfo> userAdminList = publicDAO.findBy("isAdmin", true, WqUserInfo.class);
			for(WqUserInfo user : userAdminList){
				if(user.getId().equals(company.get("userid"))){
					user.setUserName((String)company.get("username"));
					user.setPassword((String)company.get("password"));
					user.setCnName(company.get("cnName"));
					publicDAO.save(user);
					break;
				}
			}
		}
		DataCache.companyInfoMap.put(com.getId(),com);
		return company;
	}
	
	//删除公司
	public void deleteCompany(String id)  throws Exception{
		WqCompanyInfo com = DataCache.companyInfoMap.get(id);
		if(com != null){
			com.setIsEnable(false);
			publicDAO.save(com);
			List<WqUserInfo> userAdminList = publicDAO.findBy("isAdmin", true, WqUserInfo.class);
			for(WqUserInfo user : userAdminList){
				if(user.getCompanyId().equals(com.getId())){
					user.setIsEnable(false);
					publicDAO.save(user);
					break;
				}
			}
			FileUtil.deletefile(AppUtil.getWebAppPath()+File.separator+com.getUrlFlag());
		}
	}
	
	
	//==========================================区域分析=====================================================
	//获取所在指定区域的公司
	public List<Map<String,Object>> getComListByZcode(String zcode,List<Map<String,Object>> allCompany) throws Exception{
		if(allCompany == null){
			allCompany = findAllCompany();
		}
		List<Map<String,Object>> comList = new ArrayList<Map<String,Object>>();
		
		for(Map<String,Object> company : allCompany){
			String location = (String)company.get("location");
			if(location!=null){
				String[] zcodes = location.split(",");
				for(String z : zcodes){
					if(z.startsWith(zcode)){
						comList.add(company);
						break;
					}
				}
			}
		}
		return comList;
	}
	
	//分析zcode下的公司
	public List<Map<String,Object>> analyseLocation(String zcode,List<Map<String,Object>> allCompany) throws Exception{
		DcChinaArea area = DataCache.dcChinaAreaMap.get(zcode);
		List<DcChinaArea> subAreas = area!=null ? area.getChildren() : DataCache.dcChinaAreaList;
		if(subAreas==null || subAreas.isEmpty()){
			return null;
		}
		
		if(allCompany == null){
			allCompany = findAllCompany();
		}

		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		try{
			for(DcChinaArea sArea : subAreas){
				List<Map<String,Object>> comList = getComListByZcode(sArea.getZcode(),allCompany);
				if(comList.isEmpty()){
					continue;
				}
				int staffCount = 0, tryComCount = 0, officialComCount = 0, tryStaffCount = 0, officialStaffCount = 0;
				for(Map<String,Object> com : comList){
					staffCount += (Integer)com.get("staffCount");
					if("1".equals(com.get("state"))){//正式使用
						officialComCount ++;
						officialStaffCount += (Integer)com.get("staffCount");
					}else{
						tryComCount ++;
						tryStaffCount += (Integer)com.get("staffCount");
					}
				}
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("zcode", sArea.getZcode());
				map.put("zcodeName", sArea.getName());
				map.put("companyCount", comList.size());
				map.put("staffCount", staffCount);
				map.put("tryComCount", tryComCount);
				map.put("officialComCount", officialComCount);
				map.put("tryStaffCount", tryStaffCount);
				map.put("officialStaffCount", officialStaffCount);
				List<Map<String,Object>> l = analyseLocation(sArea.getZcode(),allCompany);
				map.put("hasSubAreaCompany", (l!=null && !l.isEmpty()));
				list.add(map);
			}
			return list;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	//==========================================行业分析=====================================================
	//获取所在指定行业的公司
	public List<Map<String,Object>> getComListByTrade(String tradeId) throws Exception{
		List<Map<String,Object>> allCompany = findAllCompany();
		List<Map<String,Object>> comList = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> com : allCompany){
			if(tradeId.equals(com.get("trade"))){
				comList.add(com);
			}
		}
		return comList;
	}
	//分析行业下的公司
	public Collection<Map<String,Object>> analyseTrade() throws Exception{
		List<Map<String,Object>> allCompany = findAllCompany();
		
		Map<String,Map<String,Object>> returnMap = new LinkedHashMap<String,Map<String,Object>>();
		
		for(Map<String,Object> com : allCompany){
			String trade = (String)com.get("trade");
			if(StringUtils.isBlank(trade)){
				continue;
			}
			for(WqTradeInfo t : DataCache.tradeList){
				if(!t.getId().equals(trade)){
					continue;
				}
				int companyCount=0,tryComCount=0,officialComCount=0,tryStaffCount=0,officialStaffCount=0,staffCount=(Integer)com.get("staffCount");
				Map<String,Object> map = returnMap.get(trade);
				if(map == null){
					returnMap.put(trade, map = new HashMap<String,Object>());
					map.put("tradeId", t.getId());
					map.put("tradeName", t.getName());
					if("1".equals(com.get("state"))){//正式使用
						officialComCount = 1;
						officialStaffCount = staffCount;
					}else{
						tryComCount = 1;
						tryStaffCount = staffCount;
					}
				}else{
					officialComCount = (Integer)map.get("officialComCount");
					officialStaffCount = (Integer)map.get("officialStaffCount");
					tryComCount = (Integer)map.get("tryComCount");
					tryStaffCount = (Integer)map.get("tryStaffCount");
					companyCount = (Integer)map.get("companyCount");
					
					if("1".equals(com.get("state"))){//正式使用
						officialComCount++;
						officialStaffCount += staffCount;
					}else{
						tryComCount++;
						tryStaffCount++;
					}
					staffCount += (Integer)map.get("staffCount");
				}
				map.put("companyCount", companyCount+1);
				map.put("staffCount", staffCount);
				map.put("officialComCount", officialComCount);
				map.put("officialStaffCount", officialStaffCount);
				map.put("tryComCount", tryComCount);
				map.put("tryStaffCount", tryStaffCount);
				break;
			}
		}
		
		return returnMap.values();
	}
	
	
	//==========================================超级管理员=====================================================
	//获取所有超级管理员
	public List<Object> findAllAdmin() throws Exception{
		try{
			WqUserInfo me = AppUtil.getUserInfo();
			List<Object> userAdminList = getSubSuperAdmin(me,true,null);
			userAdminList.remove(me);
			
			for(Iterator it = userAdminList.iterator(); it.hasNext();){
				WqUserInfo user = (WqUserInfo)it.next();
				DcChinaArea area = DataCache.dcChinaAreaMap.get(user.getAdminZcode());
				if(area != null){
					user.setZcodeName(area.getFullName());
				}
			}
			return userAdminList;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	//保存超级管理员
	public WqUserInfo saveAdmin(WqUserInfo admin) throws Exception{
		try{
			WqUserInfo me = AppUtil.getUserInfo();
			if(admin.getCreateTime() == null){
				admin.setCreateTime(new Date());
			}
			if(StringUtils.isBlank(admin.getCreater())){
				admin.setCreater(me.getUserName());
			}
			if(StringUtils.isBlank(admin.getParentId())){
				admin.setParentId(me.getId());
			}
			admin.setIsAdmin(false);
			admin.setIsEnable(true);
			publicDAO.save(admin);
			return admin;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	//删除超级管理员
	public void deleteAdmin(String id)  throws Exception{
		try{
			publicDAO.delete("id",id,WqUserInfo.class);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	
	//==========================================服务期限管理=====================================================
	
	public List<Map<String,Object>> findServiceTime() throws Exception{
		WqUserInfo me = AppUtil.getUserInfo();
		List<Object> rightSuperAdmin = getSubSuperAdmin(me,false,null);
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		
		Map<String,WqPhoneTime> ptMap = getAllPhoneTime(true);
		
		for(WqCompanyInfo company : DataCache.companyInfoMap.values()){
			//能看到自己创建的和子管理员创建的公司
			if("yd_admin".equals(me.getUserName()) || "lt_admin".equals(me.getUserName()) || "dx_admin".equals(me.getUserName()) || rightSuperAdmin.contains(company.getCreater())){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("companyId", company.getId());
				map.put("companyName", company.getName());
				map.put("zcodeName", getZcodeName(company.getLocation()));
				
				//公司的员工数
				int overTimeStaffCount = 0, noOverTimeStaffCount = 0, hadHandleCount = 0;
				for(WqStaffInfo staff : DataCache.staffMap.values()){
					if(company.getId().equals(staff.getCompanyId())){
						WqPhoneTime pt = ptMap.get(staff.getMobileNumber());
						if(pt == null || !pt.getIsOverTime()){
							noOverTimeStaffCount ++;
						}else{
							overTimeStaffCount ++;
							if(pt.getIsHandle()){
								hadHandleCount ++;
							}
						}
					}
				}
				map.put("overTimeCount", overTimeStaffCount);
				map.put("noOverTimeCount", noOverTimeStaffCount);
				map.put("hadHandleCount", hadHandleCount);
				
				returnList.add(map);
			}
		}
		return returnList;
	}
	
	public Object[] findPhoneByCompanyId(String companyId) throws Exception{
		Map<String,WqPhoneTime> ptMap = getAllPhoneTime(false);
		List<Map<String,Object>> overTimeList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> noOverTimeList = new ArrayList<Map<String,Object>>();
		String today = DateUtil.getCurDateStr("yyyy-MM-dd");
		
		for(WqStaffInfo staff : DataCache.staffMap.values()){
			if(companyId.equals(staff.getCompanyId())){
				WqPhoneTime pt = ptMap.get(staff.getMobileNumber());
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("staffId", staff.getId());
				map.put("staffName", staff.getCnName());
				map.put("phoneNumber", staff.getMobileNumber());
				if(pt != null){
					map.put("limitTime", pt.getLimitTime());
					if(pt.getIsOverTime()){
						map.put("isHandle", pt.getIsHandle());
						overTimeList.add(map);
					}else{
						map.put("differDay", DateUtil.getDistDates(today, pt.getLimitTime()));
						noOverTimeList.add(map);
					}
				}else{
					noOverTimeList.add(map);
				}
			}
		}
		return new Object[]{overTimeList,noOverTimeList};
	}
	
	public void setLimitTime(List<String> phones,String date) throws Exception{
		List<WqPhoneTime> list = (List<WqPhoneTime>)publicDAO.getAll(WqPhoneTime.class);
		if(StringUtils.isBlank(date)){//限期为无限期，直接删除期限设置记录
			for(WqPhoneTime p : list){
				if(phones.contains(p.getPhoneNumber())){
					publicDAO.delete(p);
				}
			}
			return;
		}
		String today = DateUtil.getCurDateStr("yyyy-MM-dd");
		boolean isOverTime = today.compareTo(date)>0;
		
		for(String phone : phones){
			WqPhoneTime pt = null;
			for(WqPhoneTime p : list){
				if(phone.equals(p.getPhoneNumber())){
					p.setIsOverTime(isOverTime);
					p.setLimitTime(date);
					pt = p;
					break;
				}
			}
			if(pt == null){
				pt = new WqPhoneTime();
				pt.setIsHandle(false);
				pt.setIsOverTime(isOverTime);
				pt.setLimitTime(date);
				pt.setPhoneNumber(phone);
			}
			publicDAO.save(pt);
		}
	}
	
	public List<String> handleOverTimePhones(List<String> phones) throws Exception{
		List<WqPhoneTime> list = (List<WqPhoneTime>)publicDAO.getAll(WqPhoneTime.class);
		for(WqPhoneTime p : list){
			if(phones.contains(p.getPhoneNumber())){
				if(p.getIsHandle() != true){
					p.setIsHandle(true);
					publicDAO.save(p);
				}
			}
		}
		return phones;
	}
	
	private Map<String,WqPhoneTime> getAllPhoneTime(Boolean checkOverTime){
		String today = DateUtil.getCurDateStr("yyyy-MM-dd");
		List<WqPhoneTime> list = (List<WqPhoneTime>)publicDAO.getAll(WqPhoneTime.class);
		Map<String,WqPhoneTime> map = new HashMap<String,WqPhoneTime>();
		for(WqPhoneTime time : list){
			map.put(time.getPhoneNumber(), time);
			
			if(checkOverTime == true){
				if(StringUtils.isBlank(time.getLimitTime())){//无期限
					time.setIsHandle(false);
					time.setIsOverTime(false);
				}else if(today.compareTo(time.getLimitTime())>=0){//期限未到
					time.setIsHandle(false);
					time.setIsOverTime(false);
				}else{//期限已到
					time.setIsOverTime(true);
				}
			}
		}
		return map;
	}
	

	//==========================================功能使用情况统计=====================================================
	public Object[] totalSysVisited(String startTime,String endTime) throws Exception{
		List<Map<String,Object>> allCompany = findAllCompany();
		
		Set<String> allComIds = new HashSet<String>();
		for(Map<String,Object> m : allCompany){
			allComIds.add((String)m.get("id"));
		}
		
		SysManagerService service = (SysManagerService)AppUtil.getBean("sysManagerService");
		List<WqLogInfo> logs = service.findLogBy(DataCache.logTypeMap.get(LogType.USER_CLICK_MENU).getId(), startTime, endTime, null, null);
		
		for(Iterator<WqLogInfo> it=logs.iterator(); it.hasNext(); ){
			WqLogInfo log = it.next();
			if(!allComIds.contains(log.getCompanyId())){
				it.remove();
			}
		}

		Map<String,Integer> totalDatas = new HashMap<String,Integer>();
		
		for(Map<String,Object> com : allCompany){
			String comId = (String)com.get("id");
			Map<String,Integer> datas = new HashMap<String,Integer>();
			com.put("datas", datas);
			
			for(WqLogInfo log : logs){
				if(!comId.equals(log.getCompanyId())){
					continue;
				}
				String menuId = log.getContent().split("_")[0];
				Integer num = datas.get(menuId);
				datas.put(menuId, num==null?1:(++num));
				
				num = totalDatas.get(menuId);
				totalDatas.put(menuId, num==null?1:(++num));
			}
		}
		
		List<Object[]> totalList = new ArrayList<Object[]>();
		for(Map.Entry<String, Integer> d : totalDatas.entrySet()){
			totalList.add(new Object[]{d.getKey(),d.getValue()});
		}

		//按点击次数进行排序
		Collections.sort(totalList, new Comparator<Object[]>() {
			public int compare(Object[] o1, Object[] o2) {
				int ii = (Integer)o2[1]-(Integer)o1[1];
				return ii==0?0:(ii>0?1:-1);
			}
		});
		
		for(Map<String,Object> com : allCompany){
			Map<String,Integer> datas = (Map<String,Integer>)com.get("datas");
			List<Object[]> dataList = new ArrayList<Object[]>();
			for(Object[] obj : totalList){
				if(datas.containsKey(obj[0])){
					dataList.add(new Object[]{obj[0],datas.get(obj[0])});
				}
			}
			com.put("datas", dataList);
		}
		
		Map<String,WqMenuInfo> menuMap = new HashMap<String,WqMenuInfo>();
		for(WqMenuInfo menu : (List<WqMenuInfo>)DataCache.menuList){
			menuMap.put(menu.getId(), menu);
		}
		
		for(Object[] obj : totalList){
			obj[0] = menuMap.get(obj[0]).getName();
		}
		for(Map<String,Object> com : allCompany){
			List<Object[]> dataList = (List<Object[]>)com.get("datas");
			for(Object[] obj : dataList){
				obj[0] = menuMap.get(obj[0]).getName();
			}
		}
		return new Object[]{totalList,allCompany};
	}
	
	//==========================================意见反馈=====================================================
	
	public WqAdvice saveAdvice(WqAdvice advice)throws Exception{
		try{
			WqUserInfo user = AppUtil.getUserInfo();
			advice.setCompanyId(user.getCompanyId());
			advice.setCreater(user.getUserName());
			advice.setCreateTime(new Date());
			advice.setIsHandled(false);
			publicDAO.save(advice);
			return advice;
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public boolean deleteAdvice(WqAdvice advice) throws Exception{
		try{
			publicDAO.delete(advice);
			return true;
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public boolean handledAdvices(List<WqAdvice> advices) throws Exception{
		try{
			for(WqAdvice advice : advices){
				advice.setIsHandled(true);
				publicDAO.save(advice);
			}
			return true;
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public List<WqAdvice> findAllAdvice() throws Exception{
		try{
			Set<String> allComIds = new HashSet<String>();
			WqUserInfo user = AppUtil.getUserInfo();
			boolean isNormalUser = false;
			if("1".equals(user.getSuperAdmin()) || "2".equals(user.getSuperAdmin())){//超级管理员
				List<Map<String,Object>> allCompany = findAllCompany();
				for(Map<String,Object> m : allCompany){
					allComIds.add((String)m.get("id"));
				}
			}else if(user.getIsAdmin()){//公司管理员
				allComIds.add(user.getCompanyId());
			}else{//普通用户
				isNormalUser = true;
				allComIds.add(user.getCompanyId());
			}
			List<WqAdvice> list = publicDAO.getAll("createTime", false, WqAdvice.class);
			for(Iterator<WqAdvice> it = list.iterator(); it.hasNext();){
				WqAdvice wa = it.next();
				if(!allComIds.contains(wa.getCompanyId())){
					it.remove();
				}
				if(isNormalUser == true && !user.getUserName().equals(wa.getCreater())){
					it.remove();
				}
			}
			return list;
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	private String getZcodeName(String zcodes){
		if(StringUtils.isNotBlank(zcodes)){
			String[] zs = zcodes.split(",");
			DcChinaArea area = DataCache.dcChinaAreaMap.get(zs[zs.length-1]);
			if(area != null){
				return area.getFullName();
			}
		}
		return "";
	}
	
	//==========================================行业角色管理=====================================================
	
	public WqTradeRole saveTradeRole(WqTradeRole trade) throws Exception{
		try{
			if(StringUtils.isBlank(trade.getId())){
				trade.setCreateTime(new Date());
			}
			publicDAO.save(trade);
			CacheTradeRoleMap.saveOrUpdateCache(trade);
			return trade;
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public Collection<WqTradeRole> findAllTradeRole() throws Exception{
		try{
			return DataCache.tradeRoleMap.values();
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public boolean deleteTradeRole(String tradeRoleId) throws Exception{
		try{
			publicDAO.delete("id", tradeRoleId, WqTradeRole.class);
			CacheTradeRoleMap.deleteCache(tradeRoleId);
			return true;
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
	}
}
