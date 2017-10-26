package com.jd.survey.dao.settings;

import com.jd.survey.dao.interfaces.settings.TagsDAO;
import com.jd.survey.domain.settings.Sector;
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
@Repository("TagsDAO")
@Transactional
public class TagsDAOImpl extends AbstractJpaDao<Tags> implements TagsDAO {
    private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Tags.class }));
    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    public TagsDAOImpl() {
        super();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
    public Set<Class<?>> getTypes() {
        return dataTypes;
    }

        public Set<Tags> findAll() {
            Query query = createNamedQuery("Tags.findAll",-1,-1);
            return new LinkedHashSet<Tags>(query.getResultList());
        }
    public Tags findById(Long id) {
        try {
            Query query = createNamedQuery("Tags.findById", -1, -1, id);
            return (Tags) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    public Tags findByTagName(String name) {
        try {
            Query query = createNamedQuery("Tags.findByTagName", -1, -1, name);
            return (Tags) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    public boolean canBeMerged(Tags entity) {
        return true;
    }
}
