package org.sunleads.module.region.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.WqMapRegion;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.UIDUtil;
import org.sunleads.module.report.dao.RegionVisitDAO;
import org.sunleads.timing.CacheRetrospect;

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
	 * 保存地图区域
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
	 * 删除地图区域
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
	 * 删除地图区域
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
	 * 根据Id查询地图区域列表
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
