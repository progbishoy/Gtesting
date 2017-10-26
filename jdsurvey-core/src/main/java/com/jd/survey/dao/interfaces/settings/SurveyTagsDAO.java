package com.jd.survey.dao.interfaces.settings;

import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyTags;
import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;

import java.util.Set;

/**
 * Created by ags on 10/8/2017.
 */
public interface SurveyTagsDAO extends JpaDao<SurveyTags> {
    public Set<SurveyTags> findAll() throws DataAccessException;
    public Set<SurveyTags> findBySurveyId(SurveyDefinition surveyDefinition) throws DataAccessException;
}

