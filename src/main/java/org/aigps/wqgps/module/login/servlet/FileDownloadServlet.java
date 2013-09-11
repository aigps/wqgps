package org.sunleads.module.login.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sunleads.common.util.AppUtil;

/**
 * Servlet implementation class LoginServlet
 */
public class FileDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 43232L;
	protected final static Log log=LogFactory.getLog(FileDownloadServlet.class);
	
    public FileDownloadServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String fileName = request.getParameter("f");
		
		try {
	        // 读到流中
	        InputStream inStream = new FileInputStream(AppUtil.getWebAppPath()+"/template/"+fileName);// 文件的存放路径
	        // 设置输出的格式
	        response.reset();
	        response.setContentType("bin");
	        response.addHeader("Content-Disposition", "attachment; filename=\""+URLEncoder.encode(fileName, "UTF-8")+"\"");
	        // 循环取出流中的数据
	        byte[] b = new byte[100];
	        int len;
	        while ((len = inStream.read(b)) > 0){
                response.getOutputStream().write(b, 0, len);
	        }
            inStream.close();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
}
