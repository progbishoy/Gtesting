package com.jd.survey.dao.settings;

import com.jd.survey.dao.interfaces.settings.SurveyTagsDAO;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyTags;
import com.jd.survey.domain.settings.Tags;
import org.skyway.spring.util.dao.AbstractJpaDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by ags on 10/8/2017.
 */
    @Repository("SurveyTagsDAO")
    @Transactional
    public class SurveyTagsDAOImpl extends AbstractJpaDao<SurveyTags> implements SurveyTagsDAO {

        private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { SurveyTags.class }));
        @PersistenceContext(unitName = "persistenceUnit")
        private EntityManager entityManager;

        public SurveyTagsDAOImpl() {
            super();
        }

        public EntityManager getEntityManager() {
            return entityManager;
        }
        public Set<Class<?>> getTypes() {
            return dataTypes;
        }

        public Set<SurveyTags> findAll() {
            Query query = createNamedQuery("SurveyTags.findAll",-1,-1);
            return new LinkedHashSet<SurveyTags>(query.getResultList());
        }
        public Set<SurveyTags> findBySurveyId(SurveyDefinition id) {
            try {
                Query query = createNamedQuery("SurveyTags.findBySurveyId", -1, -1, id);
                return new LinkedHashSet<SurveyTags>(query.getResultList());
            } catch (NoResultException nre) {
                return null;
            }
        }
        public boolean canBeMerged(SurveyTags entity) {
            return true;
        }
    }


