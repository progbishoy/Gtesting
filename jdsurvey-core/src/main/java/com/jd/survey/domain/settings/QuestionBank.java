/*Copyright (C) 2017  CROSS WORKERS, Inc.

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.

  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.jd.survey.domain.settings;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.jd.survey.domain.security.User;
import com.jd.survey.util.SortedSetUpdater;


import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * Created by ags on 9/19/2017.
 */



@Entity
@NamedQueries({
        @NamedQuery(name = "QuestionBank.findAll", query = "select o from QuestionBank o order by o.id desc "),
        @NamedQuery(name = "QuestionBank.findById", query = "select o from QuestionBank o where o.id = ?1"),
        @NamedQuery(name = "QuestionBank.findByTagAndDifficulty", query = "select o from QuestionBank o where o.questionTag=?1 and o.difficulty = ?2 order by rand()")
})
public class QuestionBank extends SortedSetUpdater<QuestionBankOption> implements Comparable <QuestionBank>,  Serializable, SortedSetUpdater.InrementableCompartator {

    /**
     *
     */
    private static final long serialVersionUID = 893573304272858465L;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Integer version;


    @NotNull
    @NotEmpty
    @Size(max = 2000)
    @Column(length = 2000, nullable= false)
    private String questionText;


    @NotNull
    @NotEmpty
    @Size(max = 2000)
    @Column(length = 2000, nullable= false)
    private String questionAnswer;


    private Integer questionGrade;

    // Used to stored QuestionBanktext piped content 
    @Transient
    private String questionLabel;

    @Size(max = 750)
    @Column(length = 750, nullable= true)
    private String multimediaLink;

    private Integer height;
    private Integer width;

    private Boolean allowOtherTextBox;

    private Boolean required;
    private Boolean visible;

    private Integer integerMinimum;
    private Integer integerMaximum;

    private BigDecimal decimalMinimum;
    private BigDecimal decimalMaximum;

    private Boolean randomizeOptions = false;


    @DateTimeFormat(pattern="#{messages['date_format']}")
    private Date dateMinimum;


    @DateTimeFormat(pattern="#{messages['date_format']}")
    private Date dateMaximum;


    @Size(max = 2000)
    @Column(length = 2000, nullable= true)
    private String tip;


    @Size(max = 250)
    @Column(length = 250, nullable= true)
    private String regularExpression;


    private Long dataSetId;


    @Enumerated(EnumType.STRING)
    private QuestionType type = QuestionType.SHORT_TEXT_INPUT;

    @Enumerated(EnumType.STRING)
    private QuestionDirection direction = QuestionDirection.VERTICAL;



    @Enumerated(EnumType.STRING)
    private QuestionDifficultyLevel difficulty = QuestionDifficultyLevel.EASY;





