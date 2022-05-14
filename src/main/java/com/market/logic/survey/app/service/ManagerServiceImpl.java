package com.market.logic.survey.app.service;

import com.market.logic.survey.app.dao.SurveyDao;
import com.market.logic.survey.app.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ManagerServiceImpl implements ManagerService{

    private final SurveyDao surveyDao;

    @Autowired
    ManagerServiceImpl(SurveyDao surveyDao){
        this.surveyDao = surveyDao;
    }

    /**
     * Get the list of all questions for a survey
     *
     * @param surveyId
     * @return List of Questions
     */
    @Override
    public List<Question> listQuestions(String surveyId) {
        return surveyDao.listQuestions(surveyId);
    }

    /**
     * Create a survey with list of questions
     *
     * @param surveyId
     * @param response
     * @return surveyId
     */
    @Override
    public Boolean submitSurvey(String surveyId, Map<String, String> response) {
        return surveyDao.submitSurvey(surveyId,response);
    }
}
