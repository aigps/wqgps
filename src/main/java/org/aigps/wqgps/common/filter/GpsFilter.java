
package org.aigps.wqgps.common.filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.DcGpsHis;
import org.aigps.wqgps.common.entity.WqMapRegion;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.GpsUtil;
import org.aigps.wqgps.timing.CacheClientRegionIdMap;
import org.aigps.wqgps.timing.CacheClientStaffIdMap;
import org.apache.commons.lang.StringUtils;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-9-22上午11:13:41
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
public class GpsFilter {
	
	private static void setFixType(List<DcGpsHis> list){
		for(DcGpsHis node : list){
			node.setIsValidGps(true);
			if(isMsa(node)){
				node.setLocDesc("MSA "+node.getLocDesc());
			}else if(isGps(node)){
				node.setLocDesc("GPS "+node.getLocDesc());
			}else if(isGoogle(node)){
				node.setLocDesc("Google "+node.getLocDesc());
			}
		}
	}
	

	
//1  2 --------------------------------------------------------------------------------------------------
	
	public static void filterBy1Or2(List<DcGpsHis> list,int meter,boolean old)throws Exception{
		setFixType(list);
		
		List<DcGpsHis> baseNodes = new ArrayList<DcGpsHis>();
		
		for(DcGpsHis node : list){
			if(isGps(node)){
				baseNodes.clear();
			}else if(isMsa(node) || isGoogle(node)){
				checkValidNode(baseNodes,node,meter,old);
			}
		}
	}
	
	private static void checkValidNode(List<DcGpsHis> baseNodes,DcGpsHis node,int meter,boolean old) throws Exception{
		if(baseNodes.isEmpty()){
			node.setIsValidGps(true);
		}else if(baseNodes.size() == 1){
			node.setIsValidGps(isValidMeter(baseNodes.get(0),node,meter));
		}else if(baseNodes.size() == 2){
			DcGpsHis n1 = baseNodes.get(0);
			DcGpsHis n2 = baseNodes.get(1);
			addWeight(n1,n2,meter);
			addWeight(n1,node,meter);
			addWeight(n2,node,meter);
			
			if(node.weight>0){
				node.setIsValidGps(true);
			}else if((n1.weight+n2.weight)>0){
				node.setIsValidGps(false);
			}else{
				node.setIsValidGps(true);
			}
		}else if(baseNodes.size() == 3){
			DcGpsHis n1 = baseNodes.get(0);
			DcGpsHis n2 = baseNodes.get(1);
			DcGpsHis n3 = baseNodes.get(2);
			boolean t14 = addWeight(n1,node,meter);
			addWeight(n2,node,meter);
			boolean t34 = addWeight(n3,node,meter);
			
			//当前点
			if(node.weight>1){
				node.setIsValidGps(true);
			}else{
				int w = n1.weight+n2.weight+n3.weight;
				if(node.weight == 0){
					node.setIsValidGps(w==0);
				}else if(node.weight == 1){
					node.setIsValidGps(false);
					//node.setIsValidGps(w==1 || t14);
				}
			}
			
			if(old){
				//补点N3
				if(n3.getIsValidGps() == false){
					if(n3.weight>1){
						n3.setIsValidGps(true);
					}else if(n3.weight==1){
						if(t34 == true && node.getIsValidGps() == true){
							n3.setIsValidGps(true);
						}
					}else{
						int w = n1.weight+n2.weight+node.weight;
						if(w==0){
							n3.setIsValidGps(true);
						}
					}
				}
	
				//补点N1
				if(n1.getIsValidGps() == false){
					if(n1.weight>1){
						n1.setIsValidGps(true);
					}
				}
				
				//补点N2
				if(n2.getIsValidGps() == false){
					if(n2.weight>1){
						n2.setIsValidGps(true);
					}else if(n2.weight==1){
						if(n2.contains(n3) && (n1.weight+node.weight)==0){
							n2.setIsValidGps(true);
							n3.setIsValidGps(true);
						}
					}else if(n1.getIsValidGps()){
						int w = n1.weight+n3.weight+node.weight;
						if(w==0){
							n2.setIsValidGps(true);
						}
					}
				}
			}
			if(n1.weight>0){
				n2.removeWeightNode(n1);
				n3.removeWeightNode(n1);
				node.removeWeightNode(n1);
			}
			baseNodes.remove(n1);
		}
		baseNodes.add(node);
	}
	
