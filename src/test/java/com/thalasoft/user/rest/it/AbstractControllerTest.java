package com.thalasoft.user.rest.it;

import java.util.Arrays;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thalasoft.user.rest.config.TestConfiguration;
import com.thalasoft.user.rest.config.WebConfiguration;
import com.thalasoft.user.rest.utils.CommonConstants;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = { TestConfiguration.class, WebConfiguration.class })
@RunWith(SpringRunner.class)
@WebAppConfiguration
public abstract class AbstractControllerTest {

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

	protected void addBase64UserPasswordHeaders(String username, String password, HttpHeaders httpHeaders) {
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		String usernamePassword = username + ":" + password;
		String encodedAuthorisation = Base64.getEncoder().encodeToString(usernamePassword.getBytes());
		httpHeaders.add(CommonConstants.AUTH_HEADER_NAME,
				CommonConstants.AUTH_BASIC + " " + new String(encodedAuthorisation));
	}

}
