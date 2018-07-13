package com.thalasoft.user.rest.config;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.config",
                "com.thalasoft.user.rest.service", "com.thalasoft.user.rest.bootstrap", "com.thalasoft.user.data" })
public class TestConfiguration {
}
