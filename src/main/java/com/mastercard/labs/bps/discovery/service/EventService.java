package com.mastercard.labs.bps.discovery.service;

import com.mastercard.labs.bps.discovery.domain.journal.BatchFile;
import com.mastercard.labs.bps.discovery.exceptions.ExecutionException;
import com.mastercard.labs.bps.discovery.persistence.repository.BatchFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

import static com.mastercard.labs.bps.discovery.domain.journal.BatchFile.STATUS.PROCESSING;

@Service
@Slf4j
public class EventService implements RabbitListenerConfigurer {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private DiscoveryServiceImpl discoveryService;
    @Autowired
    private Exchange discoveryExchange;
    @Autowired
    private Exchange registrationExchange;
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private BatchFileRepository batchFileRepository;


    @Value("${event.concurrency}")
    private Integer concurrency;

    private RabbitListenerEndpointRegistrar registrar;

    public void sendDiscovery(String queueName, String discoveryId) {
        this.rabbitTemplate.convertAndSend(queueName, discoveryId);
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        this.registrar = registrar;
        batchFileRepository.findByStatus(PROCESSING).orElse(Collections.emptySet()).stream().filter(Objects::nonNull).forEach(batchFile -> {
            String queueName = batchFile.getId() + "|" + batchFile.getType().name();
            if (amqpAdmin.getQueueProperties(queueName) != null) {
                registrar.registerEndpoint(getSimpleRabbitListenerEndpoint(queueName));
            }
        });
    }

    private SimpleRabbitListenerEndpoint getSimpleRabbitListenerEndpoint(String queueName) {
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setId("EventService" + queueName);
        endpoint.setQueueNames(queueName);
        endpoint.setMessageListener(message -> {
            String messageKey = new String(message.getBody());
            switch (BatchFile.TYPE.valueOf(StringUtils.substringAfter(messageKey, "|"))) {
                case LOOKUP:
                    discoveryService.persistDiscovery(StringUtils.substringBefore(messageKey, "|"));
                    break;
                case REGISTRATION:
                    discoveryService.persistRegistration(StringUtils.substringBefore(messageKey, "|"));
                    break;
                default:
                    throw new ExecutionException("unknown code in message listener: " + queueName);
            }
        });
        return endpoint;
    }

    public void setUpQueue(String queueName) {
        Queue queue = QueueBuilder.durable(queueName).build();
        amqpAdmin.declareQueue(queue);
        if (BatchFile.TYPE.valueOf(StringUtils.substringAfter(queueName, "|")) == BatchFile.TYPE.LOOKUP) {
            amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(discoveryExchange).with(queueName).noargs());
        } else if (BatchFile.TYPE.valueOf(StringUtils.substringAfter(queueName, "|")) == BatchFile.TYPE.REGISTRATION) {
            amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(registrationExchange).with(queueName).noargs());
        }
        registrar.registerEndpoint(getSimpleRabbitListenerEndpoint(queueName));
    }

    public void wakeUpQueue(String queueName) {
        amqpAdmin.initialize();
    }
}