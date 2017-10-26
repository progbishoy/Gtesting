package com.jd.survey.dao.interfaces.settings;

import com.jd.survey.domain.settings.Tags;
import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;

import java.util.Set;

/**
 * Created by ags on 10/8/2017.
 */
public interface TagsDAO extends JpaDao<Tags> {

         Set<Tags> findAll() throws DataAccessException;
         Tags findById(Long id) throws DataAccessException;
         Tags findByTagName(String Name) throws DataAccessException;

    }


