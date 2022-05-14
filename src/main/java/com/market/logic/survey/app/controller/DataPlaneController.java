package com.market.logic.survey.app.controller;

import com.market.logic.survey.app.SurveyApplication;
import com.market.logic.survey.app.model.Question;
import com.market.logic.survey.app.service.ManagerService;
import com.market.logic.survey.app.validator.SurveyValidator;
import com.market.logic.survey.app.exception.ValidationFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value="/surveys", produces = APPLICATION_JSON_VALUE)
public class DataPlaneController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SurveyApplication.class);

    private final ManagerService managerService;

    private final SurveyValidator validator;

    @Autowired
    DataPlaneController(ManagerService managerService, SurveyValidator validator){
        this.managerService = managerService;
        this.validator = validator;
    }

    @RequestMapping(value = "/{surveyId}/questions", method = RequestMethod.GET)
    public List<Question> listQuestions(@PathVariable(value = "surveyId") String surveyId){
        List<String> validationErrors = new ArrayList<>();
        if(!validator.isValid(surveyId,validationErrors)){
            ValidationFailureException e = new ValidationFailureException(validationErrors.toString());
            LOGGER.error("Validation failure",e);
            throw e;
        }
        return managerService.listQuestions(surveyId);
    }


    @RequestMapping(value = "/{surveyId}/submit", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE)
    public Map<String, String> submitSurvey(@PathVariable(value = "surveyId") String surveyId,
                                            @RequestBody Map<String,String> payload,
                                            HttpServletResponse response){
        Map<String, String> responseMap = new HashMap<>();
        List<String> validationErrors = new ArrayList<>();
        if(!validator.isSurveyResponseValid(surveyId,payload, validationErrors)){
            LOGGER.error("Validation failure",new ValidationFailureException(validationErrors.toString()));
            responseMap.put("survey-id",surveyId);
            responseMap.put("status","FAILED");
            responseMap.put("message",validationErrors.toString());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return responseMap;
        }
        managerService.submitSurvey(surveyId, payload);
        responseMap.put("survey-id",surveyId);
        responseMap.put("status","SUBMITTED");
        responseMap.put("message","Thanks");
        response.setStatus(HttpServletResponse.SC_OK);
        return responseMap;
    }


}