	private static boolean addWeight(DcGpsHis n1,DcGpsHis n2,int meter){
		if(isValidMeter(n1,n2,meter)){
			n1.addWeightNode(n2);
			n2.addWeightNode(n1);
			return true;
		}
		return false;
	}
//end  1  2 --------------------------------------------------------------------------------------------------
	
	
	
	

//  3 -------------------------------------------------------------------------------------------------------
	public static void filterBy3(List<DcGpsHis> list,int meter){
		setFixType(list);
		
		List<DcGpsHis> msas = new ArrayList<DcGpsHis>();
		for(DcGpsHis node : list){
			if(isGps(node)){
				if(!msas.isEmpty()){
					filterBy3His(msas,meter);
					msas.clear();
				}
			}else if(isMsa(node) || isGoogle(node)){
				msas.add(node);
			}
		}
		if(!msas.isEmpty()){
			filterBy3His(msas,meter);
		}
	}
	
	private static void filterBy3His(List<DcGpsHis> list,int meter){
		List<List<DcGpsHis>> groupList = new ArrayList<List<DcGpsHis>>();
		for(DcGpsHis node : list){
			boolean hasAdd = false;
			
			for(List<DcGpsHis> group : groupList){
				DcGpsHis first = group.get(0);
				if(isValidMeter(first,node,meter) == true){
					group.add(node);
					hasAdd = true;
					break;
				}
			}
			if(hasAdd == false){
				List<DcGpsHis> group = new ArrayList<DcGpsHis>();
				group.add(node);
				groupList.add(group);
			}
		}
		if(groupList.size()<2){
			return;
		}
		
		boolean isLargeThan3 = false;
		for(List<DcGpsHis> group : groupList){
			if(group.size()>3){
				isLargeThan3 = true;
				break;
			}
		}
		if(isLargeThan3 == false){
			return;
		}
		
		for(int i=1; i<groupList.size(); i++){
			List<DcGpsHis> group1 = groupList.get(i);
			for(int j=0; j<i; j++){
				List<DcGpsHis> group2 = groupList.get(j);
				String maxTime = group2.get(group2.size()-1).getReportTime();
				
				for(Iterator<DcGpsHis> it=group1.iterator(); it.hasNext();){
					DcGpsHis g1 = it.next();
					if(g1.getIsValidGps() == false){
						continue;
					}
					if(maxTime.compareTo(g1.getReportTime())>=0){
						g1.setIsValidGps(false);
					}
				}
			}
		}
	}
//end  3 -------------------------------------------------------------------------------------------------------
	
	
//  4 -------------------------------------------------------------------------------------------------------
	public static void filterBy4(List<DcGpsHis> list,int meter){
		setFixType(list);
		
		String staffId = list.get(0).getTmnAlias();
		Set<String> clientIds = CacheClientStaffIdMap.getClientIds(staffId);
		Set<String> regionIds = CacheClientRegionIdMap.getRegionIds(clientIds);
		
		String companyRegionIds = AppUtil.getSessionData().getCompany().getRegionIds();
		if(StringUtils.isNotBlank(companyRegionIds)){
			regionIds.addAll(Arrays.asList(companyRegionIds.split(",")));
		}
		
		List<WqMapRegion> regions = new ArrayList<WqMapRegion>();
		
		for(String id : regionIds){
			WqMapRegion region = DataCache.mapRegionMap.get(id);
			if(region!=null){
				regions.add(region);
			}
		}
		
		for(DcGpsHis node : list){
			if(isMsa(node) || isGoogle(node)){
				node.setIsValidGps(false);
				for(WqMapRegion region : regions){
					if(isInRegion(node,region)){
						node.setIsValidGps(true);
						break;
					}
				}
			}
		}
	}
// end 4 -------------------------------------------------------------------------------------------------------
	
	
// 5 -------------------------------------------------------------------------------------------------------
	
