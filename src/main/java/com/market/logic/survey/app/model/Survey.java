package com.market.logic.survey.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@JsonIgnoreProperties(value = "true")
@Getter
@Setter
public class Survey {
    @JsonProperty("surveyId")
    private String surveyId;

    @JsonProperty("questions")
    ConcurrentHashMap<String,Question> questions;

    @JsonProperty("response-count")
    private AtomicLong responseCount;

    @JsonProperty("report")
    ConcurrentHashMap<String,ResponseChart> report;

    public Survey(String surveyId) {
        this.surveyId = surveyId;
        this.questions = new ConcurrentHashMap<>();
        this.responseCount = new AtomicLong(0);
        this.report = new ConcurrentHashMap<>();
    }

    public Question getQuestion(String questionId){
        return questions.get(questionId);
    }


    public Question addQuestion(Question question){
        questions.put(question.getQuestionId(),question);
        report.put(question.getQuestionId(),new ResponseChart(question.getOptions().keySet()));
        return questions.get(question.getQuestionId());
    }

    public Question removeQuestion(String questionId){
        report.remove(questionId);
        return questions.remove(questionId);
    }

    public Boolean submitResponse(Map<String,String> response){
        responseCount.incrementAndGet();
        for(String questionId : response.keySet()){
            Question q = questions.get(questionId);
            report.get(questionId).processResponse(response.get(questionId));
        }
        return Boolean.TRUE;
    }


}
