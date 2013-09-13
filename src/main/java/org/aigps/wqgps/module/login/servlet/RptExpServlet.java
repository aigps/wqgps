package org.aigps.wqgps.module.login.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.CellType;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet implementation class LoginServlet
 */
public class RptExpServlet extends HttpServlet {
	private static final long serialVersionUID = 965354L;
	protected final static Log log=LogFactory.getLog(RptExpServlet.class);
	
	private static Pattern pattern = Pattern.compile("\\[[0-9]+\\]");
	
    public RptExpServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		String isWriteData = request.getParameter("isWriteData");
		String[][] datas=null;
		if(!"0".equals(isWriteData)){
			//��ȡ������������
			datas = getDatas(request);
			if(datas == null){
				log.error("��������ʱ����������!");
				return;
			}
		}

		
		String reportName = request.getParameter("reportName");
		
		try {
			response.setContentType("application/vnd.ms-excel");
			response.addHeader("Content-Disposition","attachment;filename=\""+URLEncoder.encode(reportName, "UTF-8")+".xls"+"\"");    
			OutputStream os = response.getOutputStream();
			
			//���ݱ������ƣ���ȡ����ģ��
			File file = new File(AppUtil.getWebAppPath()+"/template/"+reportName+".xls");
			Workbook wb = Workbook.getWorkbook(file);
			WritableWorkbook book = Workbook.createWorkbook(os, wb);
			
			//��õ�һ�����������
			WritableSheet sheet = book.getSheet(0);
			if(!"0".equals(isWriteData)){

				//���������뵽sheet��
				setSheetData(sheet,datas);
			}
			
			book.write(); 
			book.close();

			
			os.close();
			response.flushBuffer();
			response.setContentLength((int) file.length());
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public static void setSheetData(WritableSheet sheet, String[][] datas) throws Exception{
		sheet.getSettings().setScaleFactor(100);
		
		int colNum = sheet.getColumns(), rowNum = sheet.getRows(), beginRowNum = -1;
		
		//��ȡ��һ����Ҫ�����ݵ��к�
		for(int row=0; row<rowNum; row++) {
			for(int j=0; j<colNum; j++) {
				Cell cell = sheet.getCell(j,row);
				String content = cell.getContents().trim();
				Matcher matcher = pattern.matcher(content);
				if(matcher.find()){
					beginRowNum = row;
					break;
				}
			}
		}
		
		if(beginRowNum != -1){
			//��ȡ���õ��и�
			int rowHeight = sheet.getRowHeight(beginRowNum);
			WritableCell[] cells = new WritableCell[colNum];
			//�õ�Ҫ����ֵ����
			for(int col=0; col<colNum; col++){
				cells[col] = sheet.getWritableCell(col, beginRowNum);
			}
			
			//�������ݵĶ��٣��������
			for(int row=0; row<datas.length-1; row++){
				int createRow = beginRowNum+row+1;
				sheet.insertRow(createRow);
				for(int col=0; col<colNum; col++){
					WritableCell cell = cells[col].copyTo(col, createRow);
					//������ñ�����ɫ�ǰ�ɫ
					if(row%2 == 1 && cell.getCellFormat() != null) {
						WritableCellFormat format = new WritableCellFormat(cell.getCellFormat());
						format.setBackground(Colour.WHITE);
						cell.setCellFormat(format);
					}
					sheet.addCell(cell);
				}
				//Ϊ�������������и�
				sheet.setRowView(createRow, rowHeight);
			}
			
			//����ֵ�Ļ�ȡ�����
			for(int row=beginRowNum; row<beginRowNum+datas.length; row++){
				for(int col=0; col<colNum; col++){
					WritableCell cell = sheet.getWritableCell(col, row);
					String content = cell.getContents().trim();
					content = getRealContent(content, datas[row-beginRowNum]);
					if (cell.getType() == CellType.LABEL) {
						Label lc = (Label) cell;
						lc.setString(content);
					}
				}
			}
		}
		
	}
	
	/**
	 * ��ȡǰ�˴��������������ݣ����ݸ�ʽ���ַ��ǡ�#_#������У�#.# �����
	 * @param request
	 * @return
	 */
	private String[][] getDatas(HttpServletRequest request){
		String datas = request.getParameter("datas");
		if(datas == null){
			return null;
		}
		
		String[] rows = datas.split("#_#");
		String[][] rptDatas = new String[rows.length][];
		
		for(int i=0; i<rptDatas.length; i++) {
			while(rows[i].indexOf("#.##.#") != -1){
				rows[i] = rows[i].replaceAll("#.##.#", "#.# #.#");
			}
			rptDatas[i] = rows[i].split("#.#");
			for(int j=0; j<rptDatas[i].length; j++){
				if(rptDatas[i][j].equals(" ")){
					rptDatas[i][j] = "";
				}
			}
		}
		
		return rptDatas;
	}

	/**
	 * ͨ����ʵ��ֵ���������ʶֵ���õ������ϵ���ʵֵ��
	 * @param content
	 * @param rowDatas
	 * @return
	 */
	private static String getRealContent(String content, String[] rowDatas) {
		Matcher matcher = pattern.matcher(content);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()){
			for(int i=0;i<=matcher.groupCount();i++){
				String token = matcher.group(i);
				String index = token.substring(1,token.length()-1);
				int num = Integer.parseInt(index);
				if(rowDatas.length <= num){
					matcher.appendReplacement(sb, "");
				}else{
					matcher.appendReplacement(sb, rowDatas[num]);
				}
			}
		}
		matcher.appendTail(sb); 
		return sb.toString();
	}

	public static void main(String[] args){
//		RptExpServlet a = new RptExpServlet();
//		System.out.println(a.getRealContent("[0]ǧ��/Сʱ", new String[]{"",""}));
	}
	
}
