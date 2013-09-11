package org.sunleads.module.location.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.sunleads.common.dao.HibernateDAO;
import org.sunleads.common.entity.DcGpsHis;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqPlanLocate;
import org.sunleads.common.entity.WqPlanLocateDetail;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.DateUtil;
import org.sunleads.common.util.GvphUtil;
import org.sunleads.common.util.SqlStringUtil;

/**
 * ��λ��ϢDAO
 * @author admin
 *
 */
@Component
public class LocationDAO extends HibernateDAO<Object, String>{

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	/**
	 * ����Ա��ID�б��ȡ��������б�
	 * @param staffIdList
	 * @param nowDateTime
	 * @return
	 * @throws Exception
	 */
	public Set<String> findRegionVisitByStaffIdList(List<String> staffIdList,String nowDateTime) throws Exception{
		StringBuffer sqlBuffer = new StringBuffer("SELECT T.STAFF_ID FROM WQ_REGION_VISIT T WHERE T.ENTER_TIME>=? ");
		sqlBuffer.append(SqlStringUtil.getInSqlByStringList("T.STAFF_ID", staffIdList));
		Set<String> visitSet = new HashSet<String>();
		List<Map<String,Object>> visitList = jdbcTemplate.queryForList(sqlBuffer.toString(),new Object[]{nowDateTime});
		for(Map<String,Object> map :visitList){
			String staffId = (String)map.get("STAFF_ID");
			visitSet.add(staffId);
		}
		return visitSet;
	}
	
	/**
	 * ����ʷ��λ������Ա��ID�����Լ�ʱ���ȡ����ʱ������ӽ���ʱ���Ա����ʷ����
	 * �����߼�����ȡѡ�е��������ʱ����㵽������֮�����м�¼��Ȼ��Ƚϻ�ȡ��ӽ���ʱ��ļ�¼
	 * @param staffIdList
	 * @param dateTime
	 * @return
	 * @throws Exception
	 */
	public List<DcGpsHis> findNearDataGpsHisByDateTime(List<String> staffIdList,String dateTime) throws Exception{
		String selectDateTime = dateTime+":59";//��ȡ����ʱ����
		String selectDate = DateUtil.formatDateStr(selectDateTime,DateUtil.DEFAULT_DATE_FORMAT);//��ȡ����
		String beginDateTimeNum = DateUtil.dateStrToBeginDateNumber(selectDate);//��ȡ���ڿ�ʼʱ��
		String endDateTimeNum = DateUtil.dateStrToEndDateNumber(selectDate);//��ȡ���ڽ���ʱ��
		
		List<Map<String,Object>> tmpList = GvphUtil.findAllGvphList(staffIdList, beginDateTimeNum, endDateTimeNum, jdbcTemplate);
		
		//��ѯ���Ľ����ֵ Ա����->��ʷ��¼
		Map<String,List<DcGpsHis>> staffIdDataMap = new HashMap<String,List<DcGpsHis>>();
		for(Map<String,Object> map:tmpList){
			DcGpsHis dcGpsHis = new DcGpsHis();
			DcGpsHis.createModel(map, dcGpsHis);
			String staffId = dcGpsHis.getTmnAlias();
			if(staffIdDataMap.containsKey(staffId)){
				List<DcGpsHis> list = staffIdDataMap.get(staffId);
				list.add(dcGpsHis);
			}else{
				List<DcGpsHis> list = new ArrayList<DcGpsHis>();
				list.add(dcGpsHis);
				staffIdDataMap.put(staffId, list);
			}
		}
		WqCompanyInfo company = AppUtil.getSessionData().getCompany();
		Boolean flag = company.getUseInvalidLoc();
		
		//�������սӽ���ѯʱ��Ľ����
		List<DcGpsHis> resultList = new ArrayList<DcGpsHis>();
		for(String staffId:staffIdList){
			List<DcGpsHis> tList = staffIdDataMap.get(staffId);
			if(tList!=null && !tList.isEmpty()){
				List<DcGpsHis> validList = new ArrayList<DcGpsHis>();
				for(DcGpsHis dcGpsHis:tList){
					//�����˾��ʹ����Ч��λ������Ч��λ���˵�
					if(flag!=true && dcGpsHis.getIsValidGps()!=true){
						continue;
					}
					if(dcGpsHis.getLat()!=null && dcGpsHis.getLat().doubleValue()>0){
						validList.add(dcGpsHis);
					}else if(dcGpsHis.getLatOffset()!=null && dcGpsHis.getLatOffset().doubleValue()>0){
						validList.add(dcGpsHis);
					}
				}
				long min = 10000000000l;
				DcGpsHis nearDcGpsHis = null;
				for(DcGpsHis dcGpsHis:validList){
					long curValue = Math.abs(DateUtil.getDateSCDiff(selectDateTime,dcGpsHis.getReportTime()));
					if(curValue < min){
						min = curValue;
						nearDcGpsHis = dcGpsHis;
					}
				}
				resultList.add(nearDcGpsHis);
			}
		}
		return resultList;
	}
	
	/**
	 * (�ƻ���λ)���ݵ�¼�û���ȡ�ƻ���λ����
	 * @return
	 * @throws Exception
	 */
	public List<WqPlanLocate> findWqPlanLocateByUserId(String userId)throws Exception{
		String hql = "from WqPlanLocate w where w.userId =? order by w.planLocateTime";
		List<WqPlanLocate> resultList = this.find(hql, userId);
		return resultList;
	}
	
	/**
	 * (�ƻ���λ)���ݼƻ���λID��ȡ��ϸ��λ��Ϣ
	 * @param planId
	 * @return
	 */
	public List<WqPlanLocateDetail> findWqPlanLocateDetailByPlanId(String planId)throws Exception{
		String hql = "from WqPlanLocateDetail w where w.planLocateId = ?";
		return find(hql,planId);
	}
	
	/**
	 * ����ʷ�켣������Ա��ID��ȡ��ʷ�켣
	 * @param staffId
	 * @param beginDateTime
	 * @param endDateTime
	 * @return
	 */
	public List<DcGpsHis> findDataGpsHisByCondition(String staffId,String beginDateTime,String endDateTime)throws Exception {
		List<Map<String,Object>> tmpList = GvphUtil.findAllGvphList(staffId, beginDateTime, endDateTime, jdbcTemplate);

		List<DcGpsHis> resultList = new ArrayList<DcGpsHis>();
		for(Map<String,Object> map:tmpList){
			DcGpsHis dcGpsHis = new DcGpsHis();
			DcGpsHis.createModel(map, dcGpsHis);
			resultList.add(dcGpsHis);
		}
		return resultList;
	}

	/**
	 * ��ѯԱ������ʷ�ֻ�״̬
	 * @param staffId
	 * @param beginDateTime
	 * @param endDateTime
	 * @return
	 */
	public List<Map<String,Object>> findPhoneStateHis(String staffId,String beginDateTime,String endDateTime){
		String sql = "SELECT RPT_TIME,STTS FROM WQ_TMN_STTS_HIS where STAFF_ID=? AND RPT_TIME>=? AND RPT_TIME<=? ORDER BY RPT_TIME";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,staffId,beginDateTime,endDateTime);
		return list;
	}
}
