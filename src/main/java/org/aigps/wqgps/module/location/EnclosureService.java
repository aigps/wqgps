package org.sunleads.module.location;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.auth.AuthType;
import org.sunleads.auth.AuthUtil;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.DcChinaArea;
import org.sunleads.common.entity.WqEleFence;
import org.sunleads.common.entity.WqMapRegion;
import org.sunleads.common.entity.WqRule;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.log.LogType;
import org.sunleads.common.log.LogUtil;
import org.sunleads.common.util.AppUtil;
import org.sunleads.module.region.service.RegionService;
/**
 * ����Χ������
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
//����Χ��
	/**
	 * ��ѯ����Χ��
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public List<WqEleFence> findWqEleFence() throws Exception{
		try {
			WqUserInfo wqUserInfo = AppUtil.getUserInfo();
			String companyId = wqUserInfo.getCompanyId();
			List<WqEleFence> resultList = null;
			//��˾����Ա  ��ҪȨ��
			if(wqUserInfo.getIsAdmin()){
				String hql = "FROM WqEleFence where companyId=? and isEnable=? order by name";
				resultList = publicDAO.find(hql,companyId,true);
			}else{//��ͨ�û�  Ҫ�ж�Ȩ��
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
	 * ���ݵ���Χ����ȡ����Χ������
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
	
	//����Χ��������
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
	 * �������޸ĵ���Χ��
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
	 * ɾ������Χ��
	 * @param wqEleFence
	 * @return
	 * @throws Exception
	 */
	public WqEleFence deleteWqEleFence(WqEleFence wqEleFence) throws Exception{
		try {
			if(WqEleFence.TYPE_ELE_FENCE.equals(wqEleFence.getType())){//����ǵ���Χ��
				//ɾ������Χ����Ӧ���й�����䵽��Ա��Ȩ��
				List<WqRule> wqRuleList = findWqRuleByWqEleFence(wqEleFence);
				for(WqRule wqRule : wqRuleList){
					AuthUtil.deleteResListByResId(AuthType.STAFF_RULE, wqRule.getId());
				}
				//ɾ������Χ����Ӧ�����й���
				publicDAO.delete("eleFenceId", wqEleFence.getId(), WqRule.class);
				//ɾ����Ӧ������
				if(wqEleFence.getRegionId() != null){
					regionService.deleteMapRegion(wqEleFence.getRegionId());
				}
			}
			//ɾ������Χ��
			publicDAO.delete("id",wqEleFence.getId(),WqEleFence.class);
			LogUtil.saveLog(LogType.DELETE_ELE_FENCE, wqEleFence.getName());
			return wqEleFence;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
//Χ������
	/**
	 * ���ݵ���Χ����ȡΧ������
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
	 * �������޸�Χ������
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
	 * ɾ��Χ������
	 * @param wqRule
	 * @return
	 * @throws Exception
	 */
	public WqRule deleteWqRule(WqRule wqRule) throws Exception{
		try {
			//ɾ��������䵽��Ա��Ȩ��
			AuthUtil.deleteResListByResId(AuthType.STAFF_RULE, wqRule.getId());
			//ɾ������
			publicDAO.delete("id", wqRule.getId(), WqRule.class);
			return wqRule;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	/**
	 * ����Χ�������ȡ�����Ա������
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
