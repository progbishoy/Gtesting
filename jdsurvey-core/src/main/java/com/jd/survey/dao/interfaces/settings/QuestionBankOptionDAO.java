package com.jd.survey.dao.interfaces.settings;
import java.util.Set;

import com.jd.survey.domain.settings.QuestionBank;
import com.jd.survey.domain.settings.QuestionBankOption;
import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;

/**
 * Created by ags on 10/9/2017.
 */
public interface QuestionBankOptionDAO extends JpaDao<QuestionBankOption> {


        public Set<QuestionBankOption> findAll() throws DataAccessException;
        public Set<QuestionBankOption> findAll(int startResult, int maxRows) throws DataAccessException;
        public QuestionBankOption findById(Long id) throws DataAccessException;
        public Long getCount() throws DataAccessException;
        public int deleteByQuestionId(Long id) throws DataAccessException;
        public Set<QuestionBankOption> findByQuestionId(QuestionBank id) throws DataAccessException;

}
