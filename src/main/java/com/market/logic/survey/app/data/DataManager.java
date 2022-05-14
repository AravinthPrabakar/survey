package com.market.logic.survey.app.data;

import com.market.logic.survey.app.model.Question;
import com.market.logic.survey.app.model.Survey;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Scope("singleton")
public class DataManager {

    private final ConcurrentHashMap<String, Survey> surveys = new ConcurrentHashMap<>();
    private final AtomicLong seqSurveyId = new AtomicLong(1);
    private final AtomicLong seqQuestionId = new AtomicLong(1000);

    public void addSurvey(Survey survey){
        surveys.put(survey.getSurveyId(),survey);
    }

    public Survey getSurvey(String surveyId){
        return surveys.get(surveyId);
    }

    public String createSurvey(){
        String seqSurveyId = String.valueOf(getNextSurveySequence());
        surveys.putIfAbsent(seqSurveyId, new Survey(seqSurveyId));
        return surveys.containsKey(seqSurveyId) ? seqSurveyId : null;
    }

    public List<Question> listQuestions(String surveyId){
        return new ArrayList<>(surveys.get(surveyId).getQuestions().values());
    }

    public Long getNextSurveySequence(){
        return seqSurveyId.getAndIncrement();
    }

    public Long getNextQuestionSequence(){
        return seqQuestionId.getAndIncrement();
    }

}
