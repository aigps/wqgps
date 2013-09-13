package org.aigps.wqgps.module.sms.smgp;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.SmsModel;
import org.aigps.wqgps.common.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.huawei.smproxy.SMGPSMProxy;
import com.huawei.smproxy.comm.smgp.message.SMGPDeliverMessage;
import com.huawei.smproxy.comm.smgp.message.SMGPDeliverRespMessage;
import com.huawei.smproxy.comm.smgp.message.SMGPMessage;
import com.huawei.smproxy.comm.smgp.message.SMGPSubmitMessage;
import com.huawei.smproxy.comm.smgp.message.SMGPSubmitRespMessage;
import com.huawei.smproxy.util.Args;

@Component
public class SmsCmdHandle extends SMGPSMProxy{
	public final static Log log = LogFactory.getLog(SmsCmdHandle.class);

	// 系统配置信息
	private static Args conArgs = Env.getCmdConfig().getArgs("SMGPConnect");

	// 消息配置信息
	private static Args msgArgs = Env.getCmdMsgConfig().getArgs("SMGPMessage");

	private static SmsCmdHandle instance;
	
	long startNum=0;
	
	long endNum=0;

	public static SmsCmdHandle getInstance() {
		if (instance == null) {
			instance = new SmsCmdHandle();
		}
		return instance;
	}

	protected SmsCmdHandle() {
		super(SmsCmdHandle.conArgs);
	}

	/**
	 * 当与InfoX的连接被中断时的处理
	 */
	public void OnTerminate() {
		System.out.println("Connection have been breaked! ");
	}

	/**
	 * 对SMGW主动下发的消息的处理。此例中只返回一个成功的响应。
	 * 
	 * @param msg
	 *            收到的消息。
	 * @return 返回的相应消息。
	 */
	public SMGPMessage onDeliver(final SMGPDeliverMessage msg) {
		endNum=new Date().getTime();
		System.out.println("val:"+(endNum-startNum));
		System.out.println("responseId----------------"+msg.getMsgId());
		if(msg.getIsReport()==0){
			SmsModel smsModel=new SmsModel();
			if(msg.getSrcTermID().substring(0, 2).equalsIgnoreCase("86")){
				smsModel.setTel(msg.getSrcTermID().substring(2));
			}else if(msg.getSrcTermID().substring(0, 3).equalsIgnoreCase("+86")){
				smsModel.setTel(msg.getSrcTermID().substring(3));
			}else{
				smsModel.setTel(msg.getSrcTermID());
			}
			
			smsModel.setTime(DateUtil.dateToString(msg.getRecvTime(),DateUtil.DEFAULT_DATETIME_FORMAT));
			if(msg.getMsgFormat()==8){
				try {
					smsModel.setMessage(new String(msg.getMsgContent(),"UnicodeBigUnmarked"));
				} catch (UnsupportedEncodingException e) {
					log.error("", e);
				}
			}else{
				smsModel.setMessage(new String(msg.getMsgContent()));
			}
			
			List<SmsModel> smsModelList=DataCache.smsDeliverMap.get(smsModel.getTel());
			if(smsModelList==null){
				smsModelList=new ArrayList<SmsModel>();
				smsModelList.add(smsModel);
				DataCache.smsDeliverMap.put(smsModel.getTel(), smsModelList);
			}else{
				smsModelList.add(smsModel);
			}
		}

		return new SMGPDeliverRespMessage(msg.getMsgId(), 0);
		
	}

	/**
	 * 发送一条消息，完成真正的消息发送。
	 * 
	 * @param revicePhone
	 *            短消息接收号码，小于100个字符
	 * @param msgContent
	 *            短消息内容
	 * @return true：发送成功。false：发送失败。
	 */
	public boolean send(String[] recivePhone, String msgContent,String smsformat) {
		
//		String msgUUId=SqlStringUtil.getUuidSequence();
		SMGPSubmitMessage msg = new SMGPSubmitMessage(Integer.parseInt(msgArgs
				.get("msgType", "9")), // 短消息，类型小于255
				Integer.parseInt(msgArgs.get("needReport", "1")), // 是否要求返回状态报告，小于255
				Integer.parseInt(msgArgs.get("priority", "9")), // 发送的优先级，小于9
				msgArgs.get("serviceId", "goodnews13"), // 业务类型
				msgArgs.get("feeType", "01"), // 资费类型，小于2个字符
				msgArgs.get("feeCode", "999"), // 资费代码，小于6个字符
				msgArgs.get("fixedFee", ""), // 包月费
				Integer.parseInt(smsformat), // 短消息格式，小于255
				msgArgs.get("validTime", ""), // "010722110300032+",有效时间
				msgArgs.get("atTime", ""), // "010722110300032+",发送时间
				msgArgs.get("srcTermId", "1065901020039"), // "15384061450", 短消息发送用户号码，小于21个字符
				"", // 计费用户号码，小于21个字符
				recivePhone, // 短消息接收号码，小于100个字符
				msgContent, // 短消息内容
				""); // 保留字段
			
		try {
			
			startNum=new Date().getTime();
			
			SMGPSubmitRespMessage reportMsg = (SMGPSubmitRespMessage) super.send(msg);

			String revicePhoneStr="";
			for(int tempIndex=0;tempIndex<recivePhone.length;tempIndex++){
				revicePhoneStr=revicePhoneStr+recivePhone[tempIndex]+",";
			}
			log.info("接收短信号码："+revicePhoneStr);
			
			System.out.println("reuestId----------------"+reportMsg.getMsgId());		
			if(reportMsg!=null){
				System.out.println("Get SubmitResp Message Success! The status = "+ reportMsg.getStatus());	
			}
		} catch (Exception e) {
			log.error("", e);
			return false;
		}
//		try {
//			Thread.sleep(1000*60);
//		} catch (InterruptedException e) {
//			log.error("", e);
//		}
		return true;
	}
}
