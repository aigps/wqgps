package org.aigps.wqgps.module.sms.meilin;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MeiLinSmsServer {// ���������
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

	public boolean sendMsg7bit(String fNeedRead,	//�Ƿ���Ҫ�Ķ���ִ��0����Ҫ��1��Ҫ
								String fTakeKey,	//������ȡ�룺���ڶ�λ���±��ض��ŵ��Ķ���ǣ�һ����˵�Ƕ��ż�¼��������fNeedRead=0ʱ���Դ˲�����ֱ�Ӵ����ֵ��
								String fReceivers,	//���Ž�����
								String fContent		//������������
								) throws Exception {
		// .net webService �����ռ�
		String namespace = "http://tempuri.org/";
		// .net webService ����õķ���
		String methodName = "N3SendSMS1";
		String soapActionURI = "http://tempuri.org/N3SendSMS1";
		Service service = new Service();

		Call call = (Call) service.createCall();

		call.setTargetEndpointAddress(new java.net.URL(smsHead));
		call.setUseSOAPAction(true);
		// ����ط�û��Ծͻ����Server was unable to read request�Ĵ���
		call.setSOAPActionURI(soapActionURI);
		// ����Ҫ���õ�.net webService����
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
		// ���ø÷����ķ���ֵ
		call.setReturnType(XMLType.XSD_STRING);

		String ret = (String) call.invoke(new Object[] { fNeedRead, "1" , fTakeKey, "����ͼ��",
				fContent, fReceivers + ";", name, pwd });
		log.error("���ؽ��---> " + ret);
		if (ret.indexOf("OK") != -1) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		try {
			MeiLinSmsServer server = MeiLinSmsServer.getInstance();
			server.sendMsg7bit("1","01","15157102348", "������Ϣ");
			// server.getFetchRead();
		} catch (Exception e) {
			log.error("", e);
		}
	}
}
