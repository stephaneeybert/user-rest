package com.thalasoft.user.rest.config;

import java.util.Arrays;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.properties",
    "com.thalasoft.user.rest.encoder", "com.thalasoft.user.rest.service", "com.thalasoft.user.data.config" })
public class ApplicationConfiguration {

  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      System.out.println("Let's inspect the beans provided by Spring Boot:");
      String[] beanNames = ctx.getBeanDefinitionNames();
      Arrays.sort(beanNames);
      for (String beanName : beanNames) {
        System.out.println("=====>> " + beanName);
      }
    };
  }

}
