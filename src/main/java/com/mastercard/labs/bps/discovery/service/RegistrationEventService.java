package com.mastercard.labs.bps.discovery.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegistrationEventService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private DiscoveryServiceImpl discoveryService;

    @Value("${event.registration.queue}")
    private String queueName;

    public void sendRegistration(String registrationId) {
        this.rabbitTemplate.convertAndSend(queueName, registrationId);
    }

    @RabbitListener(queues = "${event.registration.queue}", concurrency = "4")
    public void processRegistration(String registrationId) {
        log.info("Registration Received: " + registrationId);
        discoveryService.persistRegistration(registrationId);
    }
}