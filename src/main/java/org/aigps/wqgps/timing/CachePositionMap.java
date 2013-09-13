package org.aigps.wqgps.timing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aigps.wqgps.common.cache.DataCache;
import org.aigps.wqgps.common.entity.DcGpsReal;
import org.aigps.wqgps.common.util.AppUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CachePositionMap{
	public final static Log log = LogFactory.getLog(CachePositionMap.class);

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public static void startup(){
		new Thread(new Runnable(){
			public void run() { 
				try{
					CachePositionMap refresh = (CachePositionMap) AppUtil.getBean("cachePositionMap");
					List<DcGpsReal> list = refresh.findAll();
					
					for(DcGpsReal p : list){
						String staffId = p.getTmnAlias();
						if(StringUtils.isNotBlank(staffId)){
							DataCache.staffPostionMap.put(p.getTmnAlias(), p);
						}
					}
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}).start();
	}
	
	public List<DcGpsReal> findAll(){
		List<Map<String, Object>> tempList = jdbcTemplate.queryForList("SELECT * FROM DC_GPS_REAL");
		List<DcGpsReal> resultList = new ArrayList<DcGpsReal>();
		for(Map<String,Object> map:tempList){
			DcGpsReal dcGpsReal = new DcGpsReal();
			DcGpsReal.createModel(map, dcGpsReal);
			resultList.add(dcGpsReal);
		}
		return resultList;
	}

}

