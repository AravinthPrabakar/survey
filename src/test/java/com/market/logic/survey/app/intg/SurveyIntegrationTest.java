package com.market.logic.survey.app.intg;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SurveyIntegrationTest {
    protected static final String baseURL="http://localhost:" ;
    protected static final String createSurveyURL="/surveys/";
    protected static final String questionURL="/surveys/<SURVEY_ID>/questions/";
    protected static final String surveySubmitURL="/surveys/<SURVEY_ID>/submit/";
    protected static final String analyzeQuestionURL="/surveys/<SURVEY_ID>/questions/<QUESTION_ID>/analyze";
    protected static final String SURVEY_ID="survey-id";
    protected static final String DISTRIBUTION="distribution";
    protected static final String RESPONSE_COUNT="responseCount";
    protected static final String ID="id";
    protected static final String SURVEY_PLACEHOLDER="<SURVEY_ID>";

    protected static final String QUESTION_ID="question-id";
    protected static final String QUESTION_PLACEHOLDER="<QUESTION_ID>";

    protected static final String STATUS="status";
    protected static final String OPTIONS="options";
    protected static final String RESPONSE="response";
    protected static final String STATUS_CREATED="CREATED";
    protected static final String STATUS_SUBMITTED="SUBMITTED";
    protected static final String STATUS_NOTFOUND="NOT FOUND";
    protected static final String STATUS_FOUND="FOUND";

    protected static HttpEntity<String> request;
    AtomicInteger SURVEY_SEQ= new AtomicInteger(1);

    AtomicInteger QUESTION_SEQ= new AtomicInteger(1000);
    // bind the above RANDOM_PORT
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    @Order(1)
    public void testCreateSurvey() throws Exception {
        createSurvey(restTemplate, port,SURVEY_SEQ);
    }

    @Test
    @Order(2)
    public void testAddQuestion() throws Exception {
        createQuestion(restTemplate, port,SURVEY_SEQ);
    }

    @Test
    @Order(3)
    public void testGetQuestion() throws Exception {
        getQuestion(true,QUESTION_SEQ.get());
        QUESTION_SEQ.incrementAndGet();
        getQuestion(false,QUESTION_SEQ.get());
    }

    @Test
    @Order(4)
    public void testUpdateQuestion() throws Exception {
        updateQuestion(QUESTION_SEQ.get());
    }

    @Test
    @Order(5)
    public void testListQuestion() throws Exception {
        listQuestion();
    }
    @Test
    @Order(6)
    public void testSubmitQuestion() throws Exception {
        HttpHeaders surveyHeaders = new HttpHeaders();
        surveyHeaders.setContentType(MediaType.APPLICATION_JSON);
        JSONObject survey = new JSONObject();
        survey.put(String.valueOf(QUESTION_SEQ),"B");
        HttpEntity<String> surveyRequest =
                new HttpEntity<>(survey.toString(), surveyHeaders);

        submitSurvey(restTemplate,port,SURVEY_SEQ,surveyRequest);
        submitSurvey(restTemplate,port,SURVEY_SEQ,surveyRequest);

        survey.put(String.valueOf(QUESTION_SEQ),"A");
        surveyRequest =
                new HttpEntity<>(survey.toString(), surveyHeaders);
        submitSurvey(restTemplate,port,SURVEY_SEQ,surveyRequest);

    }

    @Test
    @Order(7)
    public void testGetDistribution() throws Exception {
        HashMap response = getDistribution(restTemplate,port,SURVEY_SEQ,QUESTION_SEQ);
        HashMap distribution = (HashMap) response.get(DISTRIBUTION);
        assertEquals(2,distribution.get("B"));
        assertEquals(1,distribution.get("A"));
        assertEquals(3,response.get(RESPONSE_COUNT));
    }

    @Test
    @Order(8)
    public void testDeleteQuestion() throws Exception {
        deleteQuestion(QUESTION_SEQ.get());
    }

    private void listQuestion() throws Exception {
        ResponseEntity<List> response = restTemplate.getForEntity(
                new URL(baseURL + port + generateURL(questionURL,SURVEY_SEQ.get(),null)).toString(), List.class);
        List questions = response.getBody();
        assertEquals(1,questions.size());
    }

    private void getQuestion(boolean isCreated,Integer questionId) throws Exception {
        ResponseEntity<HashMap> response = restTemplate.getForEntity(
                new URL(baseURL + port + generateURL(questionURL,SURVEY_SEQ.get(),questionId)).toString(), HashMap.class);
        Map<String,Object> responseMap = response.getBody();
        if(isCreated){
            LinkedHashMap resp = (LinkedHashMap)responseMap.get(RESPONSE);
            assertEquals(String.valueOf(QUESTION_SEQ.get()),resp.get(ID));
            assertEquals(STATUS_FOUND,responseMap.get(STATUS));
        }else{
            assertEquals(STATUS_NOTFOUND,responseMap.get(STATUS));
        }

    }


    private void updateQuestion(Integer questionId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject question = new JSONObject();
        JSONObject options = new JSONObject();
        options.put("A","The Arctic");
        options.put("B","The Sahara desert");
        options.put("C","Not Interested");
        question.put("question", "Would you rather live for the rest of your life ?");
        question.put("options", options);
        request =
                new HttpEntity<String>(question.toString(), headers);
        restTemplate.put(
                new URL(baseURL + port + generateURL(questionURL,SURVEY_SEQ.get(),questionId)).toString(), request);
        ResponseEntity<HashMap> response = restTemplate.getForEntity(
                new URL(baseURL + port + generateURL(questionURL,SURVEY_SEQ.get(),questionId)).toString(), HashMap.class);
        Map<String,Object> responseMap = response.getBody();
        LinkedHashMap resp = (LinkedHashMap)responseMap.get(RESPONSE);
        LinkedHashMap optionResp = (LinkedHashMap)resp.get(OPTIONS);
        assertEquals(String.valueOf(QUESTION_SEQ.get()),resp.get(ID));
        assertEquals(question.get("question"),resp.get("question"));
        assertEquals(options.length(),optionResp.size());
        assertEquals(STATUS_FOUND,responseMap.get(STATUS));
    }

    private void deleteQuestion(Integer questionId) throws Exception {
        restTemplate.delete(
                new URL(baseURL + port + generateURL(questionURL,SURVEY_SEQ.get(),questionId)).toString());
        ResponseEntity<HashMap> response = restTemplate.getForEntity(
                new URL(baseURL + port + generateURL(questionURL,SURVEY_SEQ.get(),questionId)).toString(), HashMap.class);
        Map<String,Object> responseMap = response.getBody();
        assertEquals(STATUS_NOTFOUND,responseMap.get(STATUS));
    }

    protected String generateURL(String url, Integer surveyId, Integer questionId){
        if(surveyId != null){
            url = url.replace(SURVEY_PLACEHOLDER,String.valueOf(surveyId));
        }
        if(questionId != null){
            if(url.contains(QUESTION_PLACEHOLDER)){
                url = url.replace(QUESTION_PLACEHOLDER,String.valueOf(questionId));
            }else{
                url = url+questionId;
            }

        }
        return url;
    }

    @BeforeAll
    public static void runBeforeAllTestMethods() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject question = new JSONObject();
        JSONObject options = new JSONObject();
        options.put("A","The Arctic");
        options.put("B","The Sahara desert");
        question.put("question", "Would you rather live for the rest of your life ?");
        question.put("options", options);
        request =
                new HttpEntity<String>(question.toString(), headers);
    }


    protected void createSurvey(TestRestTemplate restTemplate, int port, AtomicInteger SURVEY_SEQ) throws MalformedURLException {
        ResponseEntity<HashMap> response = restTemplate.postForEntity(new URL(baseURL + port + createSurveyURL).toString(),null,HashMap.class);
        HashMap responseMap =  response.getBody();
        assertEquals(String.valueOf(SURVEY_SEQ),responseMap.get(SURVEY_ID) );
        assertEquals(STATUS_CREATED,responseMap.get(STATUS) );
    }


    protected void createQuestion(TestRestTemplate restTemplate, int port, AtomicInteger SURVEY_SEQ) throws Exception {
        ResponseEntity<HashMap> response = restTemplate.postForEntity(
                new URL(baseURL + port + generateURL(questionURL,SURVEY_SEQ.get(),null)).toString(),request,HashMap.class);
        HashMap responseMap =  response.getBody();
        assertEquals(String.valueOf(SURVEY_SEQ),responseMap.get(SURVEY_ID) );
        assertEquals(STATUS_CREATED,responseMap.get(STATUS) );
    }

    protected void submitSurvey(TestRestTemplate restTemplate, int port, AtomicInteger SURVEY_SEQ, HttpEntity<String> surveyRequest) throws Exception {

        ResponseEntity<HashMap> response = restTemplate.postForEntity(
                new URL(baseURL + port + generateURL(surveySubmitURL,SURVEY_SEQ.get(),null)).toString(),surveyRequest,HashMap.class);
        HashMap responseMap =  response.getBody();
        assertEquals(String.valueOf(SURVEY_SEQ),responseMap.get(SURVEY_ID) );
        assertEquals(STATUS_SUBMITTED,responseMap.get(STATUS) );
    }


    protected HashMap getDistribution(TestRestTemplate restTemplate, int port, AtomicInteger SURVEY_SEQ, AtomicInteger QUESTION_SEQ) throws Exception {

        ResponseEntity<HashMap> response = restTemplate.getForEntity(
                new URL(baseURL + port + generateURL(analyzeQuestionURL,SURVEY_SEQ.get(),QUESTION_SEQ.get())).toString(),HashMap.class);
        HashMap responseMap =  response.getBody();
        assertNotNull(responseMap.get(DISTRIBUTION));
        return responseMap;
    }



}
