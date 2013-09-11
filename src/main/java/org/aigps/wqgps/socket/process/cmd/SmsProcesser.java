package org.sunleads.socket.process.cmd;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.ym.model.YmAccessMsg;
import org.gps.ym.model.YmCmdModel;
import org.springframework.security.core.codec.Base64;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.dao.PublicService;
import org.sunleads.common.entity.WqCompanyInfo;
import org.sunleads.common.entity.WqEmergency;
import org.sunleads.common.entity.WqStaffInfo;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.DateUtil;
import org.sunleads.module.sms.smgp.ISmsSender;

/**
 * �ظ��·�ָ�����
 * @author admin
 *
 */

public class SmsProcesser {

	protected static final Log log = LogFactory.getLog(SmsProcesser.class);

	public static void process(YmAccessMsg accessMsg) {
		if(StringUtils.isBlank(accessMsg.getData())){
			return;
		}
		try{
			String[] contentArr = new YmCmdModel(accessMsg).getCmdParams();
			if(contentArr!=null && contentArr.length>=2){
				String msid = accessMsg.getDeviceCode();//�ֻ��ż��ն˺�
				String smsType = contentArr[0];//��Ϣ����
				String smsContent = contentArr[1];
				smsContent = new String(Base64.decode(smsContent.getBytes()));////��Ϣ����
				log.error("==================�յ����ݣ�"+smsContent+" "+smsType+"  "+msid);
				
				if("2".equals(smsType)){//�����¼�
					WqStaffInfo staff = null;
					for(WqStaffInfo s : DataCache.staffMap.values()){
						if(msid.equals(s.getMsid())){
							staff = s;
							break;
						}
					}
					if(staff == null){
						log.error("==================û�ҵ�MSID�Ŷ�Ӧ��Ա����"+msid);
						return;
					}
					WqCompanyInfo company = DataCache.companyInfoMap.get(staff.getCompanyId());
					if(company == null || StringUtils.isBlank(company.getContactNumber())){
						log.error("==================û�ҵ�Ա����Ӧ�Ĺ�˾��"+staff.getCompanyId());
						return;
					}

					ISmsSender smsSender = (ISmsSender) AppUtil.getBean((String)AppUtil.getBean("smsSender"));

					WqEmergency model = new WqEmergency();
					model.setCompanyId(company.getId());
					model.setMessage(smsContent);
					model.setMsgTime(DateUtil.getCurDate());
					model.setSendTo(company.getContactNumber());
					model.setStaffId(staff.getId());
					model.setSendState("0");
					
					PublicService service = (PublicService)AppUtil.getBean("publicService");
					service.save(model);

					log.error("==================����4");
					try{
						boolean sendState=smsSender.send(new String[]{company.getContactNumber()}, smsContent,"00","8");
						model.setSendState(sendState?"1":"2");
					}catch(Exception e){
						model.setSendState("2");
					}
					service.save(model);
				}else if("1".equals(smsType)){//�������
					
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}
}
