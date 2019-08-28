package com.mastercard.labs.bps.discovery.configuration;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.mastercard.labs.bps.discovery.persistence.repository",
        entityManagerFactoryRef = "journalEntityManagerFactory",
        transactionManagerRef = "journalTransactionManager")
public class JournalConfig extends DbConfig {

    @Autowired
    private Environment environment;

    @Bean
    public LocalContainerEntityManagerFactoryBean journalEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = entityManagerFactory();
        em.setDataSource(journalDataSource());
        em.setPackagesToScan("com.mastercard.labs.bps.discovery.domain");
        return em;
    }

    @Bean
    public PlatformTransactionManager journalTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(journalEntityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean(name = "journalDataSource")
    public DataSource journalDataSource() {
        HikariDataSource hikariDataSource = (HikariDataSource) DataSourceBuilder.create()
                .username(environment.getProperty("spring.datasource.username"))
                .password(environment.getProperty("spring.datasource.password"))
                .url(environment.getProperty("journal.datasource.url"))
                .driverClassName(environment.getProperty("journal.datasource.driver-class-name"))
                .build();
        hikariDataSource.setConnectionTimeout(Long.parseLong(environment.getProperty("spring.datasource.hikari.connection-timeout")));
        hikariDataSource.setIdleTimeout(Long.parseLong(environment.getProperty("spring.datasource.hikari.idle-timeout")));
        hikariDataSource.setMaxLifetime(Long.parseLong(environment.getProperty("spring.datasource.hikari.max-lifetime")));
        hikariDataSource.setMaximumPoolSize(Integer.parseInt(environment.getProperty("spring.datasource.hikari.maximum-pool-size")));
        hikariDataSource.setMinimumIdle(Integer.parseInt(environment.getProperty("spring.datasource.hikari.minimum-idle")));
        return hikariDataSource;
    }

}
