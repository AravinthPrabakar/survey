package com.market.logic.survey.app.controller;

import com.market.logic.survey.app.model.ResponseChart;
import com.market.logic.survey.app.service.AdminService;
import com.market.logic.survey.app.SurveyApplication;
import com.market.logic.survey.app.exception.ValidationFailureException;
import com.market.logic.survey.app.model.Question;
import com.market.logic.survey.app.validator.SurveyValidator;
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
public class ControlPlaneController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SurveyApplication.class);

    private final AdminService adminService;

    private final SurveyValidator validator;

    @Autowired
    ControlPlaneController(AdminService adminService, SurveyValidator validator){
        this.adminService = adminService;
        this.validator = validator;
    }



    /**
     * This method will return question based on the id
     * @return question
     */
    @RequestMapping( method = RequestMethod.POST)
    public Map<String, String> createSurvey(HttpServletResponse response){
        String surveyId = adminService.createSurvey();
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("survey-id",surveyId);
        responseMap.put("status","CREATED");
        response.setStatus(HttpServletResponse.SC_CREATED);
        return responseMap;
    }

    /**
     * This method will return question based on the id
     * @param questionId
     * @param surveyId
     * @return question
     */
    @RequestMapping(value = "/{surveyId}/questions/{id}", method = RequestMethod.GET)
    public Map<String,Object> getQuestion(@PathVariable(value = "surveyId") String surveyId, @PathVariable("id") String questionId ){
        Question question = adminService.getQuestion(surveyId,questionId);
        Map<String, Object> responseMap = new HashMap<>();
        if(question != null){
            responseMap.put("response",question);
            responseMap.put("status","FOUND");
        }else{
            responseMap.put("status","NOT FOUND");
        }
        return responseMap;
    }

    /**
     * This method will add new question to survey
     * @param surveyId
     * @param payload
     * @param response
     * @return ResponseMap
     */
    @RequestMapping(value = "/{surveyId}/questions", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE)
    public Map<String, String> addQuestion(@PathVariable(value = "surveyId") String surveyId,
                             @RequestBody Question payload,
                             HttpServletResponse response){
        List<String> validationErrors = new ArrayList<>();
        if(!validator.isValid(surveyId,payload,true,validationErrors)){
            LOGGER.info("Question payload received is invalid" + surveyId);
            throw new ValidationFailureException(validationErrors.toString());
        }
        Question responseObj = adminService.addQuestion(surveyId, payload);

        response.setStatus(HttpServletResponse.SC_CREATED);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("survey-id",surveyId);
        responseMap.put("question-id",responseObj.getQuestionId());
        responseMap.put("status","CREATED");
        responseMap.put("message",validationErrors.toString());
        return responseMap;
    }

    /**
     * This method will update new question to survey
     * @param surveyId
     * @param questionId
     * @param payload
     * @param response
     * @return ResponseMap
     */
    @RequestMapping(value = "/{surveyId}/questions/{id}", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE)
    public Map<String, String> updateQuestion(@PathVariable("surveyId") String surveyId, @PathVariable("id") String questionId,
                               @RequestBody Question payload,
                               HttpServletResponse response) throws Exception {
        List<String> validationErrors = new ArrayList<>();
        payload.setQuestionId(questionId);
        if(!validator.isValid(surveyId,payload,validationErrors)){
            LOGGER.info("Question payload received is invalid" + surveyId);
            throw new ValidationFailureException(validationErrors.toString());
        }
        adminService.updateQuestion(surveyId, questionId, payload);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("survey-id",surveyId);
        responseMap.put("question-id",questionId);
        responseMap.put("status","UPDATED");
        responseMap.put("message",validationErrors.toString());
        response.setStatus(HttpServletResponse.SC_CREATED);
        return responseMap;
    }
    /**
     * This method will delete new question to survey
     * @param surveyId
     * @param questionId
     * @param response
     * @return
     */
    @RequestMapping(value = "/{surveyId}/questions/{id}", method = RequestMethod.DELETE)
    public Map<String,String> deleteQuestion(@PathVariable(value = "surveyId") String surveyId, @PathVariable(value = "id") String questionId,
                               HttpServletResponse response){
        Boolean resp = adminService.deleteQuestion(surveyId, questionId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("survey-id",surveyId);
        responseMap.put("question-id",questionId);
        if(resp){
            responseMap.put("status","DELETED");
        }else{
            responseMap.put("status","FAILED");
        }
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        return responseMap;
    }

    /**
     * This method will generate the response distribution for a question
     * @param surveyId
     * @param questionId
     * @return responseChart
     */
    @RequestMapping(value = "/{surveyId}/questions/{id}/analyze", method = RequestMethod.GET)
    public ResponseChart getResponseDistribution(@PathVariable(value = "surveyId") String surveyId, @PathVariable(value = "id") String questionId){
        return adminService.generateDistributionChart(surveyId, questionId);
    }
}
