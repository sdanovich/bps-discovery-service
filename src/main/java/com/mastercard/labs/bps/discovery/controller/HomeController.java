package com.mastercard.labs.bps.discovery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.labs.bps.discovery.webhook.model.HealthCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Home redirection to swagger com.mastercard.labs.bps.discovery.api documentation
 */
@Controller
@ApiIgnore
@RequestMapping(value = "/")
public class HomeController {

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "redirect:swagger-ui.html";
    }

    @RequestMapping(value = "/healthCheck", method = RequestMethod.GET)
    public ResponseEntity<HealthCheck> health(HttpServletRequest request) throws IOException {
        ResponseEntity<String> response = new RestTemplate().getForEntity(request.getRequestURL().toString().replace("/healthCheck", "/actuator/health"), String.class);
        String body = response.getBody();
        HealthCheck healthCheck = jacksonObjectMapper.readValue(body, HealthCheck.class);
        healthCheck.setStatus(healthCheck.getStatus().toString().equalsIgnoreCase("UP") ? Boolean.TRUE : Boolean.FALSE);
        return Boolean.valueOf(healthCheck.getStatus().toString()) ? ResponseEntity.ok(healthCheck) : ResponseEntity.badRequest().body(healthCheck);
    }

    @GetMapping("/discovery-ui")
    public String discoveryUI() throws IOException {
        return "discovery";
    }
}
