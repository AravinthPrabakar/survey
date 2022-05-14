package com.market.logic.survey.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@JsonIgnoreProperties(value = "true")
@Getter
@Setter
public class Question {
    @JsonProperty("id")
    private String questionId;

    @JsonProperty("question")
    private String questionDesc;

    @JsonProperty("options")
    private Map<String,String> options;

    public Question(){
        super();
    }

    public Question(String questionId, String questionDesc, Map<String,String> options) {
        super();
        this.questionId = questionId;
        this.questionDesc = questionDesc;
        this.options = options;
    }

}

