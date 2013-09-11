
package org.sunleads.common.log;

import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.sunleads.common.cache.DataCache;
import org.sunleads.common.entity.WqLogInfo;
import org.sunleads.common.entity.WqUserInfo;
import org.sunleads.common.util.AppUtil;
import org.sunleads.common.util.TimingUtil;


/**
 * @Title��<�����>
 * @Description��<������>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date��  2011-6-20����04:47:45
 * Modified By��  <�޸�����������ƴ����д>
 * Modified Date��<�޸����ڣ���ʽ:YYYY-MM-DD>
 *
 * Copyright��Copyright(C),1995-2011 ��IPC��09004804��
 * Company�������е��Ƽ��������޹�˾
 */
@Component
@Transactional
public class LogUtil{
	private final static Log log = LogFactory.getLog(LogUtil.class);
	private static boolean hadStartUp = false;
	
	private static List<WqLogInfo> logList = new ArrayList<WqLogInfo>();
	private final static String inserSql = "INSERT INTO WQ_LOG_INFO(TYPE_ID,COMPANY_ID,CONTENT,CREATER,CREATE_TIME) VALUES (?,?,?,?,?)";
	
	//�����̣߳���ʱ�����������־��д�뵽���ݿ���
	private static void startup(){
		Thread t = new Thread(new Runnable(){
			public void run() {
				while(true){
					try{
						final List<WqLogInfo> list = logList;
						logList = new ArrayList<WqLogInfo>();
						
						if(!list.isEmpty()){
							JdbcTemplate jdbcTemplate = (JdbcTemplate)AppUtil.getBean("jdbcTemplate");

							final Iterator<WqLogInfo> it = list.iterator();
							jdbcTemplate.batchUpdate(inserSql, new BatchPreparedStatementSetter(){
								public int getBatchSize() {
									return list.size();
								}
								public void setValues(PreparedStatement ps, int index) throws SQLException {
									WqLogInfo info = it.next();
									ps.setString(1, info.getTypeId());
									ps.setString(2, info.getCompanyId());
									ps.setString(3, info.getContent());
									ps.setString(4, info.getCreater());
									ps.setTimestamp(5, new Timestamp(info.getCreateTime().getTime()));
								}
					        });
						}
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					} finally {
						try{
							Thread.sleep(TimingUtil.getForInt("save.loginfo.interval"));
						} catch (Exception e) {
							log.error(e.getMessage(),e);
						}
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();
		hadStartUp = true;
	}
	
	//������־,�ȷŵ������������ͳһ���浽���ݿ���
	public static void saveLog(String logType,String content){
		WqUserInfo user = null;
		try{
			user = AppUtil.getUserInfo();
		} catch (Exception e) {
		} 
		if(user == null){
			return;
		}
		WqLogInfo logInfo = new WqLogInfo();
		logInfo.setContent(content);
		logInfo.setCreateTime(new Date(System.currentTimeMillis()));
		logInfo.setTypeId(DataCache.logTypeMap.get(logType).getId());
		logInfo.setCompanyId(user.getCompanyId());
		logInfo.setCreater(user.getUserName()+"["+user.getCnName()+"]");
		
		logList.add(logInfo);
		
		if(hadStartUp == false){
			startup();
		}
	}

}

