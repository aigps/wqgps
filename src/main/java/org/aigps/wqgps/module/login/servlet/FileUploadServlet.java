package org.sunleads.module.login.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings({"rawtypes"})
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 6653967682563350802L;
	
	public final static Log log = LogFactory.getLog(FileUploadServlet.class);
	
	public FileUploadServlet(){
		
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		DiskFileItemFactory factory = new DiskFileItemFactory();

		ServletFileUpload fileUpload = new ServletFileUpload(factory);
		fileUpload.setSizeMax(1024 * 1025 * 1024);
		String urlFlag=request.getParameter("urlFlag");
		try {
			List items = fileUpload.parseRequest(request);
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				if (item.isFormField()) {
					String name = item.getFieldName();
					String value = item.getString();
					System.out.println(name + ":" + value);
				} else {
//					String fieldName = item.getFieldName();
//					String fileName = item.getName();
//					String contentType = item.getContentType();
//					boolean isInMemory = item.isInMemory();
//					long sizeInBytes = item.getSize();
					String path = getServletContext().getRealPath("/");
					File uploadedFile = new File(path+urlFlag+File.separator+"resources/image/logo.jpg");
					item.write(uploadedFile);
				}
			}
		} catch (FileUploadException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
	}

}
