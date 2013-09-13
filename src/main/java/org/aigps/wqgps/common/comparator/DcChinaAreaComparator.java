package org.aigps.wqgps.common.comparator;

import java.util.Comparator;

import org.aigps.wqgps.common.entity.DcChinaArea;

public 	class DcChinaAreaComparator implements Comparator<DcChinaArea>{
	public int compare(DcChinaArea o1, DcChinaArea o2) {
		if(o1.getSort().intValue() > o2.getSort().intValue()){
			return 1;
		}else{
			return -1;
		}
	}
}