package com.thalasoft.user.rest.config;

import com.thalasoft.user.rest.service.CredentialsServiceImpl;
import com.thalasoft.user.rest.service.MetricsServiceImpl;
import com.thalasoft.user.rest.service.ResourceServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    @Bean
    public CredentialsServiceImpl credentialsService() {
        return new CredentialsServiceImpl();
    }

    @Bean
    public ResourceServiceImpl resourceService() {
        return new ResourceServiceImpl();
    }

    @Bean
    public MetricsServiceImpl metricsService() {
        return new MetricsServiceImpl();
    }

}
