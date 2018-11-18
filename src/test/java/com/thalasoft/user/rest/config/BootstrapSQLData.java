package com.thalasoft.user.rest.config;

import com.thalasoft.user.rest.condition.BootstrapSQL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@BootstrapSQL
public class BootstrapSQLData implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserFixtureService userFixtureService;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        userFixtureService.addAuthenticatedUser();
    }

}
