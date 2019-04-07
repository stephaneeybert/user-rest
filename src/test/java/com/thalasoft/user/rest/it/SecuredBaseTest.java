package com.thalasoft.user.rest.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Arrays;
import java.util.Base64;

import com.thalasoft.user.rest.config.TestConfiguration;
import com.thalasoft.user.rest.service.UserFixtureService;
import com.thalasoft.user.rest.config.WebConfiguration;
import com.thalasoft.user.rest.security.TestSecurityConfiguration;
import com.thalasoft.user.rest.security.oauth2.AuthorizationServerConfiguration;
import com.thalasoft.user.rest.security.oauth2.ClientFixtureService;
import com.thalasoft.user.rest.security.service.TokenAuthenticationService;
import com.thalasoft.user.rest.utils.CommonConstants;
import com.thalasoft.user.rest.utils.DomainConstants;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(classes = { TestSecurityConfiguration.class, TestConfiguration.class, WebConfiguration.class })
public abstract class SecuredBaseTest extends BaseTest {

  @Autowired
  private TokenAuthenticationService tokenAuthenticationService;

  @Before
  public void initFixture() {
    userFixtureService.addAuthenticatedUser();
  }

  @After
  public void cleanFixture() {
    userFixtureService.removeAuthenticatedUser();
  }

  @Before
  public void setup() throws Exception {
    super.setup();

    addTokenToRequestHeader(httpHeaders, ClientFixtureService.CLIENT_ID, UserFixtureService.USER_EMAIL, UserFixtureService.USER_PASSWORD);
  }

  private void addTokenToRequestHeader(HttpHeaders headers, String oauthClientId, String username, String password) throws Exception {
    String token = getOAuthAccessToken(oauthClientId, username, password);
    headers.remove(CommonConstants.ACCESS_TOKEN_HEADER_NAME);
    headers.add(CommonConstants.ACCESS_TOKEN_HEADER_NAME, tokenAuthenticationService.buildOAuthAccessToken(token));
  }

  private void addBase64UserPasswordHeaders(String username, String password, HttpHeaders httpHeaders) {
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    String usernamePassword = username + ":" + password;
    String encodedAuthorisation = Base64.getEncoder().encodeToString(usernamePassword.getBytes(UTF_8));
    httpHeaders.add(CommonConstants.ACCESS_TOKEN_HEADER_NAME,
        CommonConstants.AUTH_BASIC + " " + new String(encodedAuthorisation));
  }

  private String getOAuthAccessToken(String oauthClientId, String username, String password) throws Exception {
    MultiValueMap<String, String> oauthParams = new LinkedMultiValueMap<>();
    oauthParams.add("grant_type", AuthorizationServerConfiguration.OAUTH_GRANT_TYPE_PASSWORD);
    oauthParams.add("client_id", oauthClientId);
    oauthParams.add("username", username);
    oauthParams.add("password", password);
    addBase64UserPasswordHeaders(AuthorizationServerConfiguration.OAUTH_CLIENT_ID, AuthorizationServerConfiguration.OAUTH_CLIENT_SECRET, httpHeaders);
    ResultActions mvcResult = this.mockMvc
        .perform(post(RESTConstants.SLASH + DomainConstants.AUTH + RESTConstants.SLASH + DomainConstants.TOKEN)
        .headers(httpHeaders)
        .params(oauthParams)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
    String resultString = mvcResult.andReturn().getResponse().getContentAsString();
    JacksonJsonParser jsonParser = new JacksonJsonParser();
    return jsonParser.parseMap(resultString).get("access_token").toString();
  }

}
