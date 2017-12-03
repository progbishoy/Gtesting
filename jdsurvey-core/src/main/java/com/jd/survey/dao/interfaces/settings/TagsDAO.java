package com.jd.survey.dao.interfaces.settings;

import com.jd.survey.domain.settings.Department;
import com.jd.survey.domain.settings.Tags;
import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;

import java.util.Set;
import java.util.SortedSet;

/**
 * Created by ags on 10/8/2017.
 */
public interface TagsDAO extends JpaDao<Tags> {

         Set<Tags> findAll() throws DataAccessException;
         Tags findById(Long id) throws DataAccessException;
         Tags findByTagName(String Name) throws DataAccessException;
         Set<Tags> findByDepartments(SortedSet<Department> departments) throws DataAccessException;

    }


