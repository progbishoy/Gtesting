package com.jd.survey.dto;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import com.jd.survey.domain.settings.QuestionBankStatus;
import com.jd.survey.domain.settings.QuestionDifficultyLevel;
import com.jd.survey.domain.settings.QuestionType;
import com.jd.survey.domain.settings.Tags;

public class QuestionHolder {

	String questionText="";
	 String questionAnswer="";
	 Integer questionGrade;
	 Tags questionTag;
	@Transient
	 String tStatus="";
	@Column
	@Enumerated(EnumType.STRING)
	 QuestionBankStatus status;
	@Enumerated(EnumType.STRING)
	 QuestionType type = QuestionType.SHORT_TEXT_INPUT;
	@Enumerated(EnumType.STRING)
	 QuestionDifficultyLevel difficulty = QuestionDifficultyLevel.EASY;

	@Transient
	 String tDifficulty="";
	@Transient
	 String tType="";
	Long id;
	String selected;

	
	
	public QuestionHolder() {
		super();
	}

	public QuestionHolder(String questionText, String questionAnswer, Integer questionGrade, Tags questionTag,
			QuestionBankStatus status, QuestionType type, QuestionDifficultyLevel difficulty,  Long id) {
		super();
		this.questionText = questionText;
		this.questionAnswer = questionAnswer;
		this.questionGrade = questionGrade;
		this.questionTag = questionTag;
		this.status = status;
		this.type = type;
		this.difficulty = difficulty;	
		this.id = id;
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getQuestionAnswer() {
		return questionAnswer;
	}

	public void setQuestionAnswer(String questionAnswer) {
		this.questionAnswer = questionAnswer;
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

	public String gettStatus() {
		return tStatus;
	}

	public void settStatus(String tStatus) {
		this.tStatus = tStatus;
	}

	public QuestionBankStatus getStatus() {
		return status;
	}

	public void setStatus(QuestionBankStatus status) {
		this.status = status;
	}

	public QuestionType getType() {
		return type;
	}

	public void setType(QuestionType type) {
		this.type = type;
	}

	public QuestionDifficultyLevel getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(QuestionDifficultyLevel difficulty) {
		this.difficulty = difficulty;
	}

	public String gettDifficulty() {
		return tDifficulty;
	}

	public void settDifficulty(String tDifficulty) {
		this.tDifficulty = tDifficulty;
	}

	public String gettType() {
		return tType;
	}

	public void settType(String tType) {
		this.tType = tType;
	}

}
