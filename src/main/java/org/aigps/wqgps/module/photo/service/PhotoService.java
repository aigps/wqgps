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
* @Title����Ƭ�鿴Service
* @Description��<������>
*
* @author qixianping
* @version 1.0
*
* Create Date��  2012-5-16����09:36:58
* Modified By��  <�޸�����������ƴ����д>
* Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
*
* Copyright��Copyright(C),1995-2011 ��IPC��09004804��
* Company������Ԫ��Ƽ����޹�˾
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
	 * ͳ����Ƭ
	 * @param staffId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<WqPicHis> statisStaffPhotoList(String staffId,String startDate,String endDate){
		return photoDAO.statisStaffPhotoList(staffId, startDate, endDate);
	}
	
	/**
	 * ��ѯ����Ա������Ƭ
	 * @param vhcCode
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<WqPicHis>  seachStaffPhotoList(String staffId,String startDate,String endDate){
		return photoDAO.seachStaffPhotoList(staffId, startDate, endDate);
	}
	
	/**
	 * ��ѯ��Ƭ�ֽ�����
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
