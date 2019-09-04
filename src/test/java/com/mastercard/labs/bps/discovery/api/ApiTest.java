package com.mastercard.labs.bps.discovery.api;

import com.mastercard.labs.bps.discovery.domain.journal.Discovery;
import com.mastercard.labs.bps.discovery.persistence.repository.DiscoveryRepository;
import com.mastercard.labs.bps.discovery.persistence.repository.RegistrationRepository;
import com.mastercard.labs.bps.discovery.schedule.BatchFileProcessor;
import com.mastercard.labs.bps.discovery.service.RestTemplateServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration
@WebAppConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("debug")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
public class ApiTest {

    @Autowired
    private RestTemplateServiceImpl restTemplateService;

    @Autowired
    private DiscoveryRepository discoveryRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BatchFileProcessor batchFileProcessor;

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

    @Before
    public void setUp() {
        discoveryRepository.deleteAll();
        registrationRepository.deleteAll();
    }

    @Test
    public void runDiscoveryTest() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("discovery.file/Versapay_BPADiscovery_08272019.csv");
        MockMultipartFile file = new MockMultipartFile("file", "Versapay_BPADiscovery_08272019.csv", MediaType.APPLICATION_OCTET_STREAM_VALUE, is);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/discovery/buyers")
                .file(file)
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE.toString())
                .accept(MediaType.ALL))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
        batchFileProcessor.process();
        Assert.assertThat(discoveryRepository.findAll().size(), Is.is(5));
        Assert.assertThat(discoveryRepository.findAll().stream().filter(d -> d.getStatus() != Discovery.STATUS.COMPLETE).collect(Collectors.toList()).size(), Is.is(0));
    }

    @Test
    public void runRegistrationTest() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("registration.file/Versapay_GOOD_BPARegistration_08272019.csv");
        MockMultipartFile file = new MockMultipartFile("file", "Versapay_GOOD_BPARegistration_08272019.csv", MediaType.APPLICATION_OCTET_STREAM_VALUE, is);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/registration/buyers")
                .file(file)
                .header("agentName", "CSI")
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE.toString())
                .accept(MediaType.ALL))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
        batchFileProcessor.process();
        //Assert.assertThat(registrationRepository.findAll().size(), Is.is(5));
        //Assert.assertThat(registrationRepository.findAll().stream().filter(d -> d.getStatus() != Discovery.STATUS.COMPLETE).collect(Collectors.toList()).size(), Is.is(0));
    }



}
