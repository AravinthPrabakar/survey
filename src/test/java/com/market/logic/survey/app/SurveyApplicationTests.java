package com.market.logic.survey.app;

import com.market.logic.survey.app.controller.ControlPlaneController;
import com.market.logic.survey.app.controller.DataPlaneController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SurveyApplicationTests {

    @Autowired
    DataPlaneController dataPlaneController;

    @Autowired
    ControlPlaneController controlPlaneController;

    @Test
    void contextLoads() {
        Assertions.assertThat(dataPlaneController).isNotNull();
        Assertions.assertThat(controlPlaneController).isNotNull();
    }

}
