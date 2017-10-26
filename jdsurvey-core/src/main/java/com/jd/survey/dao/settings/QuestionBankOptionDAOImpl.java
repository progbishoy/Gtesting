package com.jd.survey.dao.settings;

import com.jd.survey.dao.interfaces.settings.QuestionBankOptionDAO;
import com.jd.survey.dao.interfaces.settings.QuestionOptionDAO;
import com.jd.survey.domain.settings.QuestionBank;
import com.jd.survey.domain.settings.QuestionBankOption;
import com.jd.survey.domain.settings.QuestionOption;
import com.jd.survey.domain.settings.SurveyDefinitionPage;

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
/**
 * Created by ags on 10/9/2017.
 */
    @Repository("QuestionBankOptionDAO")
    @Transactional
    public class QuestionBankOptionDAOImpl extends AbstractJpaDao<QuestionBankOption> implements QuestionBankOptionDAO {

        private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { QuestionBankOption.class }));

        @PersistenceContext(unitName = "persistenceUnit")
        private EntityManager entityManager;

        public QuestionBankOptionDAOImpl() {
            super();
        }

        public EntityManager getEntityManager() {
            return entityManager;
        }

        public Set<Class<?>> getTypes() {
            return dataTypes;
        }

        @Transactional
        public Set<QuestionBankOption> findAll() throws DataAccessException {
            return findAll(-1, -1);
        }

        @SuppressWarnings("unchecked")
        @Transactional
        public Set<QuestionBankOption> findAll(int startResult, int maxRows)	throws DataAccessException {
            Query query = createNamedQuery("QuestionBankOption.findAll", startResult,maxRows);
            return new LinkedHashSet<QuestionBankOption>(query.getResultList());
        }

        @Transactional
        public QuestionBankOption findById(Long id) throws DataAccessException {
            try {
                Query query = createNamedQuery("QuestionBankOption.findById", -1, -1, id);
                return (QuestionBankOption) query.getSingleResult();
            } catch (NoResultException nre) {
                return null;
            }

        }

        @SuppressWarnings("unchecked")
        @Transactional
        public Set<QuestionBankOption> findByQuestionId(QuestionBank qid)	throws DataAccessException {
            Query query = createNamedQuery("QuestionBankOption.findByQuestionId", -1,-1, qid.getId());
            return new LinkedHashSet<QuestionBankOption>(query.getResultList());
        }



        @Transactional
        public Long getCount() throws DataAccessException {
            try {
                Query query = createNamedQuery("QuestionBankOption.getCount",-1,-1);
                return  (Long) query.getSingleResult();
            } catch (NoResultException nre) {
                return null;
            }
        }

        public boolean canBeMerged(QuestionBankOption entity) {
            return true;
        }

        @Transactional
        public int deleteByQuestionId(Long id) throws DataAccessException {
            Query query = createNamedQuery("QuestionBankOption.deleteByQuestionId", 0, 0, id);
            return query.executeUpdate();
        }

    }


