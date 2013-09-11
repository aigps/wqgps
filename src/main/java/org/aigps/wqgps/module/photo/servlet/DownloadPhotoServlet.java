package org.sunleads.module.photo.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.dao.PublicDAO;
import org.sunleads.common.util.AppUtil;

public class DownloadPhotoServlet extends HttpServlet {
	private static final long serialVersionUID = -912952487L;
	protected final static Log log=LogFactory.getLog(DownloadPhotoServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/x-download"); //内容是下载

		response.setHeader("Content-Disposition","attachment;filename=" + "download.jpg");//文件名，可以进一步处理

		//读数据

		BufferedInputStream inputStream = new BufferedInputStream(request.getInputStream()); 

		OutputStream outputStream = response.getOutputStream();

		byte [] bytes = new byte[1024]; 

		int v; 

		//写数据

		while((v=inputStream.read(bytes))>0){ 

		outputStream.write(bytes,0,v); 

		} 

		outputStream.flush();

		outputStream.close();

		inputStream.close(); 
	}

}