package com.jd.survey.domain.settings;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by ags on 9/19/2017.
 */
public class SurveyCreationCriterias {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuestionMainCategory MainCategory=QuestionMainCategory.ENGLISH;

    @Enumerated(EnumType.STRING)
    private QuestionTechnology Technology=QuestionTechnology.JAVA;

    @Enumerated(EnumType.STRING)
    private QuestionLevel Level=QuestionLevel.JUNIOR;

    @Enumerated(EnumType.STRING)
    private QuestionDifficultyLevel DifficultyLevel=QuestionDifficultyLevel.EASY;

    @Enumerated(EnumType.STRING)
    private QuestionNumericalDegree NumericalDegree=QuestionNumericalDegree.ONE;

}
