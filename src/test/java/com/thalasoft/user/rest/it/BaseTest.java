package com.thalasoft.user.rest.it;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.service.UserFixtureService;
import com.thalasoft.user.rest.resource.AbstractModel;
import com.thalasoft.user.rest.resource.UserModel;
import com.thalasoft.user.rest.resource.UserRoleModel;
import com.thalasoft.user.rest.security.AuthoritiesConstants;
import com.thalasoft.user.rest.service.ModelService;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@RunWith(SpringRunner.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
    "classpath:mysql/clean-up-before-each-test.sql" })
public abstract class BaseTest {

  protected UserModel userModel0;

  protected List<UserModel> manyUserModels;

  @Autowired
  protected UserFixtureService userFixtureService;

  @Autowired
  private UserService userService;

  @Autowired
  private ModelService modelService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private FilterChainProxy springSecurityFilterChain;

  @Autowired
  protected ObjectMapper jacksonObjectMapper;

  @Autowired
  protected MessageSource messageSource;

  @Autowired
  private AcceptHeaderLocaleResolver localeResolver;

  protected MockHttpSession session;

  protected MockHttpServletRequest request;

  protected MockMvc mockMvc;

  protected HttpHeaders httpHeaders;

  @Before
  public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain)
        .build();
    httpHeaders = new HttpHeaders();
  }

  @Before
  public void createUserModels() throws Exception {
    userModel0 = new UserModel();
    userModel0.setFirstname("Cyril");
    userModel0.setLastname("Eybert");
    userModel0.setEmail("cyril@yahoo.es");
    Set<UserRoleModel> userRoleModels = new HashSet<UserRoleModel>();
    UserRoleModel user0AdminRoleModel = new UserRoleModel();
    user0AdminRoleModel.setRole(AuthoritiesConstants.ROLE_ADMIN.getRole());
    userRoleModels.add(user0AdminRoleModel);
    UserRoleModel user0UserRoleModel = new UserRoleModel();
    user0UserRoleModel.setRole(AuthoritiesConstants.ROLE_USER.getRole());
    userRoleModels.add(user0UserRoleModel);
    userModel0.setUserRoleModels(userRoleModels);

    manyUserModels = new ArrayList<UserModel>();
    for (int i = 0; i < 30; i++) {
      String index = intToString(i, 2);
      UserModel oneUserModel = new UserModel();
      oneUserModel.setFirstname("zfirstname" + index);
      oneUserModel.setLastname("zlastname" + index);
      oneUserModel.setEmail("zemail" + index + "@nokia.com");
      User createdUser = userService.add(modelService.toUser(oneUserModel));
      oneUserModel.setModelId(createdUser.getId());
      manyUserModels.add(oneUserModel);
    }
  }

  @After
  public void deleteUserModels() throws Exception {
    if (userModel0 != null && !StringUtils.isEmpty(userModel0.getModelId())) {
      userService.delete(userModel0.getModelId());
    }
    for (UserModel userModel : manyUserModels) {
      if (userModel != null && !StringUtils.isEmpty(userModel.getModelId())) {
        userService.delete(userModel.getModelId());
      }
    }
  }

  protected String localizeErrorMessage(String errorCode, Object args[], Locale locale) {
    return messageSource.getMessage(errorCode, args, locale);
  }

  protected String localizeErrorMessage(String errorCode, Locale locale) {
    return messageSource.getMessage(errorCode, null, locale);
  }

  protected Locale getDefaultLocale() {
    return localeResolver.getDefaultLocale();
  }

  protected String localizeErrorMessage(String errorCode, Object args[]) {
    Locale locale = localeResolver.getDefaultLocale();
    return messageSource.getMessage(errorCode, args, locale);
  }

  protected String localizeErrorMessage(String errorCode) {
    Locale locale = localeResolver.getDefaultLocale();
    return messageSource.getMessage(errorCode, null, locale);
  }

  protected String intToString(int num, int digits) {
    String output = Integer.toString(num);
    while (output.length() < digits) {
      output = "0" + output;
    }
    return output;
  }

  protected <T extends Object> T deserialize(final MvcResult mvcResult, Class<T> clazz) throws Exception {
    return jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
  }

  protected <T extends AbstractModel> T deserializeModel(final MvcResult mvcResult, Class<T> clazz) throws Exception {
    return jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
  }

  protected <T extends AbstractModel> List<T> deserializeModels(final MvcResult mvcResult, Class<T> clazz)
      throws Exception {
    final CollectionType javaType = jacksonObjectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
    return jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), javaType);
  }

}
