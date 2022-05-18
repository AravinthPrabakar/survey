package com.market.logic.survey.app.dao;

import com.market.logic.survey.app.data.DataManager;
import com.market.logic.survey.app.model.Question;
import com.market.logic.survey.app.model.ResponseChart;
import com.market.logic.survey.app.model.Survey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SurveyDaoImpl implements SurveyDao{

    private final DataManager dataManager;

    @Autowired
    SurveyDaoImpl(DataManager dataManager){
        this.dataManager  = dataManager;
    }


    /**
     * Create a new Survey
     *
     * @return surveyId
     */
    @Override
    public String createSurvey() {
        return dataManager.createSurvey();
    }


    /**
     * Create a new Survey
     *
     * @return surveyId
     */
    @Override
    public Boolean surveyExist(String surveyId) {
        return dataManager.getSurvey(surveyId) == null ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * Get a question
     *
     * @param surveyId
     * @param questionId
     * @return question
     */
    @Override
    public Question getQuestion(String surveyId, String questionId) {
        return dataManager.getSurvey(surveyId).getQuestion(questionId);
    }

    /**
     * Add a new question
     *
     * @param surveyId
     * @param question
     * @return Question
     */
    @Override
    public Question addQuestion(String surveyId, Question question) {
        Survey survey = dataManager.getSurvey(surveyId);
        if(survey == null){
            survey = new Survey(surveyId);
            dataManager.addSurvey(survey);
        }
        question.setQuestionId(String.valueOf(dataManager.getNextQuestionSequence()));
        return dataManager.getSurvey(surveyId).addQuestion(question);
    }

    /**
     * update a question
     *
     * @param surveyId
     * @param question
     * @return
     */
    @Override
    public Question updateQuestion(String surveyId, Question question) {
        Survey survey = dataManager.getSurvey(surveyId);
        if(survey == null){
            survey = new Survey(surveyId);
            dataManager.addSurvey(survey);
        }
        return dataManager.getSurvey(surveyId).addQuestion(question);
    }

    /**
     * delete a question
     *
     * @param surveyId
     * @param questionId
     * @return
     */
    @Override
    public Boolean deleteQuestion(String surveyId, String questionId) {
        Survey survey = dataManager.getSurvey(surveyId);
        if(survey == null || survey.getQuestion(questionId) == null){
            return Boolean.FALSE;
        }
        survey.removeQuestion(questionId);
        return Boolean.TRUE;
    }

    /**
     * delete a question
     *
     * @param surveyId
     * @param questionId
     * @return Responsechart
     */
    @Override
    public ResponseChart generateDistributionChart(String surveyId, String questionId) {
        Survey survey = dataManager.getSurvey(surveyId);
        ResponseChart response = survey.getReport().get(questionId);
        response.setResponseCount(survey.getResponseCount().get());
        return response;
    }

    /**
     * Get the list of all questions for a survey
     *
     * @param surveyId
     * @return List of Questions
     */
    @Override
    public List<Question> listQuestions(String surveyId) {
        return dataManager.listQuestions(surveyId);
    }

    /**
     * Create a survey with list of questions
     *
     * @param surveyId
     * @param response
     * @return
     */
    @Override
    public Boolean submitSurvey(String surveyId, Map<String, String> response) {
        Survey survey = dataManager.getSurvey(surveyId);
        return survey.submitResponse(response);
    }
}
