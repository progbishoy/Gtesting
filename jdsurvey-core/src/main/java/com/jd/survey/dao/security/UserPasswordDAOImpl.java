package com.jd.survey.dao.security;

import com.jd.survey.dao.interfaces.security.UserPasswordDAO;
import com.jd.survey.domain.security.UserPassword;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.skyway.spring.util.dao.AbstractJpaDao;

import org.springframework.dao.DataAccessException;

import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

/** DAO implementation to handle persistence for object :User
*/
@Repository("UserPasswordDAO")
@Transactional
public class UserPasswordDAOImpl extends AbstractJpaDao<UserPassword> implements	UserPasswordDAO {
private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { UserPassword.class }));

@PersistenceContext(unitName = "persistenceUnit")
private EntityManager entityManager;

public UserPasswordDAOImpl() {
	super();
}

public EntityManager getEntityManager() {
	return entityManager;
}

public Set<Class<?>> getTypes() {
	return dataTypes;
}

@Transactional
public Set<UserPassword> findAll() throws DataAccessException {
	return findAll(-1, -1);
}

@SuppressWarnings("unchecked")
@Transactional
public Set<UserPassword> findAll(int startResult, int maxRows)	throws DataAccessException {
	Query query = createNamedQuery("User.findAll", startResult,maxRows);
	return new LinkedHashSet<UserPassword>(query.getResultList());
}


@Transactional
public UserPassword findById(Long id) throws DataAccessException {
	try {
		Query query = createNamedQuery("UserPassword.findById", -1, -1, id);
		return (UserPassword) query.getSingleResult();
	} catch (NoResultException nre) {
		
		return null;
	}

}

@Transactional
public Long getCount() throws DataAccessException {
	try {
		Query query = createNamedQuery("UserPassword.getCount",-1,-1);
		return  (Long) query.getSingleResult();
	} catch (NoResultException nre) {
		
		return null;
	}
}


@Override
public boolean canBeMerged(UserPassword o) {
	return true;
}





}