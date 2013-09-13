
package org.aigps.wqgps.module.report.dao;

import java.util.List;

import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.WqTravelPlan;
import org.springframework.stereotype.Component;

/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-6-28下午01:22:17
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
@Component
public class CheckWorkReportDAO extends HibernateDAO<Object, String>{

	//获取公司，时间从startTime到endTime，定制的差旅计划
	public List<WqTravelPlan> findCompanyTravelPlan(String companyId,String startDate,String endDate){
		startDate = startDate+" 00:00:00";
		endDate = endDate+" 23:59:59";
		String hql = "from WqTravelPlan where companyId=? and ((startTime>=? and endTime<=?) or (startTime<=? and endTime>=?) or (startTime<=? and endTime>=?))";
		return this.find(hql, companyId, startDate, endDate, startDate, startDate, endDate, endDate);
	}
	
}

