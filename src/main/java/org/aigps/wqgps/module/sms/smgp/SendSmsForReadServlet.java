package org.aigps.wqgps.module.sms.smgp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aigps.wqgps.common.entity.WqSmsH;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.module.sms.service.SmsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;

/**
 * Servlet implementation class LoginServlet
 */
public class SendSmsForReadServlet extends HttpServlet {
	private static final long serialVersionUID = 198656568965L;
//	private static int inval = 1;
	protected final static Log log = LogFactory.getLog(SendSmsForReadServlet.class);

	public SendSmsForReadServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = null;
		request.setCharacterEncoding("UTF-8");
		boolean isSuccess = false;
		try {
			out = response.getWriter();
			String fTakeKey = request.getParameter("fTakeKey");
			String type = request.getParameter("type");
			if(type != null){
				type = new String(type.getBytes("ISO-8859-1"), "UTF-8");
			}
			SmsService smsService = (SmsService) AppUtil.getBean("smsService");
			WqSmsH smsModel = smsService.findSmsById(fTakeKey);
			if(smsModel != null){
				if(type == null || type.equals("")){
					smsModel.setState("03");
				}else{
					smsModel.setState(type);
				}
				smsService.saveSms(smsModel);
				isSuccess = true; 
			}
			
			log.info("∂Ã–≈ªÿ÷¥£∫"+isSuccess);

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
