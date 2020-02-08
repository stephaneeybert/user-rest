package com.thalasoft.user.rest.security;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = {
    "com.thalasoft.user.rest.security" }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = NoSecurityConfiguration.class))
public class ProdSecurityConfiguration {
}
