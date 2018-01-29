package com.jd.survey.dto;

import java.util.List;

public class QuestionForm {
	private List<QuestionHolder> questions;

	public List<QuestionHolder> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionHolder> questions) {
		this.questions = questions;
	}

}
