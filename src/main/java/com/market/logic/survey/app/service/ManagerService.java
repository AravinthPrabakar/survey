package com.market.logic.survey.app.service;

import com.market.logic.survey.app.model.Question;

import java.util.List;
import java.util.Map;

public interface ManagerService {
    /**
     * Get the list of all questions for a survey
     * @param surveyId
     * @return List of Questions
     */
    List<Question> listQuestions(String surveyId);

    /**
     * Create a survey with list of questions
     * @param surveyId
     * @param response
     * @return
     */
    Boolean submitSurvey(String surveyId, Map<String,String> response);

}
