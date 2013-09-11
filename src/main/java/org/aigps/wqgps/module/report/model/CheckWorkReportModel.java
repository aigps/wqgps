
package org.sunleads.module.report.model;

import java.util.ArrayList;
import java.util.List;


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
public class CheckWorkReportModel {
	public String staffId;//员工编号
	public String staffName;//员工名称
	public int normal = 0;//正常出勤
	public int travel = 0;//出差数
	public int offwork = 0;//脱岗数
	public int late = 0;//迟到数
	public int early = 0;//早退数
	
	public boolean needSignIn = false;
	public boolean needSignOut = false;
	
	public List<String> dateState;//每天的考勤状态
	
	public CheckWorkReportModel(String staffId, String staffName){
		this.staffId = staffId;
		this.staffName = staffName;
	}
	
	public void addDateState(String date,String state){
		if(dateState == null){
			dateState = new ArrayList<String>();
		}
		dateState.add(date+"_"+state);
	}
}

