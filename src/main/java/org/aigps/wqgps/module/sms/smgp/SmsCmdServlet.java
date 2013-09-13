package org.aigps.wqgps.module.sms.smgp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.SmsModel;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sun.misc.BASE64Decoder;

/**
 * ����ָ����պͷ��ͽӿ�
 */
public class SmsCmdServlet extends HttpServlet {
	private static final long serialVersionUID = 198656568965L;
	private static final String CMD_SEND="SEND";
	private static final String CMD_RECE="RECE";

	protected final static Log log = LogFactory.getLog(SmsCmdServlet.class);

	public SmsCmdServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = null;
		try {
			out = response.getWriter();
			String cmd = request.getParameter("cmd");//send ���ͣ�rece ����
			String phone = request.getParameter("phone");//ָ�����룬���͵�ʱ��һ��Ҫ�������յ�ʱ��ɴ��ɲ��������������������ж��ţ�������ˣ������ָ������Ķ��š�
			String smsformat = request.getParameter("smsformat");//0 Ascii���룬15���ֶ���Ϣ����������
			String smscontent = request.getParameter("smscontent");//base64������Ķ�������
			smscontent = getFromBASE64(smscontent);
			
			SmsCmdHandle smsCmdHandle = (SmsCmdHandle) AppUtil.getBean("smsCmdHandle");
			if (cmd == null) {
				out.print(false);
				return;
			}else if(cmd.equalsIgnoreCase(CMD_SEND)){

				boolean isSuccess = smsCmdHandle.send(phone.split(","),
						smscontent, smsformat);
				if (!isSuccess) {
					out.print(false);
				} else {
					out.print(true);
				}
			}else if(cmd.equalsIgnoreCase(CMD_RECE)){
				List<SmsModel> smsModeList = new ArrayList<SmsModel>();;
				if(phone==null||phone.equals("")){
					for(String telStr:DataCache.smsDeliverMap.keySet()){
						smsModeList.addAll(DataCache.smsDeliverMap.get(telStr));
					}
					DataCache.smsDeliverMap.clear();
				}else{
					if(DataCache.smsDeliverMap.get(phone)!=null){
						smsModeList.addAll(DataCache.smsDeliverMap.get(phone));
						DataCache.smsDeliverMap.remove(phone);
					}

				}
				String smsXml=createMsgXmlInfo(smsModeList);
				out.print(smsXml);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			out.close();
		}
	}

	/**
	 * BASE64����
	 * @param s
	 * @return
	 */
	public String getFromBASE64(String s) {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * ��װ���ն��Ž�ϳ�XML
	 * @param smsModelList
	 * @return
	 */
	public static String createMsgXmlInfo(List<SmsModel> smsModelList){
		Writer writer = null;
	 	try {
	 		writer = new StringWriter();
			OutputFormat format = new OutputFormat();
		 	format.setSuppressDeclaration(true); 
		 	XMLWriter xmlWriter = new XMLWriter(writer,format); 
		 	Document doc = new DOMDocument();
		 	Element eleRoot = doc.createElement("SMSS");
		 	for(SmsModel smsModel:smsModelList){
			 	Element smsRoot = doc.createElement("SMS");
				
			 	Element smsTelItem = new DOMElement("TEL");
			 	smsTelItem.appendChild(doc.createTextNode(smsModel.getTel()));
			 	Element smsTimeItem = new DOMElement("TIME");
			 	smsTimeItem.appendChild(doc.createTextNode(smsModel.getTime()));
			 	Element smsMsgItem = new DOMElement("MESSAGE");
			 	smsMsgItem.appendChild(doc.createTextNode(smsModel.getMessage()));
			 	
			 	smsRoot.appendChild(smsTelItem);
				smsRoot.appendChild(smsTimeItem);
				smsRoot.appendChild(smsMsgItem);

				eleRoot.appendChild(smsRoot);
		 	}

		 	doc.appendChild(eleRoot);
			xmlWriter.write(doc);
		} catch (IOException e) {
			log.error("", e);
		}
	 	return writer.toString();
	}
	
	public static void main(String[] args) {
		//System.out.println(createMsgXmlInfo());
	}
}
