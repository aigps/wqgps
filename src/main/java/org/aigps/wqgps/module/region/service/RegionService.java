package org.aigps.wqgps.module.region.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.UIDUtil;
import org.aigps.wqgps.module.report.dao.RegionVisitDAO;
import org.aigps.wqgps.timing.CacheRetrospect;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class RegionService {
	public final static Log log = LogFactory.getLog(RegionService.class);
	
	private PublicDAO publicDAO;
	private RegionVisitDAO regionVisitDAO;
	
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	@Autowired
	public void setRegionVisitDAO(RegionVisitDAO regionVisitDAO) {
		this.regionVisitDAO = regionVisitDAO;
	}
	
	
	/**
	 * �����ͼ����
	 * @param entiry
	 * @return
	 */
	public WqMapRegion saveMapRegion(WqMapRegion entiry) throws Exception{
		try {
			if(entiry.getCreateTime() == null){
				entiry.setCreateTime(new Date());
				WqUserInfo user = AppUtil.getUserInfo();
				entiry.setCreater(user.getUserName());
				entiry.setCompanyId(user.getCompanyId());
			}
			if(StringUtils.isBlank(entiry.getId())){
				entiry.setId(UIDUtil.getId());
			}
			publicDAO.save(entiry);
			DataCache.mapRegionMap.put(entiry.getId(),entiry);
			return entiry;
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}
	
	/**
	 * ɾ����ͼ����
	 * @param entiry
	 * @return
	 */
	public WqMapRegion deleteMapRegion(WqMapRegion entiry) throws Exception{
		try {
			publicDAO.delete(entiry);
			DataCache.mapRegionMap.remove(entiry.getId());
			regionVisitDAO.deleteRegionVisitByRegionId(entiry.getId());
			CacheRetrospect.deleteRegion(entiry.getId());
			return entiry;
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}
	
	/**
	 * ɾ����ͼ����
	 * @param entiry
	 * @return
	 */
	public boolean deleteMapRegion(String id) throws Exception{
		try {
			publicDAO.delete("id",id,WqMapRegion.class);
			DataCache.mapRegionMap.remove(id);
			regionVisitDAO.deleteRegionVisitByRegionId(id);
			CacheRetrospect.deleteRegion(id);
			return true;
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}
	
	/**
	 * ����Id��ѯ��ͼ�����б�
	 * @param list
	 * @return
	 */
	public List<WqMapRegion> findMapRegoinBy(List<String> regionIds) throws Exception{
		try {
			List<WqMapRegion> list = new ArrayList<WqMapRegion>();
			for(String rid : regionIds){
				WqMapRegion region = DataCache.mapRegionMap.get(rid);
				if(region != null){
					list.add(region);
				}
			}
			return list;
		} catch (Exception e) {
			log.error(null, e);
			throw e;
		}
	}
}
