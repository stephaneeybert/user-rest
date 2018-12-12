package com.thalasoft.user.rest.config;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.properties",
        "com.thalasoft.user.rest.service", "com.thalasoft.user.data.config" })
public class ApplicationConfiguration {
}
