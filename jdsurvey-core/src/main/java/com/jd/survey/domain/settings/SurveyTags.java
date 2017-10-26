package com.jd.survey.domain.settings;

import com.jd.survey.util.SortedSetUpdater;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by ags on 10/8/2017.
 */

@Entity
@NamedQueries({
        @NamedQuery(name = "SurveyTags.findAll", query = "select o from SurveyTags o"),
        @NamedQuery(name = "SurveyTags.findById", query = "select o from SurveyTags o where o.id = ?1"),
        @NamedQuery(name = "SurveyTags.findBySurveyId", query = "select o from SurveyTags o where o.surveyDefinition = ?1")
})
public class SurveyTags extends SortedSetUpdater<SurveyTags>
        implements Comparable <SurveyTags>, Serializable, SortedSetUpdater.InrementableCompartator	{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "SURVEY_DEFINITION_ID")
    private SurveyDefinition surveyDefinition;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "Tag_ID")
    private Tags tag;



    private Integer Easy;
    private Integer Medium;
    private Integer Hard;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SurveyDefinition getSurveyDefinition() {
        return surveyDefinition;
    }

    public void setSurveyDefinition(SurveyDefinition surveyDefinition) {
        this.surveyDefinition = surveyDefinition;
    }

    public Tags getTag() {
        return tag;
    }

    public void setTag(Tags tag) {
        this.tag = tag;
    }

    public Integer getEasy() {
        return Easy;
    }

    public void setEasy(Integer easy) {
        Easy = easy;
    }

    public Integer getMedium() {
        return Medium;
    }

    public void setMedium(Integer medium) {
        Medium = medium;
    }

    public Integer getHard() {
        return Hard;
    }

    public void setHard(Integer hard) {
        Hard = hard;
    }








    public void setOrder(Short order) {

    }

    public Short getOrder() {
        return 0;
    }



    @Override
    public int compareTo(SurveyTags that) {
        final int BEFORE = -1;
        final int AFTER = 1;
        if (that == null) {
            return BEFORE;
        }
        Comparable<Long> thisSurveyTag = this.getId();
        Comparable<Long> thatSurveyTag = that.getId();
        if(thisSurveyTag == null) {
            return AFTER;
        } else if(thatSurveyTag == null) {
            return BEFORE;
        } else {
            return thisSurveyTag.compareTo(that.getId());
        }
    }
}
