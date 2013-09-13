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
public class DxSmsSender extends SMGPSMProxy implements ISmsSender{
	public final static Log log = LogFactory.getLog(DxSmsSender.class);

	// ϵͳ������Ϣ
	private static Args conArgs = Env.getConfig().getArgs("SMGPConnect");

	// ��Ϣ������Ϣ
	private static Args msgArgs = Env.getMsgConfig().getArgs("SMGPMessage");

	private static DxSmsSender instance;
	
	long startNum=0;
	long endNum=0;

	public static DxSmsSender getInstance() {
		if (instance == null) {
			instance = new DxSmsSender();
		}
		return instance;
	}

	protected DxSmsSender() {
		super(DxSmsSender.conArgs);
	}

	/**
	 * ����InfoX�����ӱ��ж�ʱ�Ĵ���
	 */
	public void OnTerminate() {
		System.out.println("Connection have been breaked! ");
	}

	/**
	 * ��SMGW�����·�����Ϣ�Ĵ���������ֻ����һ���ɹ�����Ӧ��
	 * 
	 * @param msg
	 *            �յ�����Ϣ��
	 * @return ���ص���Ӧ��Ϣ��
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
	 * ����һ����Ϣ�������������Ϣ���͡�
	 * 
	 * @param revicePhone
	 *            ����Ϣ���պ��룬С��100���ַ�
	 * @param msgContent
	 *            ����Ϣ����
	 * @return true�����ͳɹ���false������ʧ�ܡ�
	 */
	public boolean send(String[] recivePhone, String msgContent,String sendType,String smsformat) {
		//��������
		SMGPSubmitMessage msg = new SMGPSubmitMessage(Integer.parseInt(msgArgs
				.get("msgType", "9")), // ����Ϣ������С��255
				Integer.parseInt(msgArgs.get("needReport", "1")), // �Ƿ�Ҫ�󷵻�״̬���棬С��255
				Integer.parseInt(msgArgs.get("priority", "9")), // ���͵����ȼ���С��9
				msgArgs.get("serviceId", "goodnews13"), // ҵ������
				msgArgs.get("feeType", "01"), // �ʷ����ͣ�С��2���ַ�
				msgArgs.get("feeCode", "999"), // �ʷѴ��룬С��6���ַ�
				msgArgs.get("fixedFee", ""), // ���·�
				Integer.parseInt(smsformat), // ����Ϣ��ʽ��С��255
				msgArgs.get("validTime", ""), // "010722110300032+",��Чʱ��
				msgArgs.get("atTime", ""), // "010722110300032+",����ʱ��
				msgArgs.get("srcTermId", "1065901020039"), // "15384061450", ����Ϣ�����û����룬С��21���ַ�
				"", // �Ʒ��û����룬С��21���ַ�
				recivePhone, // ����Ϣ���պ��룬С��100���ַ�
				msgContent, // ����Ϣ����
				""); // �����ֶ�
		try {
			startNum=new Date().getTime();
			
			SMGPSubmitRespMessage reportMsg = (SMGPSubmitRespMessage) super.send(msg);
			log.info("-------���Ͷ���--------");
			log.info("�������ͣ�"+(sendType.equals("00")?"��ͨ����":"ָ�����"));
			String revicePhoneStr="";
			for(int tempIndex=0;tempIndex<recivePhone.length;tempIndex++){
				revicePhoneStr=revicePhoneStr+recivePhone[tempIndex]+",";
			}
			log.info("���ն��ź��룺"+revicePhoneStr);
			
			System.out.println("reuestId----------------"+reportMsg.getMsgId());		
			if(reportMsg!=null){
				System.out.println("Get SubmitResp Message Success! The status = "+ reportMsg.getStatus());	
			}
		} catch (Exception e) {
			log.error("", e);
			return false;
		}


		return true;
	}

	public boolean send(String fNeedRead, String fTakeKey, String fReceivers,
			String fContent) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static void main(String[] args) {
		new DxSmsSender().send(new String[] { "15384061450" }, "this is a message","","8");
	}

}
