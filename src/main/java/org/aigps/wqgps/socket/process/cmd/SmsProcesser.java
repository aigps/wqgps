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
 * 回复下发指令处理器
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
				String msid = accessMsg.getDeviceCode();//手机号即终端号
				String smsType = contentArr[0];//消息类型
				String smsContent = contentArr[1];
				smsContent = new String(Base64.decode(smsContent.getBytes()));////消息内容
				log.error("==================收到内容："+smsContent+" "+smsType+"  "+msid);
				
				if("2".equals(smsType)){//紧急事件
					WqStaffInfo staff = null;
					for(WqStaffInfo s : DataCache.staffMap.values()){
						if(msid.equals(s.getMsid())){
							staff = s;
							break;
						}
					}
					if(staff == null){
						log.error("==================没找到MSID号对应的员工："+msid);
						return;
					}
					WqCompanyInfo company = DataCache.companyInfoMap.get(staff.getCompanyId());
					if(company == null || StringUtils.isBlank(company.getContactNumber())){
						log.error("==================没找到员工对应的公司："+staff.getCompanyId());
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

					log.error("==================进：4");
					try{
						boolean sendState=smsSender.send(new String[]{company.getContactNumber()}, smsContent,"00","8");
						model.setSendState(sendState?"1":"2");
					}catch(Exception e){
						model.setSendState("2");
					}
					service.save(model);
				}else if("1".equals(smsType)){//心情短语
					
				}
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
	}
}
