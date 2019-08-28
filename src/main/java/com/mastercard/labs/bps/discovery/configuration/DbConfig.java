package com.mastercard.labs.bps.discovery.configuration;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.HashMap;

public class DbConfig {

    protected LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto",  "update");
        properties.put("hibernate.jdbc.time_zone", "UTC");
        properties.put("hibernate.enable_lazy_load_no_trans", "true");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.physical_naming_strategy", "com.mastercard.labs.bps.discovery.persistence.support.PhysicalNamingStrategyImpl");
        properties.put("javax.persistence.validation.mode", "none");
        properties.put("javax.persistence.schema-generation.database.action", "create");
        properties.put("javax.persistence.schema-generation.create-source", "script-then-metadata");
        properties.put("javax.persistence.schema-generation.create-script-source", "META-INF/schema.sql");
        em.setJpaPropertyMap(properties);
        return em;
    }

}
