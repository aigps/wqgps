package org.aigps.wqgps.module.business.service;

import java.util.List;

import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.WqMood;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class MoodService {
	public final static Log log = LogFactory.getLog(MoodService.class);
	
	private PublicDAO publicDAO;
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	
	public List<WqMood> getMoodByTime(String startTime,String endTime) throws Exception{
		startTime = startTime + ":00";
		endTime = endTime + ":59";
		try{
			String companyId = AppUtil.getSessionData().getCompany().getId();
			return publicDAO.find("from WqMood where msgTime>=? and msgTime<=? and companyId=? order by msgTime", startTime,endTime,companyId);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
}
