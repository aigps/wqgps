package org.aigps.wqgps.module.location;

import java.util.Date;
import java.util.List;

import org.aigps.wqgps.auth.AuthType;
import org.aigps.wqgps.auth.AuthUtil;
import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.DcChinaArea;
import org.aigps.wqgps.common.entity.WqEleFence;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.common.entity.WqRule;
import org.aigps.wqgps.common.entity.WqStaffInfo;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.log.LogType;
import org.aigps.wqgps.common.log.LogUtil;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.module.region.service.RegionService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * 电子围栏服务
 * @author admin
 *
 */
@Component
@Transactional
@SuppressWarnings("unchecked")
public class EnclosureService {
	public final static Log log = LogFactory.getLog(EnclosureService.class);
	private PublicDAO publicDAO;
	private RegionService regionService;
	@Autowired
	public void setRegionService(RegionService regionService) {
		this.regionService = regionService;
	}
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
//电子围栏
	/**
	 * 查询电子围栏
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public List<WqEleFence> findWqEleFence() throws Exception{
		try {
			WqUserInfo wqUserInfo = AppUtil.getUserInfo();
			String companyId = wqUserInfo.getCompanyId();
			List<WqEleFence> resultList = null;
			//公司管理员  不要权限
			if(wqUserInfo.getIsAdmin()){
				String hql = "FROM WqEleFence where companyId=? and isEnable=? order by name";
				resultList = publicDAO.find(hql,companyId,true);
			}else{//普通用户  要判断权限
				String hql = "FROM WqEleFence where companyId=? and isEnable=? and creater=? order by name";
				resultList = publicDAO.find(hql,companyId,true,wqUserInfo.getUserName());
			}
			for(WqEleFence wqEleFence : resultList){
				if(StringUtils.isNotBlank(wqEleFence.getAreaId())){
					DcChinaArea area = DataCache.dcChinaAreaMap.get(wqEleFence.getAreaId());
					if(area != null){
						wqEleFence.setAreaName(area.getFullName());
					}
				}
			}
			return resultList;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	/**
	 * 根据电子围栏获取电子围栏区域
	 * @param wqEleFence
	 * @return
	 * @throws Exception
	 */
	public WqMapRegion findWqMapRegionByWqEleFence(WqEleFence wqEleFence) throws Exception{
		try{
			return DataCache.mapRegionMap.get(wqEleFence.getRegionId());
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	//更新围栏的区域
	public WqEleFence updateWqEleFenceRegion(WqEleFence wqEleFence,WqMapRegion region) throws Exception{
		if(region == null){
			if(StringUtils.isNotBlank(wqEleFence.getRegionId())){
				regionService.deleteMapRegion(wqEleFence.getRegionId());
				wqEleFence.setRegionId(null);
			}
			DcChinaArea area = DataCache.dcChinaAreaMap.get(wqEleFence.getAreaId());
			if(area != null){
				wqEleFence.setAreaName(area.getFullName());
			}
		}else{
			wqEleFence.setAreaId(null);
			regionService.saveMapRegion(region);
			if(!region.getId().equals(wqEleFence.getRegionId())){
				if(StringUtils.isNotBlank(wqEleFence.getRegionId())){
					regionService.deleteMapRegion(wqEleFence.getRegionId());
				}
				wqEleFence.setRegionId(region.getId());
			}
		}
		publicDAO.save(wqEleFence);
		
		return wqEleFence;
	}
	
	/**
	 * 新增或修改电子围栏
	 * @param wqEleFence
	 * @param wqMapRegion
	 * @return
	 * @throws Exception
	 */
	public WqEleFence saveOrUpdateWqEleFence(WqEleFence wqEleFence) throws Exception{
		try {
			WqUserInfo wqUserInfo = AppUtil.getUserInfo();
			String companyId = wqUserInfo.getCompanyId();
			String userName = wqUserInfo.getUserName();
			boolean createFlag = wqEleFence.getId()==null?true:false;
			if(createFlag){
				wqEleFence.setIsEnable(true);
				wqEleFence.setCompanyId(companyId);
				wqEleFence.setCreater(userName);
				wqEleFence.setCreateTime(new Date());
			}
			publicDAO.save(wqEleFence);
			if(createFlag){
				LogUtil.saveLog(LogType.CREATE_ELE_FENCE, wqEleFence.getName());
			}else{
				LogUtil.saveLog(LogType.UPDATE_ELE_FENCE, wqEleFence.getName());
			}
			return wqEleFence;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	
	/**
	 * 删除电子围栏
	 * @param wqEleFence
	 * @return
	 * @throws Exception
	 */
	public WqEleFence deleteWqEleFence(WqEleFence wqEleFence) throws Exception{
		try {
			if(WqEleFence.TYPE_ELE_FENCE.equals(wqEleFence.getType())){//如果是电子围栏
				//删除电子围栏对应所有规则分配到的员工权限
				List<WqRule> wqRuleList = findWqRuleByWqEleFence(wqEleFence);
				for(WqRule wqRule : wqRuleList){
					AuthUtil.deleteResListByResId(AuthType.STAFF_RULE, wqRule.getId());
				}
				//删除电子围栏对应的所有规则
				publicDAO.delete("eleFenceId", wqEleFence.getId(), WqRule.class);
				//删除对应的区域
				if(wqEleFence.getRegionId() != null){
					regionService.deleteMapRegion(wqEleFence.getRegionId());
				}
			}
			//删除电子围栏
			publicDAO.delete("id",wqEleFence.getId(),WqEleFence.class);
			LogUtil.saveLog(LogType.DELETE_ELE_FENCE, wqEleFence.getName());
			return wqEleFence;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
//围栏规则
	/**
	 * 根据电子围栏获取围栏规则
	 * @return
	 * @throws Exception
	 */
	public List<WqRule> findWqRuleByWqEleFence(WqEleFence wqEleFence)throws Exception{
		try{
			return publicDAO.findBy("eleFenceId", wqEleFence.getId(),WqRule.class);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	/**
	 * 新增或修改围栏规则
	 */
	public WqRule saveOrUpdateWqRule(WqRule wqRule,List<String> staffIdList)throws Exception{
		try {
			WqUserInfo wqUserInfo = AppUtil.getUserInfo();
			String userName = wqUserInfo.getUserName();
			String companyId = wqUserInfo.getCompanyId();
			boolean createFlag = wqRule.getId()==null?true:false;
			if(createFlag){
				wqRule.setCompanyId(companyId);
				wqRule.setIsEnable(true);
				wqRule.setCreateTime(new Date());
				wqRule.setCreater(userName);
			}
			publicDAO.save(wqRule);
			AuthUtil.saveOwnerListByResId(AuthType.STAFF_RULE,staffIdList,wqRule.getId());
			return wqRule;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	/**
	 * 删除围栏规则
	 * @param wqRule
	 * @return
	 * @throws Exception
	 */
	public WqRule deleteWqRule(WqRule wqRule) throws Exception{
		try {
			//删除规则分配到的员工权限
			AuthUtil.deleteResListByResId(AuthType.STAFF_RULE, wqRule.getId());
			//删除规则
			publicDAO.delete("id", wqRule.getId(), WqRule.class);
			return wqRule;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	/**
	 * 根据围栏规则获取分配的员工集合
	 * @param wqRule
	 * @return
	 * @throws Exception
	 */
	public List<WqStaffInfo> findWqStaffInfoByWqRule(WqRule wqRule) throws Exception{
		try{
			return AuthUtil.getOwnerListByResId(AuthType.STAFF_RULE,wqRule.getId());
		}catch(Exception e){
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	public List<WqMapRegion> findMapRegoinByIds(List<String> regionIds){
		try {
			return regionService.findMapRegoinBy(regionIds);
		} catch (Exception e) {
			log.error(null, e);
		}
		return null;
	}
}
