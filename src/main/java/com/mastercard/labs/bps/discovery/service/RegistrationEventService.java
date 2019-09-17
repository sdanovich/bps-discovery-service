package com.mastercard.labs.bps.discovery.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegistrationEventService implements RabbitListenerConfigurer {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private DiscoveryServiceImpl discoveryService;

    @Value("${event.registration.queue}")
    private String queueName;
    @Value("${event.concurrency}")
    private Integer concurrency;

    public void sendRegistration(String registrationId) {
        this.rabbitTemplate.convertAndSend(queueName, registrationId);
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setId("RegistrationEventService");
        endpoint.setConcurrency(concurrency.toString());
        endpoint.setQueueNames(queueName);
        endpoint.setMessageListener(message -> {
            log.info("Registration Received: " + message.toString());
            discoveryService.persistRegistration(message.toString());
        });
        registrar.registerEndpoint(endpoint);
    }
}