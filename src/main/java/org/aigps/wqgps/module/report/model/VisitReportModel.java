
package org.aigps.wqgps.module.report.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.entity.WqClientInfo;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-6-29下午02:17:11
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
public class VisitReportModel {
	public String staffId;//员工编号
	public String staffName;//员工名称
	public int planVisitCount = 0;//计划拜访客户数
	public int actualVisitCount = 0;//实际拜访客户数
	public int validVisitCount = 0;//有效拜访客户数
	public int visitTimes = 0;//拜访客户次数
	public int validVisitTimes = 0;//有效拜访客户次数
	public long visitTotalLong = 0;//拜访总时长
	public long validVisitLong = 0;//有效拜访总时长
	public String visitDate;//拜访日期
	public List<WqClientInfo> notVisitClientList;//未按计划拜访的客户
	public List<WqClientInfo> visitClientList;//按计划拜访的客户
	public List<Map<String,Object>> visitList;//员工拜访的每条记录
	
	public VisitReportModel(String staffId, String staffName, String visitDate){
		this.staffId = staffId;
		this.staffName = staffName;
		this.visitDate = visitDate;
	}
	public void addVisitRecord(Map<String,Object> map){
		if(visitList == null){
			visitList = new ArrayList<Map<String,Object>>();
		}
		visitList.add(map);
	}
	public void addNotVisitClientRecord(WqClientInfo client){
		if(notVisitClientList == null){
			notVisitClientList = new ArrayList<WqClientInfo>();
		}
		notVisitClientList.add(client);
	}
	public void addVisitClient(WqClientInfo client){
		if(visitClientList==null){
			visitClientList = new ArrayList<WqClientInfo>();
		}
		if(!visitClientList.contains(client)){
			visitClientList.add(client);
		}
	}
	public void removeNotVisitClient(String clientId){
		if(notVisitClientList == null){
			return;
		}
		for(Iterator<WqClientInfo> it=notVisitClientList.iterator(); it.hasNext(); ){
			if(it.next().getId().equals(clientId)){
				it.remove();
			}
		}
	}
}

