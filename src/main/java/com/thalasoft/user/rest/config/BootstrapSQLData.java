package com.thalasoft.user.rest.config;

import com.thalasoft.user.rest.service.UserFixtureService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "bootstrapsql", havingValue = "true")
public class BootstrapSQLData implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private UserFixtureService userFixtureService;

  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    userFixtureService.addAuthenticatedUser();
  }

}
