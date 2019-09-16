package com.mastercard.labs.bps;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableSwagger2
@EnableJpaAuditing
@EnableScheduling
@EnableRabbit
@EnableAsync
@ComponentScan(basePackages = {
        "com.mastercard.labs.bps.discovery.persistence.support",
        "com.mastercard.labs.bps.discovery.filter",
        "com.mastercard.labs.bps.discovery.persistence.repository",
        "com.mastercard.labs.bps.discovery.service",
        "com.mastercard.labs.bps.discovery.configuration",
        "com.mastercard.labs.bps.discovery.schedule",
        "com.mastercard.labs.bps.discovery.controller"})
public class RESTServiceApplication implements CommandLineRunner {

    @Override
    public void run(String... arg0) throws Exception {
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new ExitException();
        }
    }

    public static void main(String[] args) throws Exception {
        new SpringApplication(RESTServiceApplication.class).run(args);
    }

    class ExitException extends RuntimeException implements ExitCodeGenerator {
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }

    }
}
