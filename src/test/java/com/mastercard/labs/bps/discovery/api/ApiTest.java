package com.mastercard.labs.bps.discovery.api;

import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.service.RestTemplateServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("debug")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
public class ApiTest {

    @Autowired
    private RestTemplateServiceImpl restTemplateService;

    @Test
    public void testCallToTrack() {
        Discovery discovery = new Discovery();
        discovery.setCompanyName("AVANTI POLAR LIPIDS INC");
        discovery.setAddress1("700 INDUSTRIAL PARK DR");
        discovery.setCity("ALABASTER");
        discovery.setState("AL");
        discovery.setCountry("US");
        discovery.setZip("35007");
        try {
            restTemplateService.callTrack(discovery).get().getBody();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
