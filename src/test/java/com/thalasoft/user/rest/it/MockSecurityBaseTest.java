package com.thalasoft.user.rest.it;

import java.util.Arrays;
import java.util.Base64;

import com.thalasoft.user.rest.config.TestConfiguration;
import com.thalasoft.user.rest.config.WebConfiguration;
import com.thalasoft.user.rest.security.MockSecurityConfiguration;
import com.thalasoft.user.rest.utils.CommonConstants;

import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@SpringBootTest(classes = { MockSecurityConfiguration.class, TestConfiguration.class, WebConfiguration.class })
public abstract class MockSecurityBaseTest extends BaseTest {

	protected final String USER = "stephane";
	protected final String PASSWORD = "mypassword";

	@Before
	public void setup() throws Exception {
		super.setup();
		addBase64UserPasswordHeaders(USER, PASSWORD, httpHeaders);
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