    @OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.EAGER, mappedBy="question")
    //@OneToMany(orphanRemoval=true,fetch=FetchType.EAGER, mappedBy="question")
    @Sort(type = SortType.NATURAL)
    private SortedSet<QuestionBankOption> options = new TreeSet<QuestionBankOption>();


    @OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.EAGER, mappedBy="question")
    //@OneToMany(orphanRemoval=true,fetch=FetchType.EAGER, mappedBy="question")
    @Sort(type = SortType.NATURAL)
    private SortedSet<QuestionRowLabel> rowLabels = new TreeSet<QuestionRowLabel>();


    @OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.EAGER, mappedBy="question")
    //@OneToMany(orphanRemoval=true,fetch=FetchType.EAGER, mappedBy="question")
    @Sort(type = SortType.NATURAL)
    private SortedSet<QuestionColumnLabel> columnLabels = new TreeSet<QuestionColumnLabel>();

    @ManyToOne()
    @JoinColumn(name="tag_id")
    private Tags questionTag = new Tags();

    @ManyToOne()
    @JoinColumn(name="created_user_id" ,updatable = false)
    private User createdBy ;


    @Column(updatable=false)
    private Date createdDate ;

    @ManyToOne()
    @JoinColumn(name="modified_user_id")
    private User modifiedBy ;

    @Column
    private Date modifiedDate ;

    @Column
    @Enumerated(EnumType.STRING)
    private QuestionBankStatus status;

    @Column
    private Integer duration;



    @Transient
    @Valid
    private List<QuestionBankOption> optionsList = new ArrayList<QuestionBankOption>();

    /**
     * Spring is not using the setter on the optionsList field when binding the optionsList on the post. This field is a  work arround below is a fix to that!!!
     * Not pretty and needs to be revisited to see if there is a better way to do this
     */
    @Transient
    @Valid
    private List<QuestionBankOption> optionsList2 = new ArrayList<QuestionBankOption>();

    @Transient
    @Valid
    private List<QuestionRowLabel> rowLabelsList = new ArrayList<QuestionRowLabel>();

    @Transient
    @Valid
    private List<QuestionColumnLabel> columnLabelsList = new ArrayList<QuestionColumnLabel>();

    @Transient
    private String otherTag;


    @Transient
    private Set<DataSetItem> dataSetItems;


    public QuestionBank() {
        super();
        this.visible=true;
        // TODO Auto-generated constructor stub
    }
    public QuestionBank(Question q) {

        super();
        this.version=q.getVersion();

        this.questionText=q.getQuestionText();
        this.questionAnswer=q.getQuestionAnswer();
        this.questionGrade=q.getQuestionGrade();
        this.questionLabel=q.getQuestionLabel();
        this.multimediaLink=q.getMultimediaLink();
        this.height=q.getHeight();
        this.width=q.getWidth();
        this.allowOtherTextBox=q.getAllowOtherTextBox();
        this.required=q.getRequired();
        this.visible=q.getVisible();
        this.integerMinimum=q.getIntegerMinimum();
        this.integerMaximum=q.getIntegerMaximum();
        this.decimalMinimum=q.getDecimalMinimum();
        this.decimalMaximum=q.getDecimalMaximum();
        this.randomizeOptions =q.getRandomizeOptions();
        this.dateMinimum=q.getDateMinimum();
        this.dateMaximum=q.getDateMaximum();
        this.tip=q.getTip();
        this.regularExpression=q.getRegularExpression();
        this.dataSetId=q.getDataSetId();
        this.type =q.getType();
        this.direction = q.getDirection();
        this.rowLabels = q.getRowLabels();
        this.columnLabels = q.getColumnLabels();
        this.rowLabelsList = q.getRowLabelsList();
        this.columnLabelsList = q.getColumnLabelsList();
        this.dataSetItems=q.getDataSetItems();
        this.visible=q.getVisible();


    }

    public QuestionBank(QuestionBank question,int i) {
        super();
        this.visible=true;
        this.questionText=question.getQuestionText();
        this.questionLabel=question.getQuestionLabel();
        this.multimediaLink=question.getMultimediaLink();
        this.height=question.getHeight();
        this.width=question.getWidth();
        this.allowOtherTextBox=question.getAllowOtherTextBox();
        this.required=question.getRequired();
        this.visible=question.getVisible();
        this.type=question.getType();



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
    public Short getOrder() {return new Short("");}
    public void setOrder(Short order) {}


    public String getQuestionText() {
        if (questionText== null) {return null;}
        else {return questionText.trim();}
    }
    public void setQuestionText(String questionText) {
        if (questionText== null) {this.questionText= null;}
        else
        {this.questionText = questionText.trim();}
    }

    public String getQuestionLabel() {
        if (questionLabel!= null) {
            return questionLabel;
        }
        else{
            return questionText;
        }
    }


    public void setQuestionLabel(String questionLabel) {
        this.questionLabel = questionLabel;
    }


    public Boolean getAllowOtherTextBox() {
        return allowOtherTextBox;
    }
    public void setAllowOtherTextBox(Boolean allowOtherTextBox) {
        this.allowOtherTextBox = allowOtherTextBox;
    }
    public Boolean getVisible() {
        return visible;
    }
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Long getDataSetId() {
        return dataSetId;
    }


    public void setDataSetId(Long dataSetId) {
        this.dataSetId = dataSetId;
    }


    public Boolean getRequired() {
        return required;
    }


    public void setRequired(Boolean required) {
        this.required = required;
    }



    public Integer getIntegerMinimum() {
        return integerMinimum;
    }


    public void setIntegerMinimum(Integer integerMinimum) {
        this.integerMinimum = integerMinimum;
    }


    public Integer getIntegerMaximum() {
        return integerMaximum;
    }


    public void setIntegerMaximum(Integer integerMaximum) {
        this.integerMaximum = integerMaximum;
    }



    public BigDecimal getDecimalMinimum() {
        return decimalMinimum;
    }


    public void setDecimalMinimum(BigDecimal decimalMinimum) {
        this.decimalMinimum = decimalMinimum;
    }


    public BigDecimal getDecimalMaximum() {
        return decimalMaximum;
    }


    public void setDecimalMaximum(BigDecimal decimalMaximum) {
        this.decimalMaximum = decimalMaximum;
    }


    public Date getDateMinimum() {
        return dateMinimum;
    }


    public void setDateMinimum(Date dateMinimum) {
        this.dateMinimum = dateMinimum;
    }


    public Date getDateMaximum() {
        return dateMaximum;
    }


    public void setDateMaximum(Date dateMaximum) {
        this.dateMaximum = dateMaximum;
    }


    public String getTip() {
        if (tip == null) {return null;}
        else{return tip.trim();}

    }

    public void setTip(String tip) {
        if (tip == null) {this.tip= null;}
        else {this.tip = tip.trim();}
    }




    public String getMultimediaLink() {
        return multimediaLink;
    }


    public void setMultimediaLink(String multimediaLink) {
        //multimediaLink = multimediaLink.replaceAll("width=\"([^\"]*)\"", "");
        //multimediaLink = multimediaLink.replaceAll("width='([^']*)'", "");

        //multimediaLink = multimediaLink.replaceAll("height=\"([^\"]*)\"", "");
        //multimediaLink = multimediaLink.replaceAll("height='([^']*)'", "");

        this.multimediaLink = multimediaLink;
    }


    public Integer getHeight() {
        return height;
    }


    public void setHeight(Integer height) {
        this.height = height;
    }


    public Integer getWidth() {
        return width;
    }


    public void setWidth(Integer width) {
        this.width = width;
    }



    public QuestionType getType() {
        return type;
    }
    public void setType(QuestionType type) {
        this.type = type;
    }

    public String getTwoDigitPageOrder() {
        return "0";
    }


    public QuestionDirection getDirection() {
        return direction;
    }


    public void setDirection(QuestionDirection direction) {
        this.direction = direction;
    }


    public SortedSet<QuestionBankOption> getOptions() {
        return options;
    }
    public void setOptions(SortedSet<QuestionBankOption> options) {
        this.options = options;
        if (options !=null){
            short i= 1;
            Iterator<QuestionBankOption> it;
            it = options.iterator();
            while (it.hasNext()) {
                QuestionBankOption questionOption  = it.next();
                questionOption.setOrder(i);
                optionsList.add(questionOption);
                optionsList2.add(questionOption);
                i++;
            }
        }

    }


    public SortedSet<QuestionRowLabel> getRowLabels() {
        return rowLabels;
    }
    public void setRowLabels(SortedSet<QuestionRowLabel> rowLabels) {
        this.rowLabels = rowLabels;
        if (rowLabels !=null){
            short i= 1;
            Iterator<QuestionRowLabel> it;
            it = rowLabels.iterator();
            while (it.hasNext()) {
                QuestionRowLabel questionRowLabel  = it.next();
                questionRowLabel.setOrder(i);
                rowLabelsList.add(questionRowLabel);
                i++;
            }
        }

    }

    public SortedSet<QuestionColumnLabel> getColumnLabels() {
        return columnLabels;
    }
    public void setColumnLabels(SortedSet<QuestionColumnLabel> columnLabels) {
        this.columnLabels = columnLabels;
        if (columnLabels !=null){
            short i= 1;
            Iterator<QuestionColumnLabel> it;
            it = columnLabels.iterator();
            while (it.hasNext()) {
                QuestionColumnLabel questionColumnLabel  = it.next();
                questionColumnLabel.setOrder(i);
                columnLabelsList.add(questionColumnLabel);
                i++;
            }
        }

    }


    public List<QuestionBankOption> getOptionsList() {
        if (optionsList != null && optionsList.size()>0) {
            return optionsList;
        } else {
            return new ArrayList<QuestionBankOption>(this.options);
        }
    }
    public void setOptionsList(List<QuestionBankOption> optionsList) {
        this.optionsList = optionsList;
    }

    public List<QuestionRowLabel> getRowLabelsList() {
        return rowLabelsList;
    }
    public void setRowLabelsList(List<QuestionRowLabel> rowLabelsList) {
        this.rowLabelsList = rowLabelsList;
    }

    public List<QuestionColumnLabel> getColumnLabelsList() {
        return columnLabelsList;
    }
    public void setColumnLabelsList(List<QuestionColumnLabel> columnLabelsList) {
        this.columnLabelsList = columnLabelsList;
    }








    public Set<DataSetItem> getDataSetItems() {
        return dataSetItems;
    }


    public void setDataSetItems(Set<DataSetItem> dataSetItems) {
        this.dataSetItems = dataSetItems;
    }


    public boolean getSuportsOptions() {
        if (this.type == QuestionType.SINGLE_CHOICE_DROP_DOWN ||
                this.type == QuestionType.MULTIPLE_CHOICE_CHECKBOXES ||
                this.type == QuestionType.STAR_RATING ||
                this.type == QuestionType.SMILEY_FACES_RATING ||
                this.type == QuestionType.SINGLE_CHOICE_RADIO_BUTTONS){
            return true;
        }
        else{
            return false;
        }
    }


    public String toString() {
        return "question:" + this.id + " " + this.questionText;
    }












    //comparable interface
    @Override
    public int compareTo(QuestionBank that) {
        final int BEFORE = -1;
        final int AFTER = 1;
        if (that == null) {
            return BEFORE;
        }
        Comparable<Long> thisQuestionePage = this.getId();
        Comparable<Long> thatQuestionPage = that.getId();
        if(thisQuestionePage == null) {
            return AFTER;
        } else if(thatQuestionPage == null) {
            return BEFORE;
        } else {
            return thisQuestionePage.compareTo(that.getId());
        }
    }




    public String getRegularExpression() {
        return regularExpression;
    }


    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }


    public Boolean getRandomizeOptions() {
        return randomizeOptions;
    }


    public void setRandomizeOptions(Boolean randomizeOptions) {
        this.randomizeOptions = randomizeOptions;
    }


    public List<QuestionBankOption> getOptionsList2() {
        return optionsList2;
    }


    public void setOptionsList2(List<QuestionBankOption> optionsList2) {
        this.optionsList2 = optionsList2;
    }

    public String getQuestionAnswer() {
        if (questionAnswer== null) {return null;}
        else {return questionAnswer.trim();}
    }
    public void setQuestionAnswer(String questionAnswer) {
        if (questionAnswer== null) {this.questionAnswer= null;}
        else
        {this.questionAnswer = questionAnswer.trim();}
    }

    public Integer getQuestionGrade() {
        return questionGrade;
    }

    public void setQuestionGrade(Integer questionGrade) {
        this.questionGrade = questionGrade;
    }

    public Tags getQuestionTag() {
        return questionTag;
    }

    public void setQuestionTag(Tags questionTag) {
        this.questionTag = questionTag;
    }

    public QuestionDifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuestionDifficultyLevel difficulty) {
        this.difficulty = difficulty;
    }

    public String getOtherTag() {
        return otherTag;
    }

    public void setOtherTag(String otherTag) {
        this.otherTag = otherTag;
    }


    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public QuestionBankStatus getStatus() {
        return status;
    }

    public void setStatus(QuestionBankStatus status) {
        this.status = status;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
