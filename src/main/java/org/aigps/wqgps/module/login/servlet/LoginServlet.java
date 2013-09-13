package org.aigps.wqgps.module.login.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aigps.wqgps.common.cache.SessionData;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.log.LogType;
import org.aigps.wqgps.common.log.LogUtil;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.module.login.service.LoginService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 186L;
	protected final static Log log=LogFactory.getLog(LoginServlet.class);
	
    public LoginServlet() {
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

		companyFlag = (companyFlag==null ? "" : companyFlag);
		if(StringUtils.isNotBlank(companyFlag) && !companyFlag.endsWith("/")){
			companyFlag = companyFlag+"/";
		}
		
		PrintWriter out = response.getWriter();
		try {
			LoginService loginService = (LoginService) AppUtil.getBean("loginService");
			List<WqUserInfo> users = loginService.findUserByName(username,companyFlag);
			
			if(users==null || users.isEmpty()){
				out.print("�û�������!");
				return;
			}
			
			WqUserInfo user = null;
			for(WqUserInfo u : users){
				if(u.getPassword().equals(password)){
					user = u;
					break;
				}
			}
			if(user == null){
				out.print("�������!");
				return;
			}
			if (!user.getIsEnable()) {
				out.print("���û��ѱ�����ʹ�ã�����ϵ����Ա!");
				return;
			}
			
			SessionData sessionData = AppUtil.getSessionData();
			
			//����ǰ��������IP��ַ���浽SESSION��,���ڻ�ȡMAPKEY
			String host = request.getHeader("host");
			sessionData.setLoginIp(host.split(":")[0]);
			sessionData.setCompanyFlag(companyFlag);
			
			//�����û�����session��
			sessionData.setUserInfo(user);
			
			String userIp = request.getHeader("x-forwarded-for");
			if (userIp == null) {
				userIp = request.getRemoteAddr();
			}
			//��¼�û���¼
			LogUtil.saveLog(LogType.USER_LOGIN, "IP:"+userIp);

			//1��2���ǳ�������Ա���պ�0��ϵͳ��˾�û�
			if("1".equals(user.getSuperAdmin()) || "2".equals(user.getSuperAdmin())){
				out.print("admin.jsp");
			}else{
				out.print("index.jsp");
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			out.close();
		}
	}

}
