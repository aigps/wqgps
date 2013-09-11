package org.sunleads.module.login.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
		
		//清除session缓存数据
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
				out.print("用户不存在!");
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
				out.print("密码错误!");
				return;
			}
			if (!user.getIsEnable()) {
				out.print("此用户已被限制使用，请联系管理员!");
				return;
			}
			
			SessionData sessionData = AppUtil.getSessionData();
			
			//将当前服务器的IP地址保存到SESSION中,用于获取MAPKEY
			String host = request.getHeader("host");
			sessionData.setLoginIp(host.split(":")[0]);
			sessionData.setCompanyFlag(companyFlag);
			
			//保存用户对象到session中
			sessionData.setUserInfo(user);
			
			String userIp = request.getHeader("x-forwarded-for");
			if (userIp == null) {
				userIp = request.getRemoteAddr();
			}
			//记录用户登录
			LogUtil.saveLog(LogType.USER_LOGIN, "IP:"+userIp);

			//1和2都是超级管理员，空和0是系统公司用户
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
