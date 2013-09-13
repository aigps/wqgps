package org.aigps.wqgps.module.sysmanager.service;

import java.util.ArrayList;
import java.util.List;

import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.common.entity.WqRouteAssign;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.module.region.service.RegionService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@SuppressWarnings({"unchecked"})
public class RouteService {
	public final static Log log = LogFactory.getLog(RouteService.class);
	
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
	
	public List<WqMapRegion> findAllRoute() throws Exception{
		try{
			WqUserInfo user = AppUtil.getUserInfo();
			String[] ps = new String[]{"type","creater","companyId"};
			String[] vs = new String[]{"0",user.getUserName(),user.getCompanyId()};
			return publicDAO.findBy(ps, vs, WqMapRegion.class, "name", true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public WqMapRegion saveOrUpdateRoute(WqMapRegion entiry) throws Exception{
		try{
			return regionService.saveMapRegion(entiry);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public boolean deleteRoute(String id) throws Exception{
		try{
			regionService.deleteMapRegion(id);
			publicDAO.delete("routeId", id, WqRouteAssign.class);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	
	
	public List<WqRouteAssign> findRouteAssignByRouteId(String routeId) throws Exception{
		try{
			return publicDAO.findBy("routeId", routeId, WqRouteAssign.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	public List<WqRouteAssign> findRouteAssignByStaffId(String staffId) throws Exception{
		try{
			return publicDAO.findBy("staffId", staffId, WqRouteAssign.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public List<WqRouteAssign> batchSaveRouteAssign(WqRouteAssign ra,List<String> staffIds,List<String> routeIds) throws Exception{
		try{
			List<WqRouteAssign> list = new ArrayList<WqRouteAssign>();
			for(String staffId : staffIds){
				WqRouteAssign assign = (WqRouteAssign)BeanUtils.cloneBean(ra);
				assign.setStaffId(staffId);
				publicDAO.save(assign);
				list.add(assign);
			}
			for(String routeId : routeIds){
				WqRouteAssign assign = (WqRouteAssign)BeanUtils.cloneBean(ra);
				assign.setRouteId(routeId);
				publicDAO.save(assign);
				list.add(assign);
			}
			return list;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public WqRouteAssign saveOrUpdateRouteAssign(WqRouteAssign ra) throws Exception{
		try{
			publicDAO.save(ra);
			return ra;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public boolean deleteRouteAssign(String id) throws Exception{
		try{
			publicDAO.delete("id", id, WqRouteAssign.class);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
}
