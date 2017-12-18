package com.jd.survey.dao.interfaces.security;
import com.jd.survey.domain.security.UserPassword;

import java.lang.Long;
import java.util.Set;


import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;

/**
*/
public interface UserPasswordDAO extends JpaDao<UserPassword> {
public Set<UserPassword> findAll() throws DataAccessException;
public Set<UserPassword> findAll(int startResult, int maxRows) throws DataAccessException;



public UserPassword findById(Long id) throws DataAccessException;
public Long getCount() throws DataAccessException;


}