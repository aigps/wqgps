package org.aigps.wqgps.module.sms.service;

import java.util.List;

import org.aigps.wqgps.common.dao.PublicDAO;
import org.aigps.wqgps.common.entity.WqSmsH;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.DateUtil;
import org.aigps.wqgps.module.sms.smgp.ISmsSender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.SqlStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SmsService{
	public final static Log log = LogFactory.getLog(SmsService.class);
	
	private PublicDAO publicDAO;
	@Autowired
	public void setPublicDAO(PublicDAO publicDAO) {
		this.publicDAO = publicDAO;
	}
	
	/**
	 * ͨ��ID������ʷ��¼
	 * @param smsId
	 * @return
	 */
	public WqSmsH findSmsById(String smsId){
		List<WqSmsH> smsList = publicDAO.findBy("id",smsId,WqSmsH.class);
		if(smsList.size() > 0){
			return smsList.get(0);
		}
		return null;
	}
	
	/**
	 * ����SMS��¼
	 * @param model
	 * @return
	 */
	public WqSmsH saveSms(WqSmsH model){
		publicDAO.save(model);
		return model;
	}

	/**
	 * ������ͨ����
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public boolean sendSms(List<WqSmsH> list) throws Exception{
		String[] recivePhone=new String[list.size()];
		String msgContent="";
		int tempIndex=0;
		for (WqSmsH entity : list) {
			recivePhone[tempIndex++]=entity.getPhone();
			msgContent=entity.getSmsContent();
		}
		boolean sendState = false;
		try {
			ISmsSender smsSender = (ISmsSender) AppUtil.getBean((String)AppUtil.getBean("smsSender"));
			sendState = smsSender.send(recivePhone, msgContent,"00","8");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		for (WqSmsH entity : list) {
			entity.setSendtime(DateUtil.getCurDateStr(DateUtil.DEFAULT_DATETIME_FORMAT));
			entity.setState(sendState?"01":"00");
			publicDAO.save(entity);
		}
		return sendState;
	}
	
	/**
	 * ������Ҫ��ִ�Ķ��ţ�ֻ�ܵ������ͣ�
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public boolean sendSmsForRead(List<WqSmsH> list) throws Exception{
		boolean isAllSend = true;
		for (WqSmsH entity : list) {
			boolean sendState = false;
			entity.setSendtime(DateUtil.getCurDateStr(DateUtil.DEFAULT_DATETIME_FORMAT));
			entity.setState("01");
			publicDAO.save(entity);
			
			ISmsSender smsSender = (ISmsSender) AppUtil.getBean((String)AppUtil.getBean("smsSender"));
			sendState = smsSender.send("1",entity.getId(),entity.getPhone(), entity.getSmsContent());
			if(!sendState){
				isAllSend = false;
			}
			entity.setState(sendState?"01":"00");

			return sendState;
		}
		return isAllSend;
	}
	
	public List<WqSmsH> findWqSmsH(String startDate,String endDate,List<String> staffIds,String type) throws Exception{
		StringBuilder hql = new StringBuilder("FROM WqSmsH where 1=1 and companyId=?");
		if(startDate !=null && !startDate.equals("")){
			startDate = startDate.concat(" 00:00:00");
			hql.append(" and sendTime >= '").append(startDate).append("'");
		}
		if(endDate !=null && !endDate.equals("")){
			endDate = endDate.concat(" 23:59:59");
			hql.append(" and sendTime <= '").append(endDate).append("'");
		}
		if(type !=null && !type.equals("")){
			hql.append(" and type <= '").append(type).append("'");
		}
		if(staffIds!=null && !staffIds.isEmpty()){
			hql.append(" and ").append(SqlStringUtil.formatListToSQLIn("staffid", staffIds, true));
		}
		
		WqUserInfo user = AppUtil.getUserInfo();
		if(!user.getIsAdmin()){
			hql.append(" and sender = '").append(user.getId()).append("'");
		}
		
		hql.append(" order by sendTime asc");
		List<WqSmsH> list = publicDAO.find(hql.toString(), user.getCompanyId());
		
		return list;
	}
}
