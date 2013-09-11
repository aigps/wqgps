package org.sunleads.timing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.dao.HibernateDAO;
import org.sunleads.common.entity.WqSmsH;
import org.sunleads.common.util.AppUtil;

@Component
@Transactional
public class SmsSendsJob extends HibernateDAO<WqSmsH, String>{
	public final static Log log = LogFactory.getLog(SmsSendsJob.class);
	
	private  static final String SMS_TYPE = "SMS";
	
	protected LinkedList<WqSmsH> smslist = new LinkedList<WqSmsH>(); 

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public LinkedList<WqSmsH> getSmslist() {
		return smslist;
	}
	
	public void setSmslist(LinkedList<WqSmsH> smslist) {
		this.smslist = smslist;
	}

	public static void startup(){
		Thread t = new Thread(new Runnable(){
			public void run() { 
				while(true){
					try {
						SmsSendsJob job = (SmsSendsJob) AppUtil.getBean("smsSendsJob");
						if(job.getSmslist().isEmpty()){
							continue;
						}
						
						LinkedList<WqSmsH> list = job.getSmslist();
						job.setSmslist(new LinkedList<WqSmsH>());
						
						for (WqSmsH wqSmsH : list) {
							boolean flag = job.sendSms(wqSmsH);
							if(flag){
								wqSmsH.setState(WqSmsH.SEND_SUCCESS);
							}else{
								wqSmsH.setState(WqSmsH.SEND_FAIL);
							}
						}
						job.batchUpdate(list);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					} finally {
						try{
							Thread.sleep(10*1000);
						} catch (Exception e) {
							log.error(e.getMessage(),e);
						}
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}
	
	public List<WqSmsH> findAll(){
		String hql="FROM WqSmsH where state=?";
		return this.find(hql,"00");
	}
	
	private boolean sendSms(WqSmsH model){
		InputStream inputStream = null;
		BufferedReader input = null;
		try {
			String content = new String(Base64.encode(model.getSmsContent().getBytes()));
			if(model.getBadging() != null && !model.getBadging().equals("")){
				String badging = "[".concat(model.getBadging()).concat("]");
				badging = new String(Base64.encode(model.getBadging().getBytes()));
				content = content.concat(badging);
			}
			
			StringBuilder url = new StringBuilder();
			url.append("http://122.224.88.34:1197/sunleadsWebService/ymserver?");
			url.append("srctype=");
			url.append(SMS_TYPE);
			url.append("&simCode=");
			url.append(model.getPhone());
			url.append("&smsCnt=");
			url.append(content);
			
			HttpURLConnection urlConnection = (HttpURLConnection)new URL(url.toString()).openConnection();
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(3000);
			urlConnection.setReadTimeout(30000);
			inputStream  = urlConnection.getInputStream();
			input = new BufferedReader(new InputStreamReader(inputStream));
			String line = input.readLine();
			log.info("·¢ËÍ¶ÌÏûÏ¢£º".concat(model.getPhone()).concat("-").concat(model.getSmsContent()));
			log.info("ÏûÏ¢ÄÚÈÝ64±àÂë£º".concat(content));
			log.info(line);
			if(input != null && line.trim().equals("true")){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			try {
				if(inputStream != null){inputStream.close();}
				if(input != null){input.close();}
			} catch (Exception e2) {}
		}
		return false;
	}
	
	
	public void batchUpdate(final List<WqSmsH> list){
		String sql = "update WQ_SMS_H set state = ? where id=?";
		this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ag, int index) throws SQLException {
				WqSmsH model = list.get(index);
				ag.setString(1, model.getState());
				ag.setString(2, model.getId());
			}
			
			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}
	public static void main(String[] args) {
		SmsSendsJob job = new SmsSendsJob();
		WqSmsH model = new WqSmsH();
		model.setPhone("18675833784");
		model.setSmsContent("²âÊÔ¶ÌÏûÏ¢·¢ËÍ");
		boolean flag = job.sendSms(model);
		System.out.println(flag);
//		String aa = new String(Base64.encodeBase64("²âÊÔ-²âÊÔ,²âÊÔ¶ÌÐÅÏ¢,d".getBytes()));
//		System.out.println(aa);
	}
}

