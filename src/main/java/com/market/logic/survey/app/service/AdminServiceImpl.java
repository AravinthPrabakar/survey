package com.market.logic.survey.app.service;

import com.market.logic.survey.app.model.ResponseChart;
import com.market.logic.survey.app.dao.SurveyDao;
import com.market.logic.survey.app.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService{

    private final SurveyDao surveyDao;

    @Autowired
    AdminServiceImpl(SurveyDao surveyDao){
        this.surveyDao = surveyDao;
    }

    /**
     * Create a new Survey
     *
     * @return surveyId
     */
    @Override
    public String createSurvey() {
        return surveyDao.createSurvey();
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
        return surveyDao.getQuestion(surveyId,questionId);
    }

    /**
     * Add a new question
     *
     * @param surveyId
     * @param question
     * @return
     */
    @Override
    public Question addQuestion(String surveyId, Question question) {
        return surveyDao.addQuestion(surveyId,question);
    }

    /**
     * update a question
     *
     * @param surveyId
     * @param questionId
     * @param question
     * @return
     */
    @Override
    public Question updateQuestion(String surveyId, String questionId, Question question) {
        return surveyDao.updateQuestion(surveyId,question);
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
        return surveyDao.deleteQuestion(surveyId,questionId);
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
        return surveyDao.generateDistributionChart(surveyId,questionId);
    }
}
