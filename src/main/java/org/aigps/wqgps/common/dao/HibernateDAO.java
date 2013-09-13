/**
 * Copyright (c) 2005-2010 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: SimpleHibernateDao.java 1205 2010-09-09 15:12:17Z calvinxiu $
 */
package org.aigps.wqgps.common.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

@SuppressWarnings({"unchecked","rawtypes"})
public class HibernateDAO<T, PK extends Serializable> {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected SessionFactory sessionFactory;

	protected Class<T> entityClass;

	/**
	 * 鐢ㄤ簬Dao灞傚瓙绫讳娇鐢ㄧ殑鏋勯�鍑芥暟.
	 * 閫氳繃瀛愮被鐨勬硾鍨嬪畾涔夊彇寰楀璞＄被鍨婥lass.
	 * eg.
	 * public class UserDao extends SimpleHibernateDao<User, Long>
	 */
	public HibernateDAO() {
		this.entityClass = getSuperClassGenricType(getClass(), 0);
	}

	/**
	 * 閫氳繃鍙嶅皠, 鑾峰緱Class瀹氫箟涓０鏄庣殑鐖剁被鐨勬硾鍨嬪弬鏁扮殑绫诲瀷.
	 * 濡傛棤娉曟壘鍒� 杩斿洖Object.class.
	 * 
	 * 濡俻ublic UserDao extends HibernateDao<User,Long>
	 *
	 * @param clazz clazz The class to introspect
	 * @param index the Index of the generic ddeclaration,start from 0.
	 * @return the index generic declaration, or Object.class if cannot be determined
	 */
	public static Class getSuperClassGenricType(final Class clazz, final int index) {
		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}

