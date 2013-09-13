package org.aigps.wqgps.timing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.util.AppUtil;
import org.aigps.wqgps.common.util.TimingUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class RefreshPhoneStateMap{
	public final static Log log = LogFactory.getLog(RefreshPhoneStateMap.class);

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public static void startup(){
		Thread t = new Thread(new Runnable(){
			public void run() { 
				while(true){
					try {
						RefreshPhoneStateMap refresh = (RefreshPhoneStateMap) AppUtil.getBean("refreshPhoneStateMap");
						DataCache.phoneStateMap = refresh.findStateMap();
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					} finally {
						try{
							Thread.sleep(TimingUtil.getForInt("refresh.phone.state.interval"));
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
	
	public Map<String,String[]> findStateMap(){
		String sql = "SELECT STAFF_ID,STTS,RPT_TIME FROM WQ_TMN_STTS_REAL";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		
		Map<String,String[]> stateMap = new HashMap<String,String[]>();
		for(Map<String,Object> map : list){
			stateMap.put((String)map.get("STAFF_ID"), new String[]{(String)map.get("STTS"),(String)map.get("RPT_TIME")});
		}
		return stateMap;
	}

}

