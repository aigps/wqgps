package org.sunleads.module.sms.meilin;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MeiLinSmsServer {// 润豪服务器
	private static final Log log = LogFactory.getLog(MeiLinSmsServer.class);
	private static String name = "zhongdao_admin";
	private static String pwd = "123456";// "817538";
	private static final String smsHead = "http://www.phone366.com/WebService/OutSMSService.asmx";

	private static MeiLinSmsServer server;

	public static MeiLinSmsServer getInstance() throws Exception {
		if (server == null) {
			server = new MeiLinSmsServer();
		}
		return server;
	}

	public boolean sendMsg7bit(String fReceivers, String fContent) throws Exception {
		return sendMsg7bit("0","00",fReceivers,fContent);
	}

	public boolean sendMsg7bit(String fNeedRead,	//是否需要阅读回执：0不需要，1需要
								String fTakeKey,	//短信提取码：用于定位更新本地短信的阅读标记，一般来说是短信记录的主键（fNeedRead=0时忽略此参数，直接传入空值）
								String fReceivers,	//短信接收人
								String fContent		//短信正文内容
								) throws Exception {
		// .net webService 命名空间
		String namespace = "http://tempuri.org/";
		// .net webService 需调用的方法
		String methodName = "N3SendSMS1";
		String soapActionURI = "http://tempuri.org/N3SendSMS1";
		Service service = new Service();

		Call call = (Call) service.createCall();

		call.setTargetEndpointAddress(new java.net.URL(smsHead));
		call.setUseSOAPAction(true);
		// 这个地方没设对就会出现Server was unable to read request的错误
		call.setSOAPActionURI(soapActionURI);
		// 设置要调用的.net webService方法
		call.setOperationName(new QName(namespace, methodName));

		call.addParameter(new QName(namespace, "fNeedRead"),
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);

		call.addParameter(new QName(namespace, "fNeedTake"),
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);
		
		call.addParameter(new QName(namespace, "fTakeKey"),
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);

		call.addParameter(new QName(namespace, "fSignature"),
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);
		call.addParameter(new QName(namespace, "fContent"),
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);

		call.addParameter(new QName(namespace, "fReceivers"),
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);
		call.addParameter(new QName(namespace, "fUserName"),
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);

		call.addParameter(new QName(namespace, "fPassword"),
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);
		// 设置该方法的返回值
		call.setReturnType(XMLType.XSD_STRING);

		String ret = (String) call.invoke(new Object[] { fNeedRead, "1" , fTakeKey, "宁波图腾",
				fContent, fReceivers + ";", name, pwd });
		log.error("返回结果---> " + ret);
		if (ret.indexOf("OK") != -1) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		try {
			MeiLinSmsServer server = MeiLinSmsServer.getInstance();
			server.sendMsg7bit("1","01","15157102348", "测试信息");
			// server.getFetchRead();
		} catch (Exception e) {
			log.error("", e);
		}
	}
}
