package org.sunleads.common.entity;

import java.util.HashSet;
import java.util.Set;



public class DcGpsHis extends DcGpsReal{

	/**
	 * 
	 */
	private static final long serialVersionUID = 546133226L;
	
	//存在定位
	private boolean existPositionFlag = true;
	//权重
	public int weight = 0;
	private Set<DcGpsHis> weightNodes;
	
	public boolean isExistPositionFlag() {
		return existPositionFlag;
	}

	public void setExistPositionFlag(boolean existPositionFlag) {
		this.existPositionFlag = existPositionFlag;
	}

	public boolean removeWeightNode(DcGpsHis node){
		if(weightNodes==null){
			return false;
		}
		boolean f = weightNodes.remove(node);
		if(f){
			weight--;
			if(weightNodes.isEmpty()){
				weightNodes = null;
			}
			node.removeWeightNode(this);
		}
		return f;
	}
	
	public boolean contains(DcGpsHis node){
		if(weightNodes==null){
			return false;
		}
		return weightNodes.contains(node);
	}
	
	public void addWeightNode(DcGpsHis node){
		if(weightNodes==null){
			weightNodes=new HashSet<DcGpsHis>();
		}
		weightNodes.add(node);
		weight ++ ;
	}
}
