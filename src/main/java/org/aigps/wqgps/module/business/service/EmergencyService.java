package org.sunleads.module.business.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.entity.WqMood;
import org.sunleads.common.util.AppUtil;

@Component
@Transactional
public class EmergencyService {
	public final static Log log = LogFactory.getLog(EmergencyService.class);
	
	private PublicDAO publicDAO;
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	
	public List<WqMood> getByTime(String startTime,String endTime) throws Exception{
		startTime = startTime + ":00";
		endTime = endTime + ":59";
		try{
			String companyId = AppUtil.getSessionData().getCompany().getId();
			return publicDAO.find("from WqEmergency where msgTime>=? and msgTime<=? and companyId=? order by msgTime", startTime,endTime,companyId);
		}catch (Exception e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
}
