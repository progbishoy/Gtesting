package com.jd.survey.domain.settings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.jd.survey.util.SortedSetUpdater;
/**
 * Created by ags on 10/9/2017.
 */

@Entity
@NamedQueries({
        @NamedQuery(name = "QuestionBankOption.findAll", query = "select o from QuestionBankOption o"),
        @NamedQuery(name = "QuestionBankOption.findById", query = "select o from QuestionBankOption o where o.id = ?1"),
        @NamedQuery(name = "QuestionBankOption.getCount", query = "select count(o) from QuestionBankOption o"),
        @NamedQuery(name = "QuestionBankOption.deleteByQuestionId", query = "delete from QuestionBankOption o where o.question.id=?1"),
        @NamedQuery(name = "QuestionBankOption.findByQuestionId", query = "select o from QuestionBankOption o where o.question.id=?1")
})
public class QuestionBankOption implements Comparable <com.jd.survey.domain.settings.QuestionBankOption>, Serializable, SortedSetUpdater.InrementableCompartator{
    

        private static final long serialVersionUID = 5689804369411211023L;

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "id")
        private Long id;

        @Version
        @Column(name = "version")
        private Integer version;

        @NotNull
        @NotEmpty
        @Size(max = 50)
        @Column(name = "OPTION_VALUE",length = 50, nullable= false)
        private String value;


        @NotNull
        @NotEmpty
        @Size(max = 250)
        @Column(name = "OPTION_TEXT",length = 250, nullable= false)
        private String text;

        @NotNull
        @Column(name = "OPTION_ORDER")
        private Short order;

        @NotNull
        @ManyToOne
        @JoinColumn(name = "QUESTION_ID")
        private QuestionBank question;


        @Column(name = "RIGHT_OR_WRONG")
        private boolean right;




        public QuestionBankOption() {
            super();
            // TODO Auto-generated constructor stub
        }



        public QuestionBankOption(QuestionBank question) {
            super();
            this.question = question;
            this.order = (short) (question.getOptions().size() +1);

        }

        public QuestionBankOption(QuestionBank question, Short order) {
            super();
            this.question = question;
            this.order = order;
        }

        public QuestionBankOption(QuestionBank question, Short order, String value, String text) {
            super();
            this.question = question;
            this.order = order;
            this.value= value;
            this.text= text;
        }
    public QuestionBankOption(QuestionBank question, Short order, String value, String text,boolean right) {
        super();
        this.question = question;
        this.order = order;
        this.value= value;
        this.text= text;
        this.right=right;
    }

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        public Integer getVersion() {
            return version;
        }
        public void setVersion(Integer version) {
            this.version = version;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public String getText() {
            return text;
        }
        public void setText(String text) {
            this.text = text;
        }
        public Short getOrder() {
            return order;
        }
        public void setOrder(Short order) {
            this.order = order;
        }


        public QuestionBank getQuestion() {
            return question;
        }
        public void setQuestion(QuestionBank question) {
            this.question = question;
        }


        public String toString() {
            return this.text;
        }




        //comparable interface
        @Override
        public int compareTo(com.jd.survey.domain.settings.QuestionBankOption that) {

            final int BEFORE = -1;
            final int AFTER = 1;
            if (that == null) {
                return BEFORE;
            }
            Comparable<Short> thisQuestionBankOptionPage = this.getOrder();
            Comparable<Short> thatQuestionBankOptionPage = that.getOrder();
            if(thisQuestionBankOptionPage == null) {
                return AFTER;
            } else if(thatQuestionBankOptionPage == null) {
                return BEFORE;
            } else {
                return thisQuestionBankOptionPage.compareTo(that.getOrder());
            }
        }


    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }
}


