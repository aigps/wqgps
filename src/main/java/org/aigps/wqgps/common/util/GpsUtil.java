
package org.aigps.wqgps.common.util;


/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-4-28上午10:20:54
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
@SuppressWarnings("unused")
public class GpsUtil {

	private static final double EARTH_RADIUS = 6378.137*1000;  //地球赤道半径
	//计算半径
	private static double rad(double d){
		return  d*Math.PI/180.0;
	}
	/**
	 * 计算两点经纬度之间的距离
	 * @param prePoint
	 * @param fixPoint
	 * @return
	 */
	public static double distance(double lng1,double lat1,double lng2,double lat2){
		double radLat1 = rad(lat1);
	    double radLat2 = rad(lat2);
		double a = radLat1-radLat2;
		double b = rad(lng1) - rad(lng2);
		return 2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b/2),2))) * EARTH_RADIUS;
	}

	//计算点(plng,plat)到线[(llng1,llat1),(llng2,llat2)]的距离
	public static double distance(double plng,double plat,double llng1,double llat1,double llng2,double llat2){
		double a = distance(llng1, llat1, llng2, llat2);// 线段的长度
		double b = distance(plng, plat, llng1, llat1);// (llng1,llat1)到点的距离
		double c = distance(plng, plat, llng2, llat2);// (llng2,llat2)到点的距离
		
		if (c <= 0.000001 || b <= 0.000001) {
			return 0;
		}
		if (a <= 0.000001) {
			return b;
		}
		if (c * c >= a * a + b * b) {
			return b;
		}
		if (b * b >= a * a + c * c) {
			return c;
		}
		double p = (a + b + c) / 2;// 半周长
		double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
		return 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
	}
}

