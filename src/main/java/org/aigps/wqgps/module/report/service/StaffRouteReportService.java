package org.sunleads.module.report.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.DcGpsHis;
import org.sunleads.common.entity.WqMapRegion;
import org.sunleads.common.entity.WqRouteAssign;
import org.sunleads.common.util.GpsUtil;
import org.sunleads.module.location.dao.LocationDAO;
/**
 * 该类处理员工做的计划拜访，来获取员工计划拜访客户的详细情况
 */
@Component
@Transactional
public class StaffRouteReportService {
	public final static Log log = LogFactory.getLog(StaffRouteReportService.class);

	private PublicDAO publicDAO;
	private LocationDAO locationDAO;
	@Autowired
	public void setLocationDAO(LocationDAO locationDAO) {
		this.locationDAO = locationDAO;
	}
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}

	
	public List<Map<String,Object>> queryStaffRouteData(String staffId,String startData,String endData,double offset) throws Exception{
		try{
			List<WqRouteAssign> list = publicDAO.findBy("staffId", staffId, WqRouteAssign.class);
			if(list==null || list.isEmpty()){
				return null;
			}
			List<DcGpsHis> gvphList = locationDAO.findDataGpsHisByCondition(staffId, startData+" 00:00:00", endData+" 23:59:59");
			List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
			
			for(WqRouteAssign ra : list){
				WqMapRegion route = DataCache.mapRegionMap.get(ra.getRouteId());
				if(route == null || StringUtils.isBlank(route.getPoints())){
					continue;
				}
				Double[][] lls = formatPoints(route.getPoints());
				String startTime = ra.getStartTime();
				String endTime = ra.getEndTime();
				
				int totalGpsPoint = 0, inRouteGpsPoint = 0;
				for(DcGpsHis gps : gvphList){
					String reportTime = gps.getReportTime().substring(11);
					if(reportTime.compareTo(startTime)<0 || reportTime.compareTo(endTime)>0){
						continue;
					}
					boolean isInRoute = false;
					Double minDistance = 99999999999d, plng = gps.getLogitOffset().doubleValue(), plat = gps.getLatOffset().doubleValue();
					int index = -1;
					for(int i=0; i<lls.length; i++){
						double distince = GpsUtil.distance(lls[i][0], lls[i][1], plng, plat);
						if(offset >= distince){
							isInRoute = true;
							break;
						}
						if(minDistance > distince){
							minDistance = distince;
							index = i;
						}
					}
					
					if(isInRoute == false && index>0){
						double distince = GpsUtil.distance(plng,plat,lls[index][0], lls[index][1],lls[index-1][0], lls[index-1][1]);
						isInRoute = offset >= distince;
					}
					if(isInRoute == false && index<lls.length-1){
						double distince = GpsUtil.distance(plng,plat,lls[index][0], lls[index][1],lls[index+1][0], lls[index+1][1]);
						isInRoute = offset >= distince;
					}
					
					totalGpsPoint ++;
					if(isInRoute == true){
						inRouteGpsPoint ++;
					}
				}
				
				Map<String,Object> map = BeanUtils.describe(ra);
				map.put("routeName", route.getName());
				map.put("totalGpsPoint", totalGpsPoint);
				map.put("inRouteGpsPoint", inRouteGpsPoint);
				
				returnList.add(map);
			}
			return returnList;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	public Object[] queryStaffRouteAndGps(String staffId,String routeAssignId,String startData,String endData,double offset) throws Exception{
		try{
			List<DcGpsHis> gvphList = locationDAO.findDataGpsHisByCondition(staffId, startData+" 00:00:00", endData+" 23:59:59");
			
			WqRouteAssign ra = (WqRouteAssign)publicDAO.get(routeAssignId, WqRouteAssign.class);
			WqMapRegion route = DataCache.mapRegionMap.get(ra.getRouteId());
			
			Double[][] lls = formatPoints(route.getPoints());
			String startTime = ra.getStartTime();
			String endTime = ra.getEndTime();
			
			for(DcGpsHis gps : gvphList){
				String reportTime = gps.getReportTime().substring(11);
				if(reportTime.compareTo(startTime)<0 || reportTime.compareTo(endTime)>0){
					gps.setState("不在路线时间内");
					continue;
				}
				boolean isInRoute = false;
				Double minDistance = 999999999999d, plng = gps.getLogitOffset().doubleValue(), plat = gps.getLatOffset().doubleValue();
				int index = -1;
				for(int i=0; i<lls.length; i++){
					double distince = GpsUtil.distance(lls[i][0], lls[i][1], plng, plat);
					if(offset >= distince){
						isInRoute = true;
					}
					if(minDistance > distince){
						minDistance = distince;
						index = i;
					}
				}
				
				if(index>0){
					double distince = GpsUtil.distance(plng,plat,lls[index][0], lls[index][1],lls[index-1][0], lls[index-1][1]);
					isInRoute = isInRoute || offset >= distince;
					minDistance = isInRoute?distince:minDistance;
				}
				if(index<lls.length-1){
					double distince = GpsUtil.distance(plng,plat,lls[index][0], lls[index][1],lls[index+1][0], lls[index+1][1]);
					isInRoute = isInRoute || offset >= distince;
					minDistance = isInRoute?distince:minDistance;
				}
				gps.setState(isInRoute ? "在路线内": "不在路线内");
				gps.setMile(new BigDecimal(minDistance.intValue()));
			}
			return new Object[]{route,gvphList};
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	private Double[][] formatPoints(String points){
		String[] ps = points.split(";");
		Double[][] lls = new Double[ps.length][2];
		int index = 0;
		for(String p : ps){
			String[] ll =  p.split(",");
			try{
				lls[index] = new Double[]{Double.parseDouble(ll[0]),Double.parseDouble(ll[1])};
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			index++;
		}
		return lls;
	}
}

