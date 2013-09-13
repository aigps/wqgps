package org.aigps.wqgps.module.photo.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.entity.WqPicHis;
import org.aigps.wqgps.common.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 
 * @Title：照片查看DAO
 * @Description：<类描述>
 * 
 * @author qixianping
 * @version 1.0
 * 
 *          Create Date： 2012-5-16上午09:34:53 Modified By： <修改人中文名或拼音缩写> Modified
 *          Date：<修改日期，格式:YYYY-MM-DD>
 * 
 *          Copyright：Copyright(C),1995-2011 浙IPC备09004804号 Company：杭州元码科技有限公司
 */
@Component
public class PhotoDAO {
	public final static Log log = LogFactory.getLog(PhotoDAO.class);

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	/**
	 * 统计照片
	 * @param staffIds
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<WqPicHis> statisStaffPhotoList(String staffIds, String startDate,
			String endDate) {
		List<WqPicHis> picHisList = new ArrayList<WqPicHis>();
		List<Map<String, Object>> photoMap = new ArrayList<Map<String, Object>>();
		String[] staffIdArr = staffIds.split(",");

		if (staffIdArr.length > 300) {
			StringBuffer staffIdSb=new StringBuffer();
			
			for(int tempIndex=0;tempIndex<staffIdArr.length;tempIndex++){
				staffIdSb.append(staffIdArr[tempIndex]+",");
				if(tempIndex==staffIdArr.length-1){
					getPhotoList(photoMap,staffIdSb.toString().substring(0, staffIdSb.length()-1),startDate,endDate);
					break;
				}
				if(tempIndex!=0&&(tempIndex+1)%300==0){
					getPhotoList(photoMap,staffIdSb.toString().substring(0, staffIdSb.length()-1),startDate,endDate);
					staffIdSb.delete(0, staffIdSb.length());
				}
			}
		} else {
			getPhotoList(photoMap, staffIds, startDate, endDate);
		}
		
		WqPicHis model = null;
		for(Map map:photoMap){
			model = new WqPicHis();
			model.setPhone(map.get("phone")==null?"":map.get("phone").toString());
			model.setStandby1((map.get("photoCount")==null?"":map.get("photoCount").toString()));
			picHisList.add(model);
		}

		return picHisList;
	}

	public void getPhotoList(List<Map<String, Object>> photoMap,
			String staffId, String startDate, String endDate) {
		StringBuffer sql = new StringBuffer();
		sql.append("select phone,count(*) as photoCount from WQ_PIC_HIS ");
		sql.append("where STAFF_ID in(" + staffId + ") ");
		try {
			sql.append("and PIC_TIME >=" + DateUtil.dateToLong(startDate)+" ");
			sql.append("and PIC_TIME <=" + DateUtil.dateToLong(endDate) + " ");

			sql.append(" group by phone");
		} catch (Exception e) {
			log.error("", e);
		}
		
		try {
			photoMap.addAll(this.jdbcTemplate.queryForList(sql.toString()));
		} catch (DataAccessException e) {
			log.error("", e);
		}
	}
	
	/**
	 * 查询单个员工的照片
	 * @param vhcCode
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<WqPicHis> seachStaffPhotoList(String phone,String startDate,String endDate){
		StringBuffer sql=new StringBuffer();
		sql.append("select STAFF_ID,PHONE,PIC_DESC,PIC_TIME,PIC_NAME from WQ_PIC_HIS ");
		sql.append("where PHONE='"+phone+"'");
		try {
			sql.append("and PIC_TIME>='"+DateUtil.dateToLong(startDate)+"' ");
			sql.append("and PIC_TIME<='"+DateUtil.dateToLong(endDate)+"' ");
		} catch (Exception e) {
			log.error("", e);
		}

		sql.append(" order by PIC_TIME desc");
		List<Map<String,Object>> photoMap=null;
		try {
			photoMap=this.jdbcTemplate.queryForList(sql.toString());
		} catch (DataAccessException e) {
			log.error("", e);
		}
		List<WqPicHis> staffPhotoList=new ArrayList<WqPicHis>();
		WqPicHis model = null;
		for(Map map:photoMap){
			model = new WqPicHis();
			model.setPhone(map.get("PHONE")==null?"":map.get("PHONE").toString());
			//model.setPicData(map.get("PIC_DATA")==null?"":map.get("PIC_DATA").toString());
			model.setPicDesc(map.get("PIC_DESC")==null?"":map.get("PIC_DESC").toString());
			model.setPicName(map.get("PIC_NAME")==null?"":map.get("PIC_NAME").toString());
			model.setPicTime(map.get("PIC_TIME")==null?"":DateUtil.dateToString(DateUtil.stringToDate(map.get("PIC_TIME").toString(), DateUtil.DEFAULT_ALL_DATE_TIME_FORMAT), DateUtil.DEFAULT_DATETIME_FORMAT));
			model.setStaffId(map.get("STAFF_ID")==null?"":map.get("STAFF_ID").toString());
			//model.setStandby1(map.get("STANDBY1")==null?"":map.get("STANDBY1").toString());
			//model.setStandby2(map.get("STANDBY2")==null?"":map.get("STANDBY2").toString());

			staffPhotoList.add(model);
		}
		return staffPhotoList;
	}
	
	/**
	 * 查询照片字节内容
	 * @param vhcCode
	 * @param startDate
	 * @param endDate
	 * @param photoState
	 * @return
	 */
	public List<Object> photoContentList(String phone,String startDate,String endDate){
		List<Object> staffPhotoList=new ArrayList<Object>();
		
		//从数据库去照片数据
		StringBuffer sql=new StringBuffer();
		sql.append("select PIC_DATA from WQ_PIC_HIS ");
		sql.append("where phone='"+phone+"' ");
		try {
			sql.append(" and PIC_TIME>="+DateUtil.dateToLong(startDate) );
			sql.append(" and PIC_TIME<="+DateUtil.dateToLong(endDate));
		} catch (Exception e) {
			log.error("", e);
		}

		sql.append(" order by PIC_TIME desc");
		List<Map<String,Object>> photoMap=null;
		try {
			photoMap=this.jdbcTemplate.queryForList(sql.toString());
		} catch (DataAccessException e) {
			log.error("", e);
		}
		for(Map map:photoMap){
			staffPhotoList.add(map.get("PIC_DATA")==null?new Object():map.get("PIC_DATA"));
		}
		return staffPhotoList;
	}
}
