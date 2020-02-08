package com.thalasoft.user.rest.config;

import com.thalasoft.user.rest.service.CredentialsServiceImpl;
import com.thalasoft.user.rest.service.MetricsServiceImpl;
import com.thalasoft.user.rest.service.ModelServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ServiceConfiguration {

  @Bean
  public CredentialsServiceImpl credentialsService() {
    return new CredentialsServiceImpl();
  }

  @Bean
  public ModelServiceImpl modelService() {
    return new ModelServiceImpl();
  }

  @Bean
  public MetricsServiceImpl metricsService() {
    return new MetricsServiceImpl();
  }

}
