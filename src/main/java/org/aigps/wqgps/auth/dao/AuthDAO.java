
package org.sunleads.auth.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gps.util.SqlStringUtil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

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
public class AuthDAO {

	private final static Log log = LogFactory.getLog(AuthDAO.class);

	//获取指定用户有权限看到的资源ID列表
	public static List<String> getResIdListByUserId(String userId,String objId,JdbcTemplate jdbcTemplate){
		String sql = "SELECT RES_ID FROM WQ_USER_AUTH WHERE USER_ID=? AND OBJ_ID=?";
		return jdbcTemplate.query(sql,new Object[]{userId,objId}, new RowMapper<String>(){
			@Override
			public String mapRow(ResultSet rs, int index) throws SQLException {
				return rs.getString("RES_ID");
			}
		});
	}

	//获取指定资源的所有有权限的用户ID列表
	public static List<String> getUserIdListByResId(String resId,String objId,String compayId,JdbcTemplate jdbcTemplate){
		String sql = "SELECT USER_ID FROM WQ_USER_AUTH WHERE RES_ID=? AND OBJ_ID=? AND COMPANY_ID=?";
		return jdbcTemplate.query(sql,new Object[]{resId,objId,compayId}, new RowMapper<String>(){
			@Override
			public String mapRow(ResultSet rs, int index) throws SQLException {
				return rs.getString("USER_ID");
			}
		});
	}

