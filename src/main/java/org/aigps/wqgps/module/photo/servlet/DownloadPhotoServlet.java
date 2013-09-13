package org.aigps.wqgps.module.photo.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DownloadPhotoServlet extends HttpServlet {
	private static final long serialVersionUID = -912952487L;
	protected final static Log log=LogFactory.getLog(DownloadPhotoServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/x-download"); //����������

		response.setHeader("Content-Disposition","attachment;filename=" + "download.jpg");//�ļ��������Խ�һ������

		//������

		BufferedInputStream inputStream = new BufferedInputStream(request.getInputStream()); 

		OutputStream outputStream = response.getOutputStream();

		byte [] bytes = new byte[1024]; 

		int v; 

		//д����

		while((v=inputStream.read(bytes))>0){ 

		outputStream.write(bytes,0,v); 

		} 

		outputStream.flush();

		outputStream.close();

		inputStream.close(); 
	}

}