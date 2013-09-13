
package org.aigps.wqgps.common.util;


/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2011-4-28����10:20:54
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
 */
@SuppressWarnings("unused")
public class GpsUtil {

	private static final double EARTH_RADIUS = 6378.137*1000;  //�������뾶
	//����뾶
	private static double rad(double d){
		return  d*Math.PI/180.0;
	}
	/**
	 * �������㾭γ��֮��ľ���
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

	//�����(plng,plat)����[(llng1,llat1),(llng2,llat2)]�ľ���
	public static double distance(double plng,double plat,double llng1,double llat1,double llng2,double llat2){
		double a = distance(llng1, llat1, llng2, llat2);// �߶εĳ���
		double b = distance(plng, plat, llng1, llat1);// (llng1,llat1)����ľ���
		double c = distance(plng, plat, llng2, llat2);// (llng2,llat2)����ľ���
		
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
		double p = (a + b + c) / 2;// ���ܳ�
		double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// ���׹�ʽ�����
		return 2 * s / a;// ���ص㵽�ߵľ��루���������������ʽ��ߣ�
	}
}

