package com.thalasoft.user.rest.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import com.thalasoft.user.data.jpa.domain.EmailAddress;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.resource.UserModel;
import com.thalasoft.user.rest.utils.RESTConstants;
import com.thalasoft.user.rest.utils.DomainConstants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MvcResult;

public class UserExceptionTest extends UnsecuredBaseTest {

  @Autowired
  UserService userService;

  @Autowired
  MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

  private User user0;

  @Before
  public void beforeAnyTest() throws Exception {
    user0 = new User();
    user0.setFirstname("Marie");
    user0.setLastname("Eybert");
    user0.setEmail(new EmailAddress("marie@yahoo.se"));
    user0.setPassword("e41de4c55873f9c000f4cdaac6efd3aa");
    user0.setPasswordSalt("7bc7bf5f94fef7c7106afe5c3a40a2");
  }

  @After
  public void afterAnyTest() throws Exception {
    if (null != user0 && null != user0.getId()) {
      userService.delete(user0.getId());
    }
  }

  @Test
  public void testInvalidUserIdTriggersTypeMismatchException() throws Exception {
    String id = "dummy";

    this.mockMvc
        .perform(get(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + id)
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.url").value("http://localhost/users/dummy"))
        .andExpect(
            jsonPath("$.message").value(localizeErrorMessage("error.entity.id.invalid", new Object[] { "dummy" })))
        .andReturn();
  }

  @Test
  public void testAddingExistingUserTriggersEntityAlreadyExistException() throws Exception {
    MvcResult mvcResult = this.mockMvc
        .perform(post(RESTConstants.SLASH + DomainConstants.USERS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content("{ \"firstname\" : \"" + user0.getFirstname() + "\", \"lastname\" : \"" + user0.getLastname()
                + "\", \"email\" : \"" + user0.getEmail() + "\", \"password\" : \"" + user0.getPassword() + "\" }"))
        .andDo(print()).andExpect(status().isCreated()).andReturn();
    UserModel retrievedUserModel = deserialize(mvcResult, UserModel.class);
    user0.setId(retrievedUserModel.getModelId());

    this.mockMvc
        .perform(post(RESTConstants.SLASH + DomainConstants.USERS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content("{ \"firstname\" : \"" + user0.getFirstname() + "\", \"lastname\" : \"" + user0.getLastname()
                + "\", \"email\" : \"" + user0.getEmail() + "\", \"password\" : \"" + user0.getPassword() + "\" }"))
        .andDo(print()).andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("The entity already exists.")).andReturn();
  }

  @Test
  public void testExceptionLocalizedMessage() throws Exception {
    this.mockMvc.perform(get("/error/npe").locale(Locale.FRENCH).accept(MediaType.APPLICATION_JSON)).andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.npe", Locale.FRENCH))).andReturn();

    this.mockMvc.perform(get("/error/npe").locale(Locale.FRENCH).accept(MediaType.APPLICATION_JSON)).andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.npe", Locale.FRENCH))).andReturn();

    this.mockMvc.perform(get("/error/npe").locale(Locale.FRENCH).accept(MediaType.APPLICATION_JSON)).andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.npe", Locale.FRENCH))).andReturn();

    this.mockMvc.perform(get("/error/npe").locale(Locale.FRENCH).accept(MediaType.APPLICATION_JSON)).andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.npe", Locale.FRENCH))).andReturn();
  }

  @Test
  public void testCannotEncodePasswordException() throws Exception {
    this.mockMvc.perform(get("/error/cannotEncodePassword").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.cannot.encode.password"))).andReturn();
  }

  @Test
  public void testHttpMessageNotReadableException() throws Exception {
    this.mockMvc.perform(get("/error/httpBody").accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.http.request.body.cannot.be.parsed")))
        .andReturn();
  }

  @Test
  public void testInvalidDataAccessApiUsageException() throws Exception {
    this.mockMvc.perform(get("/error/dao").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.dao"))).andReturn();
  }

  @Test
  public void testNumberFormatException() throws Exception {
    this.mockMvc.perform(get("/error/nfe").accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.nfe"))).andReturn();
  }

  @Test
  public void testNullPointerException() throws Exception {
    this.mockMvc.perform(get("/error/npe").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.npe"))).andReturn();
  }

  @Test
  public void testRuntimeException() throws Exception {
    this.mockMvc.perform(get("/error/rte").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.rte"))).andReturn();
  }

}
