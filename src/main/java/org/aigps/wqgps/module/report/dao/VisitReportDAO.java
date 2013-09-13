
package org.aigps.wqgps.module.report.dao;

import java.util.List;

import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.util.SqlStringUtil;
import org.springframework.stereotype.Component;

/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2011-6-28����01:22:17
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
 */
@Component
public class VisitReportDAO extends HibernateDAO<Object, String>{
	
	/**
	 * �����ڶ��ڣ�����ָ��Ա���İݷüƻ�,ͬʱ����Ӧ�İݷÿͻ�Ҳ�����
	 * @param staffIds Ա��IDS
	 * @param startDate ��ʼ����
	 * @param endDate ��������
	 * @return
	 */
	public List<Object[]> findVisitPlan(List<String> staffIds,String startDate,String endDate){
		String hql = "from WqVisitPlan p,WqClientInfo c where p.clientId=c.id and p.visitDate>=? and p.visitDate<=? and "+SqlStringUtil.formatListToSQLIn("p.staffId", staffIds, true) + " order by p.visitDate";
		return this.find(hql, startDate,endDate);
	}
	
	/**
	 * �����ڶ��ڣ�����ָ��Ա���İݷüƻ�,ͬʱ����Ӧ�İݷÿͻ�Ҳ�����
	 * @param staffIds Ա��IDS
	 * @param startDate ��ʼ����
	 * @param endDate ��������
	 * @return
	 */
	public List<Object[]> findVisitClient(String staffId,String startDate,String endDate){
		String hql = "from WqVisitPlan p,WqClientInfo c where p.clientId=c.id and p.visitDate>=? and p.visitDate<=? and p.staffId=? order by p.visitDate";
		return this.find(hql, startDate,endDate,staffId);
	}

	/**
	 * �����ڶ��ڣ�����ָ��Ա���İݷüƻ�,ͬʱ����Ӧ�İݷÿͻ�Ҳ�����
	 * @param staffIds Ա��IDS
	 * @param startDate ��ʼ����
	 * @param endDate ��������
	 * @return
	 */
	public List<Object[]> findVisitClient(List<String> staffIds,String startDate,String endDate){
		String hql = "from WqVisitPlan p,WqClientInfo c where p.clientId=c.id and p.visitDate>=? and p.visitDate<=? and "+SqlStringUtil.formatListToSQLIn("p.staffId", staffIds, true) + " order by p.visitDate";
		return this.find(hql, startDate,endDate);
	}
	
	/**
	 * �����ڶ��ڣ�����ָ��Ա���İݷüƻ�,ͬʱ����Ӧ�İݷÿͻ�Ҳ�����
	 * @param staffIds Ա��IDS
	 * @param startDate ��ʼ����
	 * @param endDate ��������
	 * @return
	 */
	public List<Object[]> findVisitPlan(String staffId,String startDate,String endDate){
		String hql = "from WqVisitPlan p,WqClientInfo c where p.clientId=c.id and p.visitDate>=? and p.visitDate<=? and p.staffId=? order by p.visitDate";
		return this.find(hql, startDate,endDate,staffId);
	}
	
}

