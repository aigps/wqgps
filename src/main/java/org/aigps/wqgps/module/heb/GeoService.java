package org.aigps.wqgps.module.heb;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Component
@Transactional
public class GeoService {
	public final static Log log = LogFactory.getLog(GeoService.class);

	public static List<Double[]> findLngLatList(String addr){
		String json = findAddrsJson(addr);
		if(StringUtils.isBlank(json)){
			return null;
		}
		return getLngLatList(json);
	}

	//调用百度的URL查询得到地理结果的JSON
	private static String findAddrsJson(String addr){
		StringBuilder datas = new StringBuilder();
		
		try {
			String url = "http://search1.mapabc.com/sisserver?config=BESN&resType=json&number=20&enc=utf-8&a_k=eb9da267333335538a27e0defc7f403bc2ea896bb932f6a1b4f521d99b4ba6cb0db54f4b5d5f27b5&cityCode="+URLEncoder.encode("中国", "UTF-8")+"&searchName="+URLEncoder.encode(addr, "UTF-8");
			log.error("---------------url:"+url);
			URL myURL = new URL(url);
			URLConnection httpsConn = (URLConnection) myURL.openConnection();
			if (httpsConn != null) {
				httpsConn.setConnectTimeout(5000);
				InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(),"UTF-8");
				BufferedReader br = new BufferedReader(insr);
				String data = null;
				while ((data = br.readLine()) != null) {
					datas.append(data);
				}
				insr.close();
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return null;
		}
		return datas.toString();
	}
	
	//通过地理关键字，和城市编号，查询地理位置
	private static List<Double[]> getLngLatList(String json){
		log.error("---------------json:"+json);
		
		JSONObject jsonObj = JSON.parseObject(json);
		String status = (String)jsonObj.get("message");
		if(!"ok".equals(status)){
			return null;
		}
		int count = jsonObj.getIntValue("count");
		if(count == 0){
			return null;
		}
		JSONArray poilist = (JSONArray)jsonObj.get("poilist");
		List<Double[]> list = new ArrayList<Double[]>();
		for(Object obj : poilist){
			JSONObject item = (JSONObject)obj;
			Double lng = item.getDouble("x");
			Double lat = item.getDouble("y");
			list.add(new Double[]{lng,lat});
		}
		return list;
	}
	
	
	
	//==========================================================================================================================

	public static void main(String[] args ) {
		List<Double[]> poiList = new ArrayList<Double[]>();
		poiList.add(new Double[]{116.388,39.9882});
		poiList.add(new Double[]{116.288,39.8882});
		poiList.add(new Double[]{116.280,39.8800});
		poiList.add(new Double[]{116.398,39.9082});
		
		String json = findRouteJson(poiList);
		System.out.println(json);
		List<Double[]> ll = getRouteList(json);
		System.out.println(ll);
	}

	public static List<Double[]> findRoute(List<Double[]> poiList) {
		String json = findRouteJson(poiList);
		if(StringUtils.isBlank(json)){
			return null;
		}
		return getRouteList(json);
	}

	//调用百度的URL查询得到地理结果的JSON
	private static String findRouteJson(List<Double[]> poiList){
		StringBuilder datas = new StringBuilder();
		Double[] start = poiList.get(0);
		Double[] end = poiList.get(poiList.size()-1);
		String xs = "", ys = "";
		if(poiList.size() > 2) {
			Object[] lngs = new Object[poiList.size()-2];
			Object[] lats = new Object[poiList.size()-2];
			for(int i=1; i<poiList.size()-1; i++){
				lngs[i-1] = poiList.get(i)[0];
				lats[i-1] = poiList.get(i)[1];
			}
			xs = StringUtils.join(lngs, ",");
			ys = StringUtils.join(lats, ",");
		}
		try {
			String url = "http://search1.mapabc.com/sisserver?resType=json&enc=utf-8&ver=2.0&config=R&routeType=2&a_k=eb9da267333335538a27e0defc7f403bc2ea896bb932f6a1b4f521d99b4ba6cb0db54f4b5d5f27b5&x1=%s&y1=%s&xs=%s&ys=%s&x2=%s&y2=%s";
			url = String.format(url, start[0], start[1], xs, ys, end[0], end[1]);
			log.error("---------------url:"+url);
			System.out.println(url);
			URL myURL = new URL(url);
			URLConnection httpsConn = (URLConnection) myURL.openConnection();
			if (httpsConn != null) {
				httpsConn.setConnectTimeout(5000);
				InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream(),"UTF-8");
				BufferedReader br = new BufferedReader(insr);
				String data = null;
				while ((data = br.readLine()) != null) {
					datas.append(data);
				}
				insr.close();
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return null;
		}
		return datas.toString();
	}
	

	private static List<Double[]> getRouteList(String json){
		log.error("---------------json:"+json);
		
		JSONObject jsonObj = JSON.parseObject(json);
		String status = (String)jsonObj.get("message");
		if(!"ok".equals(status)){
			return null;
		}
		int count = jsonObj.getIntValue("count");
		if(count == 0){
			return null;
		}
		JSONArray segmengList = (JSONArray)jsonObj.get("segmengList");
		List<Double[]> list = new ArrayList<Double[]>();
		for(Object obj : segmengList){
			JSONObject item = (JSONObject)obj;
			String coor = item.getString("coor");
			String[] coors = coor.split(",");
			for(int i=0; i<coors.length; i=i+2) {
				list.add(new Double[]{Double.valueOf(coors[i]),Double.valueOf(coors[i+1])});
			}
		}
		return list;
	}
	
	
}
