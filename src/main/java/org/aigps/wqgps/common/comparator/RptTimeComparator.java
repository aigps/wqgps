package org.sunleads.common.comparator;

import java.util.Comparator;
import java.util.Map;
/**
 * �ϱ�ʱ��Ƚ���
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class RptTimeComparator implements Comparator{
	
	public int compare(Object object1, Object object2) {
    	String l1 = ((Map)object1).get("REPORT_TIME").toString();
    	String l2 = ((Map)object2).get("REPORT_TIME").toString();
    	return l1.compareTo(l2);
    }

}
