package com.market.logic.survey.app.dao;


import com.market.logic.survey.app.model.Question;
import com.market.logic.survey.app.model.ResponseChart;

import java.util.List;
import java.util.Map;

public interface SurveyDao {

    /**
     * Create a new Survey
     * @return surveyId
     */
    String createSurvey();


    /**
     * Check the existence of Survey
     * @return TRUE/FALSE
     */
    Boolean surveyExist(String surveyId);
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
     * @param question
     * @return Question
     */
    Question updateQuestion(String surveyId, Question question);



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
