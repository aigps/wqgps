package org.sunleads.common.dao;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.sunleads.common.util.SqlStringUtil;

@Component
@SuppressWarnings({"rawtypes","unchecked"})
public class PublicDAO extends HibernateDAO<Object, String> {

	public List findLikeBy(String propertyName,String value,Class entityClass){
		this.entityClass = entityClass;
		String hql = "from "+entityClass.getName()+" where "+propertyName +" like '%"+value+"%'";
		return this.find(hql);
	}

	public List findBy(String propertyName1,String propertyName2,String propertyName3,String values1,String values2,String values3,Class entityClass){
		this.entityClass = entityClass;
		String hql = "from "+entityClass.getName()+" where "+propertyName1+"='"+values1+"' and "+propertyName2+" like '%"+values2+"%' and "+propertyName3+" like '%"+values3+"%'";
		return this.find(hql);
	}
	
	public List findBy(String propertyName,Object value,Class entityClass){
		this.entityClass = entityClass;
		return this.findBy(propertyName, value);
	}

	public List findBy(String propertyName,Collection<String> values,Class entityClass){
		this.entityClass = entityClass;
		String hql = "from "+entityClass.getName()+" where "+SqlStringUtil.formatListToSQLIn(propertyName, values, true);
		return this.find(hql);
	}

	public List findBy(String propertyName,List<String> values,String sortPropertyName,Boolean isAsc,Class entityClass){
		this.entityClass = entityClass;
		String hql = "from "+entityClass.getName()+" where "+SqlStringUtil.formatListToSQLIn(propertyName, values, true)+" order by "+sortPropertyName+(isAsc?" asc":" desc");
		return this.find(hql);
	}
	
	public List findBy(String[] propertyNames,Object[] values,Class entityClass,String orderByProperty, boolean isAsc){
		this.entityClass = entityClass;
		StringBuilder sql = new StringBuilder("from "+entityClass.getName()+" where ");
		for(int i=0; i<propertyNames.length; i++){
			sql.append(propertyNames[i]).append("=? and ");
		}
		sql = sql.delete(sql.length()-4,sql.length());
		if(StringUtils.isNotBlank(orderByProperty)){
			sql.append(" order by ").append(orderByProperty).append(isAsc ? " asc":" desc");
		}
		return this.find(sql.toString(),values);
	}
	
	public List get(Collection<String> ids, Class entityClass){
		this.entityClass = entityClass;
		return this.get(ids);
	}

	public Object get(String id, Class entityClass){
		this.entityClass = entityClass;
		return this.get(id);
	}

	public List getAll(Class entityClass){
		this.entityClass = entityClass;
		return this.getAll();
	}

	public List getAll(String orderByProperty, boolean isAsc, Class entityClass){
		this.entityClass = entityClass;
		return this.getAll(orderByProperty, isAsc);
	}
	
	public void delete(String propertyName,String value,Class entityClass){
		String hql = "delete from "+entityClass.getName()+" where "+propertyName+"=?";
		this.batchExecute(hql, value);
	}
	
	public int getCount(String propertyName,String value,Class entityClass){
		return this.findBy(propertyName, value, entityClass).size();
	}
	
}
