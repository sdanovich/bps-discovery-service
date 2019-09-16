package com.mastercard.labs.bps.discovery.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DiscoveryEventService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private DiscoveryServiceImpl discoveryService;

    @Value("${event.discovery.queue}")
    private String queueName;

    public void sendDiscovery(String discoveryId) {
        this.rabbitTemplate.convertAndSend(queueName, discoveryId);
    }

    @RabbitListener(queues = "${event.discovery.queue}", concurrency = "4")
    public void processDiscovery(String discoveryId) {
        log.info("Discovery Received: " + discoveryId);
        discoveryService.persistDiscovery(discoveryId);
    }
}