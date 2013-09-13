package org.aigps.wqgps.module.photo.service;

import java.util.List;

import org.aigps.wqgps.common.entity.WqPicHis;
import org.aigps.wqgps.common.util.DataCompressUtil;
import org.aigps.wqgps.module.photo.dao.PhotoDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
* @Title：照片查看Service
* @Description：<类描述>
*
* @author qixianping
* @version 1.0
*
* Create Date：  2012-5-16上午09:36:58
* Modified By：  <修改人中文名或拼音缩写>
* Modified Date：<修改日期，格式:YYYY-MM-DD>
*
* Copyright：Copyright(C),1995-2011 浙IPC备09004804号
* Company：杭州元码科技有限公司
 */
@Component
@Transactional
@SuppressWarnings("unchecked")
public class PhotoService {
	public final static Log log = LogFactory.getLog(PhotoService.class);
	
	private PhotoDAO photoDAO; 
	@Autowired
	public void setPhotoDao(PhotoDAO photoDAO){
		this.photoDAO = photoDAO;
	}
	
	/**
	 * 统计照片
	 * @param staffId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<WqPicHis> statisStaffPhotoList(String staffId,String startDate,String endDate){
		return photoDAO.statisStaffPhotoList(staffId, startDate, endDate);
	}
	
	/**
	 * 查询单个员工的照片
	 * @param vhcCode
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<WqPicHis>  seachStaffPhotoList(String staffId,String startDate,String endDate){
		return photoDAO.seachStaffPhotoList(staffId, startDate, endDate);
	}
	
	/**
	 * 查询照片字节内容
	 * @param vhcCode
	 * @param startDate
	 * @param endDate
	 * @param photoState
	 * @return
	 */
	public Object photoContentList(String staffId,String startDate,String endDate){
		return DataCompressUtil.compress(photoDAO.photoContentList(staffId, startDate, endDate));
	}

}
