package com.thalasoft.user.rest.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thalasoft.user.rest.config.TestConfiguration;
import com.thalasoft.user.rest.config.WebConfiguration;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = { TestConfiguration.class, WebConfiguration.class })
@RunWith(SpringRunner.class)
public abstract class AbstractTest {

	@Autowired
	protected WebApplicationContext webApplicationContext;

	@Autowired
	protected FilterChainProxy springSecurityFilterChain;

	@Autowired
	protected ObjectMapper jacksonObjectMapper;

	@Autowired
	protected MessageSource messageSource;

	protected HttpHeaders httpHeaders;

	protected void createHeaders() {
		httpHeaders = new HttpHeaders();
	}

}