	//对用户进行权限的保存
	public static Boolean saveUserResListByUserId(List<String> resList,final String objId,final String userId,final String companyId,JdbcTemplate jdbcTemplate){
		String sql = "INSERT INTO WQ_USER_AUTH(USER_ID,RES_ID,OBJ_ID,COMPANY_ID) VALUES (?,?,?,?)";
		final Iterator<String> it = resList.iterator();
		final int size = resList.size();

		try{
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				public int getBatchSize() {
					return size;
				}
	
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					String resId = it.next();
					ps.setString(1, userId);
					ps.setString(2, resId);
					ps.setString(3, objId);
					ps.setString(4, companyId);
				}
			});
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return false;
		}
		return true;
	}

	//对用户进行权限的保存
	public static Boolean saveUserResListByResId(List<String> userIdList,final String objId,final String resId,final String compayId,JdbcTemplate jdbcTemplate){
		String sql = "INSERT INTO WQ_USER_AUTH(USER_ID,RES_ID,OBJ_ID,COMPANY_ID) VALUES (?,?,?,?)";
		final Iterator<String> it = userIdList.iterator();
		final int size = userIdList.size();

		try{
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				public int getBatchSize() {
					return size;
				}
				
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					String userId = it.next();
					ps.setString(1, userId);
					ps.setString(2, resId);
					ps.setString(3, objId);
					ps.setString(4, compayId);
				}
			});
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return false;
		}
		return true;
	}
	
	//删除指定用户的权限
	public static Boolean deleteUserResByUserId(String userId,String objId,JdbcTemplate jdbcTemplate){
		String sql = "DELETE FROM WQ_USER_AUTH WHERE USER_ID=? AND OBJ_ID=?";
		try{
			jdbcTemplate.update(sql, userId, objId);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return false;
		}
		return true;
	}
	
	//删除指定资源的权限
	public static Boolean deleteUserResByResId(String resId,String objId,JdbcTemplate jdbcTemplate){
		String sql = "DELETE FROM WQ_USER_AUTH WHERE RES_ID=? AND OBJ_ID=?";
		try{
			jdbcTemplate.update(sql, resId, objId);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return false;
		}
		return true;
	}

	
	//为多个用户添加多个资源，不删除原有用户的资源
	public static Boolean addUsersResList(String objId,List<String> userIds,List<String> resList,String companyId,JdbcTemplate jdbcTemplate){
		deleteUsersResList(objId,userIds,resList,jdbcTemplate);
		boolean flag = true;
		for(String userId : userIds){
			flag = flag && saveUserResListByUserId(resList,objId,userId,companyId,jdbcTemplate);
		}
		return flag;
	}
	
	//为多个用户移除多个资源
	public static Boolean deleteUsersResList(String objId,List<String> userIds,List<String> resList,JdbcTemplate jdbcTemplate){
		String sql = "DELETE FROM WQ_USER_AUTH WHERE USER_ID=? AND OBJ_ID=? AND ";
		try{
			for(String userId : userIds){
				String delSql = sql+SqlStringUtil.formatListToSQLIn("RES_ID", resList, true);
				jdbcTemplate.update(delSql, userId, objId);
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return false;
		}
		return true;
	}
//=============================================================================================================================
	
	
	
	//获取指定权限类型的所有权限
	public static List<Map<String,Object>> getListByObjId(String objId,JdbcTemplate jdbcTemplate){
		String sql = "SELECT STAFF_ID,RES_ID FROM WQ_STAFF_AUTH WHERE OBJ_ID=?";
		return jdbcTemplate.queryForList(sql, objId);
	}

	//获取指定员工有权限看到的资源ID列表
	public static List<String> getResIdListByStaffId(String staffId,String objId,JdbcTemplate jdbcTemplate){
		String sql = "SELECT RES_ID FROM WQ_STAFF_AUTH WHERE STAFF_ID=? AND OBJ_ID=?";
		return jdbcTemplate.query(sql,new Object[]{staffId,objId}, new RowMapper<String>(){
			@Override
			public String mapRow(ResultSet rs, int index) throws SQLException {
				return rs.getString("RES_ID");
			}
		});
	}

	//获取指定资源的所有有权限的员工ID列表
	public static List<String> getStaffIdListByResId(String resId,String objId,String compayId,JdbcTemplate jdbcTemplate){
		String sql = "SELECT STAFF_ID FROM WQ_STAFF_AUTH WHERE RES_ID=? AND OBJ_ID=? AND COMPANY_ID=?";
		return jdbcTemplate.query(sql,new Object[]{resId,objId,compayId}, new RowMapper<String>(){
			@Override
			public String mapRow(ResultSet rs, int index) throws SQLException {
				return rs.getString("STAFF_ID");
			}
		});
	}

	//对员工进行权限的保存
	public static Boolean saveStaffResListByStaffId(List<String> resList,final String objId,final String staffId,final String companyId,JdbcTemplate jdbcTemplate){
		String sql = "INSERT INTO WQ_STAFF_AUTH(STAFF_ID,RES_ID,OBJ_ID,COMPANY_ID) VALUES (?,?,?,?)";
		final Iterator<String> it = resList.iterator();
		final int size = resList.size();

		try{
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				public int getBatchSize() {
					return size;
				}
	
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					String resId = it.next();
					ps.setString(1, staffId);
					ps.setString(2, resId);
					ps.setString(3, objId);
					ps.setString(4, companyId);
				}
			});
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return false;
		}
		return true;
	}

	//对资源进行权限的保存
	public static Boolean saveStaffResListByResId(List<String> staffIdList,final String objId,final String resId,final String compayId,JdbcTemplate jdbcTemplate){
		String sql = "INSERT INTO WQ_STAFF_AUTH(STAFF_ID,RES_ID,OBJ_ID,COMPANY_ID) VALUES (?,?,?,?)";
		final Iterator<String> it = staffIdList.iterator();
		final int size = staffIdList.size();

		try{
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				public int getBatchSize() {
					return size;
				}
	
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					String staffId = it.next();
					ps.setString(1, staffId);
					ps.setString(2, resId);
					ps.setString(3, objId);
					ps.setString(4, compayId);
				}
			});
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return false;
		}
		return true;
	}
	
	//删除指定员工的权限
	public static Boolean deleteStaffResByStaffId(String staffId,String objId,JdbcTemplate jdbcTemplate){
		String sql = "DELETE FROM WQ_STAFF_AUTH WHERE STAFF_ID=? AND OBJ_ID=?";
		try{
			jdbcTemplate.update(sql, staffId, objId);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return false;
		}
		return true;
	}

	//删除指定员工的权限
	public static Boolean deleteStaffResByResId(String resId,String objId,JdbcTemplate jdbcTemplate){
		String sql = "DELETE FROM WQ_STAFF_AUTH WHERE RES_ID=? AND OBJ_ID=?";
		try{
			jdbcTemplate.update(sql, resId, objId);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return false;
		}
		return true;
	}

	//为多个员工添加多个资源，不删除原有员工的资源
	public static Boolean addStaffsResList(String objId,List<String> staffIds,List<String> resList,String companyId,JdbcTemplate jdbcTemplate){
		deleteStaffsResList(objId,staffIds,resList,jdbcTemplate);
		boolean flag = true;
		for(String staffId : staffIds){
			flag = flag && saveStaffResListByStaffId(resList,objId,staffId,companyId,jdbcTemplate);
		}
		return flag;
	}
	
	//为多个员工移除多个资源
	public static Boolean deleteStaffsResList(String objId,List<String> staffIds,List<String> resList,JdbcTemplate jdbcTemplate){
		String sql = "DELETE FROM WQ_STAFF_AUTH WHERE STAFF_ID=? AND OBJ_ID=? AND ";
		try{
			for(String staffId : staffIds){
				String delSql = sql+SqlStringUtil.formatListToSQLIn("RES_ID", resList, true);
				jdbcTemplate.update(delSql, staffId, objId);
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return false;
		}
		return true;
	}
}

