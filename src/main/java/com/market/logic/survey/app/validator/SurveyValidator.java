package com.market.logic.survey.app.validator;

import com.market.logic.survey.app.model.Question;
import com.market.logic.survey.app.dao.SurveyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SurveyValidator{

    private final SurveyDao surveyDao;

    @Autowired
    public SurveyValidator(SurveyDao surveyDao){
        this.surveyDao = surveyDao;
    }

    public Boolean isValid(String surveyId, Question question, List<String> validationErrors){
        return isValid(surveyId, question,false,validationErrors);
    }


    public void isSurveyValid(String surveyId, List<String> validationErrors){
        if(!surveyDao.surveyExist(surveyId)){
            validationErrors.add("Survey Id doesnt exist");
        }
    }
    public Boolean isValid(String surveyId, List<String> validationErrors){
        isSurveyValid(surveyId,validationErrors);
        List<Question> questions = surveyDao.listQuestions(surveyId);
        if(questions ==null || questions.isEmpty()){
            validationErrors.add("Questions are empty");
        }
        return validationErrors.size()>0? Boolean.FALSE: Boolean.TRUE;
    }

    public Boolean isSurveyResponseValid(String surveyId, Map<String,String> response, List<String> validationErrors){
        isValid(surveyId,validationErrors);
        for(String questionId : response.keySet()){
            String option = response.get(questionId);
            Question q = surveyDao.getQuestion(surveyId,questionId);
            if(q == null){
                validationErrors.add("Invalid Question Id in Survey");
                continue;
            }
            if(!q.getOptions().containsKey(option)){
                validationErrors.add("Options not found");
            }

        }
        return validationErrors.size()>0? Boolean.FALSE: Boolean.TRUE;
    }

    public Boolean isValid(String surveyId, Question question,boolean isCreated, List<String> validationErrors){
        isSurveyValid(surveyId,validationErrors);
        if(question == null){
            validationErrors.add("Question is empty");
            return Boolean.FALSE;
        }
        if(!isCreated && question.getQuestionId().isEmpty()){
            validationErrors.add("Question Id doesnt exist");
        }
        if(question.getQuestionDesc().isEmpty()){
            validationErrors.add("Question is Empty");
        }
        if(question.getOptions() == null || question.getOptions().isEmpty() ){
            validationErrors.add("Question options are Empty");
        }
        return validationErrors.size()>0? Boolean.FALSE: Boolean.TRUE;
    }
}
