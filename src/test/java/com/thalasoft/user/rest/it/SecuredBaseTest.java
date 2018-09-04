package com.thalasoft.user.rest.it;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Arrays;
import java.util.Base64;

import com.thalasoft.user.rest.config.TestConfiguration;
import com.thalasoft.user.rest.config.UserFixtureService;
import com.thalasoft.user.rest.config.WebConfiguration;
import com.thalasoft.user.rest.security.ProdSecurityConfiguration;
import com.thalasoft.user.rest.service.TokenAuthenticationService;
import com.thalasoft.user.rest.utils.CommonConstants;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@SpringBootTest(classes = { ProdSecurityConfiguration.class, TestConfiguration.class, WebConfiguration.class })
public abstract class SecuredBaseTest extends BaseTest {

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	@Before
	public void setup() throws Exception {
		super.setup();
		
		addTokenToRequestHeader(httpHeaders, UserFixtureService.USER_EMAIL);
	}

	private void addTokenToRequestHeader(HttpHeaders headers, String username) {
		tokenAuthenticationService.addAccessTokenToResponseHeader(headers, username);
	}

	private void addBase64UserPasswordHeaders(String username, String password, HttpHeaders httpHeaders) {
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		String usernamePassword = username + ":" + password;
		String encodedAuthorisation = Base64.getEncoder().encodeToString(usernamePassword.getBytes(UTF_8));
		httpHeaders.add(CommonConstants.ACCESS_TOKEN_HEADER_NAME,
				CommonConstants.AUTH_BASIC + " " + new String(encodedAuthorisation));
	}

}
