package org.aigps.wqgps.module.location.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aigps.wqgps.common.dao.HibernateDAO;
import org.aigps.wqgps.common.entity.DcGpsHis;
import org.aigps.wqgps.common.entity.WqCompanyInfo;
import org.aigps.wqgps.common.entity.WqPlanLocate;
import org.aigps.wqgps.common.entity.WqPlanLocateDetail;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.common.util.GvphUtil;
import org.aigps.wqgps.common.util.SqlStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 定位信息DAO
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
	 * 根据员工ID列表获取区域访问列表
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
	 * （历史定位）根据员工ID集合以及时间获取当天时间内最接近该时间的员工历史数据
	 * 处理逻辑：获取选中的这个日期时间零点到结束点之间所有记录，然后比较获取最接近该时间的记录
	 * @param staffIdList
	 * @param dateTime
	 * @return
	 * @throws Exception
	 */
	public List<DcGpsHis> findNearDataGpsHisByDateTime(List<String> staffIdList,String dateTime) throws Exception{
		String selectDateTime = dateTime+":59";//获取日期时分秒
		String selectDate = DateUtil.formatDateStr(selectDateTime,DateUtil.DEFAULT_DATE_FORMAT);//获取日期
		String beginDateTimeNum = DateUtil.dateStrToBeginDateNumber(selectDate);//获取日期开始时间
		String endDateTimeNum = DateUtil.dateStrToEndDateNumber(selectDate);//获取日期结束时间
		
		List<Map<String,Object>> tmpList = GvphUtil.findAllGvphList(staffIdList, beginDateTimeNum, endDateTimeNum, jdbcTemplate);
		
		//查询到的结果键值 员工号->历史记录
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
		
		//构建最终接近查询时间的结果集
		List<DcGpsHis> resultList = new ArrayList<DcGpsHis>();
		for(String staffId:staffIdList){
			List<DcGpsHis> tList = staffIdDataMap.get(staffId);
			if(tList!=null && !tList.isEmpty()){
				List<DcGpsHis> validList = new ArrayList<DcGpsHis>();
				for(DcGpsHis dcGpsHis:tList){
					//如果公司不使用无效定位，把无效定位过滤掉
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
	 * (计划定位)根据登录用户获取计划定位集合
	 * @return
	 * @throws Exception
	 */
	public List<WqPlanLocate> findWqPlanLocateByUserId(String userId)throws Exception{
		String hql = "from WqPlanLocate w where w.userId =? order by w.planLocateTime";
		List<WqPlanLocate> resultList = this.find(hql, userId);
		return resultList;
	}
	
	/**
	 * (计划定位)根据计划定位ID获取详细定位信息
	 * @param planId
	 * @return
	 */
	public List<WqPlanLocateDetail> findWqPlanLocateDetailByPlanId(String planId)throws Exception{
		String hql = "from WqPlanLocateDetail w where w.planLocateId = ?";
		return find(hql,planId);
	}
	
	/**
	 * （历史轨迹）根据员工ID获取历史轨迹
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
	 * 查询员工的历史手机状态
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
