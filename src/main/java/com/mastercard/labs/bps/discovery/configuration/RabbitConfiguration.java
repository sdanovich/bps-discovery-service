package com.mastercard.labs.bps.discovery.configuration;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Value("${event.discovery.exchange}")
    private String discoveryExchangeStr;
    @Value("${event.discovery.routing}")
    private String discoveryRouting;
    @Value("${event.registration.exchange}")
    private String registrationExchangeStr;
    @Value("${event.registration.routing}")
    private String registrationRouting;
    @Value("${event.concurrency}")
    private Integer concurrency;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitTemplate.getConnectionFactory());
        factory.setConcurrentConsumers(concurrency);
        factory.setMaxConcurrentConsumers(2 * concurrency);
        factory.setPrefetchCount(concurrency * 3);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(rabbitTemplate.getConnectionFactory());
    }

    @Bean
    public Exchange discoveryExchange() {
        return ExchangeBuilder.topicExchange(discoveryExchangeStr).build();
    }


    @Bean
    public Exchange registrationExchange() {
        return ExchangeBuilder.topicExchange(registrationExchangeStr).build();
    }


}