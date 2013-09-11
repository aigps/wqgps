package org.sunleads.module.sms.smgp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sunleads.common.util.AppUtil;

import sun.misc.BASE64Decoder;

/**
 * Servlet implementation class LoginServlet
 */
public class SendSmsServlet extends HttpServlet {
	private static final long serialVersionUID = 198656568965L;
//	private static int inval = 1;
	protected final static Log log = LogFactory.getLog(SendSmsServlet.class);

	public SendSmsServlet() {
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
			String sendPhone = request.getParameter("sendPhone");
			String sendContent = request.getParameter("sendContent");
//			String smstype = request.getParameter("smstype");//��������      Ԥ��
			String smsformat = request.getParameter("smsformat");//���Ÿ�ʽ      Ԥ��
			if (sendPhone == null || sendContent == null) {
				out.print(false);
				return;
			}
			sendPhone = new String(sendPhone.getBytes("ISO-8859-1"), "GBK");

			sendContent = new String(sendContent.getBytes("ISO-8859-1"), "GBK");

			sendContent = getFromBASE64(sendContent);

			ISmsSender smsSender = (ISmsSender) AppUtil.getBean((String)AppUtil.getBean("smsSender"));
			
			boolean isSuccess = smsSender.send(sendPhone.split(","),
					sendContent, "01",smsformat);
			
			log.info("����״̬��"+isSuccess);

			if (!isSuccess) {
				out.print(false);
			} else {
				out.print(true);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			out.close();
		}
	}

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
}