		return (Class) params[index];
	}

	/**
	 * 鐢ㄤ簬鐢ㄤ簬鐪佺暐Dao灞� 鍦⊿ervice灞傜洿鎺ヤ娇鐢ㄩ�鐢⊿impleHibernateDao鐨勬瀯閫犲嚱鏁�
	 * 鍦ㄦ瀯閫犲嚱鏁颁腑瀹氫箟瀵硅薄绫诲瀷Class.
	 * eg.
	 * SimpleHibernateDao<User, Long> userDao = new SimpleHibernateDao<User, Long>(sessionFactory, User.class);
	 */
	public HibernateDAO(final SessionFactory sessionFactory, final Class<T> entityClass) {
		this.sessionFactory = sessionFactory;
		this.entityClass = entityClass;
	}

	/**
	 * 鍙栧緱sessionFactory.
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * 閲囩敤@Autowired鎸夌被鍨嬫敞鍏essionFactory, 褰撴湁澶氫釜SesionFactory鐨勬椂鍊欏湪瀛愮被閲嶈浇鏈嚱鏁�
	 */
	@Autowired
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * 鍙栧緱褰撳墠Session.
	 */
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * 淇濆瓨鏂板鎴栦慨鏀圭殑瀵硅薄.
	 */
	public void save(final T entity) {
		Assert.notNull(entity, "entity涓嶈兘涓虹┖");
		getSession().saveOrUpdate(entity);
		logger.debug("save entity: {}", entity);
	}

	/**
	 * 鍒犻櫎瀵硅薄.
	 * 
	 * @param entity 瀵硅薄蹇呴』鏄痵ession涓殑瀵硅薄鎴栧惈id灞炴�鐨則ransient瀵硅薄.
	 */
	public void delete(final T entity) {
		Assert.notNull(entity, "entity涓嶈兘涓虹┖");
		getSession().delete(entity);
		logger.debug("delete entity: {}", entity);
	}

	/**
	 * 鎸塱d鍒犻櫎瀵硅薄.
	 */
	public void delete(final PK id) {
		Assert.notNull(id, "id涓嶈兘涓虹┖");
		delete(get(id));
		logger.debug("delete entity {},id is {}", entityClass.getSimpleName(), id);
	}

	/**
	 * 鎸塱d鑾峰彇瀵硅薄.
	 */
	public T get(final PK id) {
		Assert.notNull(id, "id涓嶈兘涓虹┖");
		return (T) getSession().load(entityClass, id);
	}

	/**
	 * 鎸塱d鍒楄〃鑾峰彇瀵硅薄鍒楄〃.
	 */
	public List<T> get(final Collection<PK> ids) {
		return find(Restrictions.in(getIdName(), ids));
	}

	/**
	 *	鑾峰彇鍏ㄩ儴瀵硅薄.
	 */
	public List<T> getAll() {
		return find();
	}

	/**
	 *	鑾峰彇鍏ㄩ儴瀵硅薄, 鏀寔鎸夊睘鎬ц搴�
	 */
	public List<T> getAll(String orderByProperty, boolean isAsc) {
		Criteria c = createCriteria();
		if (isAsc) {
			c.addOrder(Order.asc(orderByProperty));
		} else {
			c.addOrder(Order.desc(orderByProperty));
		}
		return c.list();
	}

	/**
	 * 鎸夊睘鎬ф煡鎵惧璞″垪琛� 鍖归厤鏂瑰紡涓虹浉绛�
	 */
	public List<T> findBy(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName涓嶈兘涓虹┖");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return find(criterion);
	}

	/**
	 * 鎸夊睘鎬ф煡鎵惧敮涓�璞� 鍖归厤鏂瑰紡涓虹浉绛�
	 */
	public T findUniqueBy(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName涓嶈兘涓虹┖");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return (T) createCriteria(criterion).uniqueResult();
	}

	/**
	 * 鎸塇QL鏌ヨ瀵硅薄鍒楄〃.
	 * 
	 * @param values 鏁伴噺鍙彉鐨勫弬鏁�鎸夐『搴忕粦瀹�
	 */
	public <X> List<X> find(final String hql, final Object... values) {
		return createQuery(hql, values).list();
	}

	/**
	 * 鎸塇QL鏌ヨ瀵硅薄鍒楄〃.
	 * 
	 * @param values 鍛藉悕鍙傛暟,鎸夊悕绉扮粦瀹�
	 */
	public <X> List<X> find(final String hql, final Map<String, ?> values) {
		return createQuery(hql, values).list();
	}

	/**
	 * 鎸塇QL鏌ヨ鍞竴瀵硅薄.
	 * 
	 * @param values 鏁伴噺鍙彉鐨勫弬鏁�鎸夐『搴忕粦瀹�
	 */
	public <X> X findUnique(final String hql, final Object... values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	/**
	 * 鎸塇QL鏌ヨ鍞竴瀵硅薄.
	 * 
	 * @param values 鍛藉悕鍙傛暟,鎸夊悕绉扮粦瀹�
	 */
	public <X> X findUnique(final String hql, final Map<String, ?> values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	/**
	 * 鎵цHQL杩涜鎵归噺淇敼/鍒犻櫎鎿嶄綔.
	 * 
	 * @param values 鏁伴噺鍙彉鐨勫弬鏁�鎸夐『搴忕粦瀹�
	 * @return 鏇存柊璁板綍鏁�
	 */
	public int batchExecute(final String hql, final Object... values) {
		return createQuery(hql, values).executeUpdate();
	}

	/**
	 * 鎵цHQL杩涜鎵归噺淇敼/鍒犻櫎鎿嶄綔.
	 * 
	 * @param values 鍛藉悕鍙傛暟,鎸夊悕绉扮粦瀹�
	 * @return 鏇存柊璁板綍鏁�
	 */
	public int batchExecute(final String hql, final Map<String, ?> values) {
		return createQuery(hql, values).executeUpdate();
	}

	/**
	 * 鏍规嵁鏌ヨHQL涓庡弬鏁板垪琛ㄥ垱寤篞uery瀵硅薄.
	 * 涓巉ind()鍑芥暟鍙繘琛屾洿鍔犵伒娲荤殑鎿嶄綔.
	 * 
	 * @param values 鏁伴噺鍙彉鐨勫弬鏁�鎸夐『搴忕粦瀹�
	 */
	public Query createQuery(final String queryString, final Object... values) {
		Assert.hasText(queryString, "queryString涓嶈兘涓虹┖");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}

	/**
	 * 鏍规嵁鏌ヨHQL涓庡弬鏁板垪琛ㄥ垱寤篞uery瀵硅薄.
	 * 涓巉ind()鍑芥暟鍙繘琛屾洿鍔犵伒娲荤殑鎿嶄綔.
	 * 
	 * @param values 鍛藉悕鍙傛暟,鎸夊悕绉扮粦瀹�
	 */
	public Query createQuery(final String queryString, final Map<String, ?> values) {
		Assert.hasText(queryString, "queryString涓嶈兘涓虹┖");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			query.setProperties(values);
		}
		return query;
	}

	/**
	 * 鎸塁riteria鏌ヨ瀵硅薄鍒楄〃.
	 * 
	 * @param criterions 鏁伴噺鍙彉鐨凜riterion.
	 */
	public List<T> find(final Criterion... criterions) {
		return createCriteria(criterions).list();
	}

	/**
	 * 鎸塁riteria鏌ヨ鍞竴瀵硅薄.
	 * 
	 * @param criterions 鏁伴噺鍙彉鐨凜riterion.
	 */
	public T findUnique(final Criterion... criterions) {
		return (T) createCriteria(criterions).uniqueResult();
	}

	/**
	 * 鏍规嵁Criterion鏉′欢鍒涘缓Criteria.
	 * 涓巉ind()鍑芥暟鍙繘琛屾洿鍔犵伒娲荤殑鎿嶄綔.
	 * 
	 * @param criterions 鏁伴噺鍙彉鐨凜riterion.
	 */
	public Criteria createCriteria(final Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}

	/**
	 * 鍒濆鍖栧璞�
	 * 浣跨敤load()鏂规硶寰楀埌鐨勪粎鏄璞roxy, 鍦ㄤ紶鍒癡iew灞傚墠闇�杩涜鍒濆鍖�
	 * 濡傛灉浼犲叆entity, 鍒欏彧鍒濆鍖杄ntity鐨勭洿鎺ュ睘鎬�浣嗕笉浼氬垵濮嬪寲寤惰繜鍔犺浇鐨勫叧鑱旈泦鍚堝拰灞炴�.
	 * 濡傞渶鍒濆鍖栧叧鑱斿睘鎬�闇�墽琛�
	 * Hibernate.initialize(user.getRoles())锛屽垵濮嬪寲User鐨勭洿鎺ュ睘鎬у拰鍏宠仈闆嗗悎.
	 * Hibernate.initialize(user.getDescription())锛屽垵濮嬪寲User鐨勭洿鎺ュ睘鎬у拰寤惰繜鍔犺浇鐨凞escription灞炴�.
	 */
	public void initProxyObject(Object proxy) {
		Hibernate.initialize(proxy);
	}

	/**
	 * Flush褰撳墠Session.
	 */
	public void flush() {
		getSession().flush();
	}

	/**
	 * 涓篞uery娣诲姞distinct transformer.
	 * 棰勫姞杞藉叧鑱斿璞＄殑HQL浼氬紩璧蜂富瀵硅薄閲嶅, 闇�杩涜distinct澶勭悊.
	 */
	public Query distinct(Query query) {
		query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return query;
	}

	/**
	 * 涓篊riteria娣诲姞distinct transformer.
	 * 棰勫姞杞藉叧鑱斿璞＄殑HQL浼氬紩璧蜂富瀵硅薄閲嶅, 闇�杩涜distinct澶勭悊.
	 */
	public Criteria distinct(Criteria criteria) {
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria;
	}

	/**
	 * 鍙栧緱瀵硅薄鐨勪富閿悕.
	 */
	public String getIdName() {
		ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
		return meta.getIdentifierPropertyName();
	}

	/**
	 * 鍒ゆ柇瀵硅薄鐨勫睘鎬у�鍦ㄦ暟鎹簱鍐呮槸鍚﹀敮涓�
	 * 
	 * 鍦ㄤ慨鏀瑰璞＄殑鎯呮櫙涓�濡傛灉灞炴�鏂颁慨鏀圭殑鍊�value)绛変簬灞炴�鍘熸潵鐨勫�(orgValue)鍒欎笉浣滄瘮杈�
	 */
	public boolean isPropertyUnique(final String propertyName, final Object newValue, final Object oldValue) {
		if (newValue == null || newValue.equals(oldValue)) {
			return true;
		}
		Object object = findUniqueBy(propertyName, newValue);
		return (object == null);
	}
}