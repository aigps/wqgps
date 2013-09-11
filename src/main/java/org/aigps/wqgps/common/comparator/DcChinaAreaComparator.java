package org.sunleads.common.comparator;

import java.util.Comparator;

import org.sunleads.common.entity.DcChinaArea;

public 	class DcChinaAreaComparator implements Comparator<DcChinaArea>{
	@Override
	public int compare(DcChinaArea o1, DcChinaArea o2) {
		if(o1.getSort().intValue() > o2.getSort().intValue()){
			return 1;
		}else{
			return -1;
		}
	}
}