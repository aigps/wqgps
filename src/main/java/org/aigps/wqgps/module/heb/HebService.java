package org.aigps.wqgps.module.heb;

import java.util.ArrayList;
import java.util.List;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.WqEleFence;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.common.entity.WqPoi;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.map.model.MapLocation;
import org.gps.map.model.ShapeEnum;
import org.gps.map.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class HebService {
	public final static Log log = LogFactory.getLog(HebService.class);
	private PublicDAO publicDAO;
	
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	
	public WqPoi[] findPoiByAddr(String[] addrs) throws Exception {
		try {
			WqPoi [] datas = new WqPoi[addrs.length];
			for(int i=0; i<addrs.length; i++) {
				List<WqPoi> poi = getPoi(addrs[i],null);
				datas[i] = (poi!=null && !poi.isEmpty()) ? poi.get(0) : null;
			}
			return datas;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public Object[] findRoute(List<WqPoi> poiList) {
		List<Double[]> nodes = new ArrayList<Double[]> ();
		for(WqPoi poi : poiList) {
			nodes.add(new Double[]{poi.getLng(),poi.getLat()});
			publicDAO.save(poi);
		}
		return new Object[]{poiList,GeoService.findRoute(nodes)};
	}
	
	public Object findEleFence(String addr, String customer) throws Exception {
		try {
			List<WqEleFence> eleList = getEle(AppUtil.getUserInfo());
			if(eleList == null || eleList.isEmpty()) {
				return 1;
			}
			List<WqPoi> poilist = getPoi(addr,customer);
			if(poilist == null || poilist.isEmpty()) {
				return 2;
			}
			Object[] info = getInRegion(eleList,poilist);
			if(info == null) {
				return poilist.get(0);
			}
			return info;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public Object[] findEleFence2(String addr, String customer, WqUserInfo user) throws Exception {
		try {
			List<WqEleFence> eleList = getEle(user);
			if(eleList == null || eleList.isEmpty()) {
				return null;
			}
			List<WqPoi> poilist = getPoi(addr,customer);
			if(poilist == null || poilist.isEmpty()) {
				return null;
			}
			return getInRegion(eleList,poilist);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	private List<WqEleFence> getEle(WqUserInfo user){
		if(user.getIsAdmin()){//公司管理员  不要权限
			String hql = "FROM WqEleFence where companyId=? and isEnable=? order by name";
			return publicDAO.find(hql,user.getCompanyId(),true);
		}else{//普通用户  要判断权限
			String hql = "FROM WqEleFence where companyId=? and isEnable=? and creater=? order by name";
			return publicDAO.find(hql,user.getCompanyId(),true,user.getUserName());
		}
	}
	
	private List<WqPoi> getPoi(String addr, String customer) {
		String hql = "from WqPoi where addr like '%"+addr+"%'";
		if(StringUtils.isNotBlank(customer)){
			hql +=" or name like '%"+customer+"%' ";
		}
		List<WqPoi> poilist = publicDAO.find(hql);
		if(poilist.isEmpty()) {
			List<Double[]> ll = GeoService.findLngLatList(addr);
			if((ll == null || ll.isEmpty()) && StringUtils.isNotBlank(customer)) {
				ll = GeoService.findLngLatList(customer);
			}
			if(ll == null || ll.isEmpty()) {
				return null;
			}
			for(Double[] l : ll) {
				WqPoi poi = new WqPoi();
				poi.setAddr(addr);
				poi.setName(customer);
				poi.setLng(l[0]);
				poi.setLat(l[1]);
				poilist.add(poi);
			}
		}
		return poilist;
	}
	
	private Object[] getInRegion(List<WqEleFence> eleList, List<WqPoi> poilist){
		for(WqEleFence ele : eleList) {
			WqMapRegion region = DataCache.mapRegionMap.get(ele.getRegionId());
			if(region == null) {
				continue;
			}
			if("0".equals(region.getType())) {
				continue;
			}
			for(WqPoi poi : poilist) {
				if(isInRegion(region, poi)) {
					return new Object[]{ele, region, poi};
				}
			}
		}
		return null;
	}
	
	private boolean isInRegion(WqMapRegion region, WqPoi poi){
		if(StringUtils.isBlank(region.getPoints())) {
			return false;
		}
		try {
			MapLocation ml = new MapLocation(poi.getLng(), poi.getLat());
			String[] points = region.getPoints().split(";");
			//矩形
			if("1".equals(region.getType())) {
				String[] p1 = points[0].split(",");
				String[] p2 = points[1].split(",");
				List<MapLocation> mllist = new ArrayList<MapLocation>();
				mllist.add(new MapLocation(Double.valueOf(p1[0]), Double.valueOf(p1[1])));
				mllist.add(new MapLocation(Double.valueOf(p2[0]), Double.valueOf(p2[1])));
				return MapUtil.isPointInPolygon(ShapeEnum.RECTANGLE, ml, mllist, 0);
			}
			//多边形
			if("2".equals(region.getType())) {
				List<MapLocation> mllist = new ArrayList<MapLocation>();
				for(String p : points){
					String[] p1 = p.split(",");
					mllist.add(new MapLocation(Double.valueOf(p1[0]), Double.valueOf(p1[1])));
				}
				return MapUtil.isPointInPolygon(ShapeEnum.POLYGON, ml, mllist, 0);
			}
			//圆形
			if("3".equals(region.getType())) {
				String[] p1 = points[0].split(",");
				MapLocation center = new MapLocation(Double.valueOf(p1[0]), Double.valueOf(p1[1]));
				return MapUtil.isInsideCircle(ml, center, region.getRadius().doubleValue() , 0);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return false;
	}
}
