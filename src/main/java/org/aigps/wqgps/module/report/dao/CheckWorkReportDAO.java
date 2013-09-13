
package org.aigps.wqgps.module.report.dao;

import java.util.List;

import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.WqTravelPlan;
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
public class CheckWorkReportDAO extends HibernateDAO<Object, String>{

	//��ȡ��˾��ʱ���startTime��endTime�����ƵĲ��üƻ�
	public List<WqTravelPlan> findCompanyTravelPlan(String companyId,String startDate,String endDate){
		startDate = startDate+" 00:00:00";
		endDate = endDate+" 23:59:59";
		String hql = "from WqTravelPlan where companyId=? and ((startTime>=? and endTime<=?) or (startTime<=? and endTime>=?) or (startTime<=? and endTime>=?))";
		return this.find(hql, companyId, startDate, endDate, startDate, startDate, endDate, endDate);
	}
	
}

