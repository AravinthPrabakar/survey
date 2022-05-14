package com.market.logic.survey.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@JsonIgnoreProperties(value = "true")
@Getter
@Setter
public class ResponseChart {

    public ResponseChart(Set<String> options) {
        this.distribution = new ConcurrentHashMap<>();
        for(String option: options){
            this.distribution.put(option, 0L);
        }
    }

    @JsonProperty("distribution")
    private ConcurrentHashMap<String, Long> distribution;

    void processResponse(String option){
        distribution.computeIfPresent(option,(key,value)->value+1);
    }
}
