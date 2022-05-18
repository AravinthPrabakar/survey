package com.market.logic.survey.app.unit;

import com.market.logic.survey.app.controller.ControlPlaneController;
import com.market.logic.survey.app.controller.DataPlaneController;
import com.market.logic.survey.app.dao.SurveyDao;
import com.market.logic.survey.app.exception.ValidationFailureException;
import com.market.logic.survey.app.model.Question;
import com.market.logic.survey.app.model.ResponseChart;
import com.market.logic.survey.app.service.AdminService;
import com.market.logic.survey.app.service.ManagerService;
import com.market.logic.survey.app.validator.SurveyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SurveyUnitTests {

    @InjectMocks
    DataPlaneController dpController;

    @InjectMocks
    ControlPlaneController cpController;

    @Mock
    AdminService adminService;

    @Mock
    ManagerService managerService;

    @Mock
    SurveyDao dao;

    SurveyValidator validator;

    Question q;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
        validator = new SurveyValidator(dao);
        cpController = new ControlPlaneController(adminService,validator);
        dpController = new DataPlaneController(managerService,validator);

        HashMap<String,String> options = new HashMap();
        options.put("A","Human");
        options.put("B","AI");
        q = new Question("1000","What are you ? ",options);

    }


    @Test
    public void testCreateQuestion(){
        when(dao.surveyExist("1")).thenReturn(Boolean.TRUE);
        when(dao.surveyExist("2")).thenReturn(Boolean.FALSE);
        when(adminService.addQuestion(any(),any())).thenReturn(q);
        assertThrows(ValidationFailureException.class, ()-> cpController.addQuestion("2",null,null));
        assertThrows(ValidationFailureException.class, ()-> cpController.addQuestion("1",null,null));
        Map<String,String> responseMap = cpController.addQuestion("1",q, new MockHttpServletResponse());
        assertEquals("CREATED",responseMap.get("status"));
    }
    @Test
    public void testGetQuestion(){
        when(adminService.getQuestion(any(),eq("1000"))).thenReturn(q);
        when(adminService.getQuestion(any(),eq("1001"))).thenReturn(null);
        assertThrows(ValidationFailureException.class, ()-> cpController.addQuestion("2",null,null));
        assertThrows(ValidationFailureException.class, ()-> cpController.addQuestion("1",null,null));
        Map<String,Object> responseMap = cpController.getQuestion("1","1000");
        assertEquals("FOUND",responseMap.get("status"));
        responseMap = cpController.getQuestion("1","1001");
        assertEquals("NOT FOUND",responseMap.get("status"));
    }

    @Test
    public void testUpdateQuestion() throws Exception {
        when(dao.surveyExist("1")).thenReturn(Boolean.TRUE);
        assertThrows(ValidationFailureException.class, ()-> cpController.updateQuestion("1","1000",null, new MockHttpServletResponse()));
        when(adminService.updateQuestion(any(),eq("1000"),any())).thenReturn(q);
        Map<String,String> responseMap = cpController.updateQuestion("1","1000",q, new MockHttpServletResponse());
        assertEquals("UPDATED",responseMap.get("status"));
    }

    @Test
    public void testDeleteQuestion() throws Exception {
        when(adminService.deleteQuestion(any(),eq("1000"))).thenReturn(Boolean.TRUE);
        when(adminService.deleteQuestion(any(),eq("1001"))).thenReturn(Boolean.FALSE);
        Map<String,String> responseMap = cpController.deleteQuestion("1","1000", new MockHttpServletResponse());
        assertEquals("DELETED",responseMap.get("status"));
        responseMap = cpController.deleteQuestion("1","1001",new MockHttpServletResponse());
        assertEquals("FAILED",responseMap.get("status"));
    }

    @Test
    public void testListQuestions() throws Exception {
        when(dao.surveyExist("1")).thenReturn(Boolean.TRUE);
        when(managerService.listQuestions("1")).thenReturn(Arrays.asList(q));
        when(dao.listQuestions("1")).thenReturn(Arrays.asList(q));
        when(dao.listQuestions("2")).thenReturn(null);
        List<Question> reponseQuestions = dpController.listQuestions("1");
        assertEquals(1,reponseQuestions.size());
        assertThrows(ValidationFailureException.class, ()-> dpController.listQuestions("2"));

    }

    @Test
    public void testSubmitSurveyAndDistribution() throws Exception {
        when(dao.surveyExist("1")).thenReturn(Boolean.TRUE);
        Map<String,String> options1 = new HashMap<>();
        options1.put("1001","A");
        Map<String,String> responseMap =  dpController.submitSurvey("1",options1, new MockHttpServletResponse());
        assertEquals("FAILED",responseMap.get("status"));
        Map<String,String> options2 = new HashMap<>();
        options2.put("1000","A");
        when(dao.getQuestion(any(),eq("1000"))).thenReturn(q);
        when(dao.listQuestions(any())).thenReturn(Arrays.asList(q));
        responseMap =  dpController.submitSurvey("1",options2, new MockHttpServletResponse());
        assertEquals("SUBMITTED",responseMap.get("status"));
        ConcurrentHashMap distribution = new ConcurrentHashMap<>();
        distribution.put("A",3);
        distribution.put("B",7);
        ResponseChart resp = new ResponseChart((Set)new HashSet<String>(options2.values()));
        resp.setResponseCount(10L);
        resp.setDistribution(distribution);
        when(adminService.generateDistributionChart(any(),eq("1000"))).thenReturn(resp);
        ResponseChart responseChart =  cpController.getResponseDistribution("1","1000");
        assertNotNull(responseChart);


    }
}
