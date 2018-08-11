package com.thalasoft.user.rest.config;

import com.thalasoft.toolbox.condition.EnvProd;
import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@EnvProd
@Component
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.security" })
public class ApplicationSecurityConfiguration {
}
