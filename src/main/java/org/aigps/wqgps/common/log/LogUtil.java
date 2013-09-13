
package org.aigps.wqgps.common.log;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.WqLogInfo;
import org.aigps.wqgps.common.entity.WqUserInfo;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.TimingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * @Title：<类标题>
 * @Description：<类描述>
 *
 * @author xiexueze
 * @version 1.0
 *
 * Create Date：  2011-6-20下午04:47:45
 * Modified By：  <修改人中文名或拼音缩写>
 * Modified Date：<修改日期，格式:YYYY-MM-DD>
 *
 * Copyright：Copyright(C),1995-2011 浙IPC备09004804号
 * Company：杭州中导科技开发有限公司
 */
@Component
@Transactional
public class LogUtil{
	private final static Log log = LogFactory.getLog(LogUtil.class);
	private static boolean hadStartUp = false;
	
	private static List<WqLogInfo> logList = new ArrayList<WqLogInfo>();
	private final static String inserSql = "INSERT INTO WQ_LOG_INFO(TYPE_ID,COMPANY_ID,CONTENT,CREATER,CREATE_TIME) VALUES (?,?,?,?,?)";
	
	//启动线程，定时将缓存里的日志，写入到数据库中
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
	
	//保存日志,先放到缓存里，等任务统一保存到数据库中
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