	public static void filterBy5(List<DcGpsHis> list,int meter){
		setFixType(list);
		
		String staffId = list.get(0).getTmnAlias();
		Set<String> clientIds = CacheClientStaffIdMap.getClientIds(staffId);
		Set<String> regionIds = CacheClientRegionIdMap.getRegionIds(clientIds);
		
		String companyRegionIds = AppUtil.getSessionData().getCompany().getRegionIds();
		if(StringUtils.isNotBlank(companyRegionIds)){
			regionIds.addAll(Arrays.asList(companyRegionIds.split(",")));
		}
		
		List<WqMapRegion> regions = new ArrayList<WqMapRegion>();
		
		for(String id : regionIds){
			WqMapRegion region = DataCache.mapRegionMap.get(id);
			if(region!=null){
				regions.add(region);
			}
		}

		List<DcGpsHis> msas = new ArrayList<DcGpsHis>();
		for(DcGpsHis node : list){
			if(isGps(node)){
				if(!msas.isEmpty()){
					filterOuterRegion(msas,regions);
					msas.clear();
				}
			}else if(isMsa(node) || isGoogle(node)){
				msas.add(node);
			}
		}
		
	}
	

	private static void filterOuterRegion(List<DcGpsHis> msas,List<WqMapRegion> regions){
		WqMapRegion lastInRegion = null;
		List<DcGpsHis> notInRegionNode = new ArrayList<DcGpsHis>();
		for(DcGpsHis msa : msas){
			WqMapRegion inRegion = null;
			for(WqMapRegion region : regions){
				if(isInRegion(msa,region)){
					inRegion = region;
					break;
				}
			}
			if(inRegion == null){
				notInRegionNode.add(msa);
			}else{
				if(!notInRegionNode.isEmpty()){
					if(lastInRegion == inRegion){
						for(DcGpsHis no : notInRegionNode){
							no.setIsValidGps(false);
						}
					}
					notInRegionNode.clear();
				}
				lastInRegion = inRegion;
			}
		}
	}
// end 5 -------------------------------------------------------------------------------------------------------

	
	
	
	private static boolean isValidMeter(DcGpsHis g1,DcGpsHis g2,int meter){
		try{
			double result = GpsUtil.distance(g1.getLogitOffset().doubleValue(), g1.getLatOffset().doubleValue(), g2.getLogitOffset().doubleValue(), g2.getLatOffset().doubleValue());
			return result < meter;
		}catch(Exception e){
			return false;
		}
	}
	
	private static boolean isInRegion(DcGpsHis g, WqMapRegion region){
		if("3".equals(region.getType())){//圆
			int radius = region.getRadius().intValue();
			String[] ll = region.getPoints().split(",");
			DcGpsHis yx = new DcGpsHis();
			yx.setLogitOffset(new BigDecimal(ll[0]));
			yx.setLatOffset(new BigDecimal(ll[1]));
			return isValidMeter(yx,g,radius);
		}else if("1".equals(region.getType())){//矩形
			String[] points = region.getPoints().split(";");
			String[] p1 = points[0].split(",");
			String[] p2 = points[1].split(",");
			double p1lng = Double.parseDouble(p1[0]);
			double p1lat = Double.parseDouble(p1[1]);
			double p2lng = Double.parseDouble(p2[0]);
			double p2lat = Double.parseDouble(p2[1]);
			
			double lng = g.getLogit().doubleValue();
			double lat = g.getLat().doubleValue();
			
			if(lng>=p1lng && lng<=p2lng){
				if(lat<=p1lat && lat>=p2lat){
					return true;
				}
			}
		}else if("2".equals(region.getType())){//多边形
			
		}
		return false;
	}
	
	private static boolean isMsa(DcGpsHis gps){
		return "0".equals(gps.getStts3());
	}
	
	private static boolean isGps(DcGpsHis gps){
		return "2".equals(gps.getStts3());
	}
	
	private static boolean isGoogle(DcGpsHis gps){
		return "7".equals(gps.getStts3())||"1".equals(gps.getStts3());
	}
}

