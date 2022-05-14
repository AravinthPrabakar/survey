package com.market.logic.survey.app.service;

import com.market.logic.survey.app.model.ResponseChart;
import com.market.logic.survey.app.model.Question;

public interface AdminService {

    /**
     * Create a new Survey
     * @return surveyId
     */
    String createSurvey();


    /**
     * Get a question
     * @param surveyId
     * @param questionId
     * @return question
     */
    Question getQuestion(String surveyId, String questionId);


    /**
     * Add a new question
     * @param surveyId
     * @param question
     * @return Question
     */
    Question addQuestion(String surveyId, Question question);


    /**
     * update a question
     * @param surveyId
     * @param questionId
     * @param question
     * @return QUESTION
     */
    Question updateQuestion(String surveyId, String questionId, Question question);



    /**
     * delete a question
     * @param surveyId
     * @param questionId
     * @return TRUE/FALSE
     */
    Boolean deleteQuestion(String surveyId, String questionId);


    /**
     * delete a question
     * @param surveyId
     * @param questionId
     * @return Responsechart
     */
    ResponseChart generateDistributionChart(String surveyId, String questionId);


}
