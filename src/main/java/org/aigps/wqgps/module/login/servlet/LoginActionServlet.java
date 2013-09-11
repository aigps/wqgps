package org.sunleads.module.login.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sunleads.common.cache.SessionData;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.log.LogType;
import org.sunleads.common.log.LogUtil;
import org.sunleads.common.util.AppUtil;
import org.sunleads.module.login.service.LoginService;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginActionServlet extends HttpServlet {
	private static final long serialVersionUID = 156L;
	protected final static Log log=LogFactory.getLog(LoginActionServlet.class);
	
    public LoginActionServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//���session��������
		request.getSession().removeAttribute("sessionData");
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String companyFlag = request.getParameter("companyFlag");
		username = new String(username.getBytes("ISO-8859-1"),"GBK");
		password = new String(password.getBytes("ISO-8859-1"),"GBK");
		
		try {
			LoginService loginService = (LoginService) AppUtil.getBean("loginService");
			List<WqUserInfo> users = loginService.findUserByName(username,companyFlag);
			
			if(users==null || users.isEmpty()){
				outPrint("�û�������!",response);
				return;
			}
			WqUserInfo user = users.get(0);
			if(!user.getPassword().equals(password)){
				outPrint("�������!",response);
				return;
			}
			if (!user.getIsEnable()) {
				outPrint("���û��ѱ�����ʹ�ã�����ϵ����Ա!",response);
				return;
			}
			
			SessionData sessionData = AppUtil.getSessionData();
			
			//����ǰ��������IP��ַ���浽SESSION��,���ڻ�ȡMAPKEY
			String host = request.getHeader("host");
			sessionData.setLoginIp(host.split(":")[0]);
			sessionData.setCompanyFlag(companyFlag);
			
			//�����û�����session��
			sessionData.setUserInfo(user);
			
			//��¼�û���¼
			LogUtil.saveLog(LogType.USER_LOGIN, "IP:"+sessionData.getLoginIp());
			
			request.getSession().setAttribute("loginFolder",companyFlag+"/");
			getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
			
		} catch (ServletException e) {
			log.error(e.getMessage(),e);
			throw e;
		} catch (IOException e) {
			log.error(e.getMessage(),e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	
	private void outPrint(String msg, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain;charset=gb2312");
		out.print(msg);
		out.close();
	}
}
