package org.sunleads.module.login.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.codec.Base64;
import org.sunleads.common.cache.SessionData;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.log.LogType;
import org.sunleads.common.log.LogUtil;
import org.sunleads.common.util.AppUtil;
import org.sunleads.module.login.service.LoginService;

/**
 * Servlet implementation class LoginServlet
 */
public class HebLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 186L;
	protected final static Log log=LogFactory.getLog(HebLoginServlet.class);
	
    public HebLoginServlet() {
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
		String companyFlag = "hebwq/";

		username = new String(Base64.decode(username.getBytes()));
		password = new String(Base64.decode(password.getBytes()));
		PrintWriter out = response.getWriter();
		try {
			response.setContentType("text/json; charset=gbk");

			LoginService loginService = (LoginService) AppUtil.getBean("loginService");
			List<WqUserInfo> users = loginService.findUserByName(username,null);
			
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
			sessionData.setOuterParamMap(getOuterParamMap(request));
			
			//保存用户对象到session中
			sessionData.setUserInfo(user);
			
			String userIp = request.getHeader("x-forwarded-for");
			if (userIp == null) {
				userIp = request.getRemoteAddr();
			}
			//记录用户登录
			LogUtil.saveLog(LogType.USER_LOGIN, "IP:"+userIp);
			
			request.getSession().setAttribute("loginFolder",companyFlag);
			String menu = (String)sessionData.getOuterParamMap().get("menu");
			if("105".equals(menu)) {
				getServletContext().getRequestDispatcher("/route.html").forward(request, response);
			}else if("106".equals(menu)) {
				getServletContext().getRequestDispatcher("/region.html").forward(request, response);
			}else{
				getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			out.close();
		}
	}

	//转化参数
	@SuppressWarnings("unchecked")
	private Map<String,Object> getOuterParamMap(HttpServletRequest request){
		Map<String,Object> outerMap = new HashMap<String,Object>(request.getParameterMap());
		try{
			for(Iterator<String> it=outerMap.keySet().iterator(); it.hasNext(); ){
				String key = it.next();
				String[] value = (String[])outerMap.get(key);
				value[0] = value[0].replaceAll(" ", "+");
				String v = new String(Base64.decode(value[0].getBytes()));
				outerMap.put(key, v);
				System.out.println("========================参数"+key+"="+v);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return outerMap;
	}
}
