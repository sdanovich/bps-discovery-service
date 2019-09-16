package com.mastercard.labs.bps.discovery.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Value("${event.discovery.exchange}")
    private String discoveryExchangeStr;
    @Value("${event.discovery.queue}")
    private String discoveryQueueName;
    @Value("${event.discovery.routing}")
    private String discoveryRouting;
    @Value("${event.registration.exchange}")
    private String registrationExchangeStr;
    @Value("${event.registration.queue}")
    private String registrationQueueName;
    @Value("${event.registration.routing}")
    private String registrationRouting;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitTemplate.getConnectionFactory());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }

    @Bean
    public Queue discoveryQueue() {
        return QueueBuilder.durable(discoveryQueueName).build();
    }

    @Bean
    public Queue discoveryDeadLetterQueue() {
        return QueueBuilder.durable(discoveryRouting).build();
    }

    @Bean
    public Exchange discoveryExchange() {
        return ExchangeBuilder.topicExchange(discoveryExchangeStr).build();
    }

    @Bean
    public Binding discoveryBinding(Queue discoveryQueue, TopicExchange discoveryExchange) {
        return BindingBuilder.bind(discoveryQueue).to(discoveryExchange).with(discoveryQueueName);
    }


    @Bean
    public Queue registrationQueue() {
        return QueueBuilder.durable(registrationQueueName).build();
    }

    @Bean
    public Queue registrationDeadLetterQueue() {
        return QueueBuilder.durable(registrationRouting).build();
    }

    @Bean
    public Exchange registrationExchange() {
        return ExchangeBuilder.topicExchange(registrationExchangeStr).build();
    }

    @Bean
    public Binding registrationBinding(Queue registrationQueue, TopicExchange registrationExchange) {
        return BindingBuilder.bind(registrationQueue).to(registrationExchange).with(registrationQueueName);
    }


}