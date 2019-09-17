package com.mastercard.labs.bps.discovery.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DiscoveryEventService implements RabbitListenerConfigurer {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private DiscoveryServiceImpl discoveryService;

    @Value("${event.discovery.queue}")
    private String queueName;
    @Value("${event.concurrency}")
    private Integer concurrency;

    public void sendDiscovery(String discoveryId) {
        this.rabbitTemplate.convertAndSend(queueName, discoveryId);
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setId("DiscoveryEventService");
        endpoint.setConcurrency(concurrency.toString());
        endpoint.setQueueNames(queueName);
        endpoint.setMessageListener(message -> {
            log.info("Discovery Received: " + new String(message.getBody()));
            discoveryService.persistDiscovery(new String(message.getBody()));
        });
        registrar.registerEndpoint(endpoint);
    }

}