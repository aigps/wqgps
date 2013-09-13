
package org.aigps.wqgps.module.report.dao;

import java.util.List;

import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.util.SqlStringUtil;
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
public class VisitReportDAO extends HibernateDAO<Object, String>{
	
	/**
	 * 在日期段内，查找指定员工的拜访计划,同时将对应的拜访客户也查出来
	 * @param staffIds 员工IDS
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return
	 */
	public List<Object[]> findVisitPlan(List<String> staffIds,String startDate,String endDate){
		String hql = "from WqVisitPlan p,WqClientInfo c where p.clientId=c.id and p.visitDate>=? and p.visitDate<=? and "+SqlStringUtil.formatListToSQLIn("p.staffId", staffIds, true) + " order by p.visitDate";
		return this.find(hql, startDate,endDate);
	}
	
	/**
	 * 在日期段内，查找指定员工的拜访计划,同时将对应的拜访客户也查出来
	 * @param staffIds 员工IDS
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return
	 */
	public List<Object[]> findVisitClient(String staffId,String startDate,String endDate){
		String hql = "from WqVisitPlan p,WqClientInfo c where p.clientId=c.id and p.visitDate>=? and p.visitDate<=? and p.staffId=? order by p.visitDate";
		return this.find(hql, startDate,endDate,staffId);
	}

	/**
	 * 在日期段内，查找指定员工的拜访计划,同时将对应的拜访客户也查出来
	 * @param staffIds 员工IDS
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return
	 */
	public List<Object[]> findVisitClient(List<String> staffIds,String startDate,String endDate){
		String hql = "from WqVisitPlan p,WqClientInfo c where p.clientId=c.id and p.visitDate>=? and p.visitDate<=? and "+SqlStringUtil.formatListToSQLIn("p.staffId", staffIds, true) + " order by p.visitDate";
		return this.find(hql, startDate,endDate);
	}
	
	/**
	 * 在日期段内，查找指定员工的拜访计划,同时将对应的拜访客户也查出来
	 * @param staffIds 员工IDS
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return
	 */
	public List<Object[]> findVisitPlan(String staffId,String startDate,String endDate){
		String hql = "from WqVisitPlan p,WqClientInfo c where p.clientId=c.id and p.visitDate>=? and p.visitDate<=? and p.staffId=? order by p.visitDate";
		return this.find(hql, startDate,endDate,staffId);
	}
	
}

