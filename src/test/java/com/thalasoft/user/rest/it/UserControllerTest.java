package com.thalasoft.user.rest.it;

import static com.thalasoft.user.rest.assertion.UserModelAssert.assertThatUserModel;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import com.thalasoft.user.rest.exception.ErrorFormInfo;
import com.thalasoft.user.rest.resource.UserModel;
import com.thalasoft.user.rest.utils.DomainConstants;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class UserControllerTest extends UnsecuredBaseTest {

  private static final String LOCALE_LANGUAGE_PARAM = "lang";

  @Test
  public void testCrudOperations() throws Exception {
    MvcResult mvcResult = this.mockMvc
        .perform(post(RESTConstants.SLASH + DomainConstants.USERS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(jacksonObjectMapper.writeValueAsString(userModel0)))
        .andDo(print()).andExpect(status().isCreated()).andExpect(jsonPath("$.firstname").exists())
        .andExpect(jsonPath("$.firstname").value(userModel0.getFirstname()))
        .andExpect(jsonPath("$.lastname").value(userModel0.getLastname()))
        .andExpect(jsonPath("$.email").value(userModel0.getEmail()))
        .andExpect(header().string("Location", Matchers.containsString("/users/"))).andReturn();
    UserModel retrievedUserModel = deserializeModel(mvcResult, UserModel.class);
    assertThatUserModel(retrievedUserModel).hasEmail(userModel0.getEmail());
    assertThatUserModel(retrievedUserModel).hasRole(retrievedUserModel.getUserRoleModels().iterator().next().getRole());
    userModel0.setModelId(retrievedUserModel.getModelId());

    mvcResult = this.mockMvc
        .perform(
            get(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + retrievedUserModel.getModelId())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andReturn();
    retrievedUserModel = deserializeModel(mvcResult, UserModel.class);
    assertThatUserModel(retrievedUserModel).hasEmail(userModel0.getEmail());

    String changedFirstname = "Cyril";
    retrievedUserModel.setFirstname(changedFirstname);
    mvcResult = this.mockMvc
        .perform(
            put(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + retrievedUserModel.getModelId())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(jacksonObjectMapper.writeValueAsString(retrievedUserModel)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.firstname").value(changedFirstname))
        .andReturn();
    retrievedUserModel = deserializeModel(mvcResult, UserModel.class);
    assertThatUserModel(retrievedUserModel).hasFirstname(changedFirstname);

    this.mockMvc
        .perform(
            delete(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + retrievedUserModel.getModelId())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk());
    userModel0.setModelId(null);

    this.mockMvc
        .perform(
            get(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + retrievedUserModel.getModelId())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  public void testPostInvalidUserShouldReturnValidationErrorMessages() throws Exception {
    UserModel faultyUserModel = new UserModel();
    faultyUserModel.setFirstname("Stephane");
    faultyUserModel.setLastname("Eybert");
    faultyUserModel.setEmail("notvalidemail");
    MvcResult mvcResult = this.mockMvc
        .perform(post(RESTConstants.SLASH + DomainConstants.USERS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(jacksonObjectMapper.writeValueAsString(faultyUserModel)))
        .andDo(print()).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.failed.controller.validation"))).andReturn();
    ErrorFormInfo retrievedMessage = deserialize(mvcResult, ErrorFormInfo.class);
    assertEquals(retrievedMessage.getHttpStatus(), HttpStatus.BAD_REQUEST);
    assertEquals(retrievedMessage.getMessage(), localizeErrorMessage("error.failed.controller.validation"));
    assertEquals(retrievedMessage.getFieldErrors().size(), 1);
  }

  @Test
  public void testPostWithLocaleHeaderReturnsLocalizedErrorMessages() throws Exception {
    UserModel faultyUserModel = new UserModel();
    faultyUserModel.setFirstname("Stephane");
    faultyUserModel.setLastname("Eybert");
    faultyUserModel.setEmail("notvalidemail");
    MvcResult mvcResult = this.mockMvc
        .perform(post(RESTConstants.SLASH + DomainConstants.USERS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).locale(Locale.FRENCH)
            .content(jacksonObjectMapper.writeValueAsString(faultyUserModel)))
        .andDo(print()).andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message").value(localizeErrorMessage("error.failed.controller.validation", Locale.FRENCH)))
        .andReturn();
    ErrorFormInfo retrievedMessage = deserialize(mvcResult, ErrorFormInfo.class);
    assertEquals(retrievedMessage.getMessage(),
        localizeErrorMessage("error.failed.controller.validation", Locale.FRENCH));
  }

  @Test
  public void testRequestWithLocaleUrlParamReturnsLocalizedErrorMessages() throws Exception {
    MvcResult mvcResult = this.mockMvc
        .perform(get(RESTConstants.SLASH + DomainConstants.USERS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).param("searchTerm", "nothing to be found")
        // .param(LOCALE_LANGUAGE_PARAM, "fr") TODO Trigger a 500 error - See
        // https://jira.spring.io/browse/SPR-16775
        ).andDo(print()).andExpect(status().isNotFound())
        // TODO
        // .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.entity.not.found",
        // Locale.FRENCH)))
        .andReturn();
    // ErrorFormInfo retrievedMessage = deserialize(mvcResult, ErrorFormInfo.class);
    // TODO assertEquals(retrievedMessage.getMessage(),
    // localizeErrorMessage("error.failed.controller.validation", Locale.FRENCH));
  }

  @Test
  public void testDefaultLocaleIsEnglish() throws Exception {
    assertEquals(Locale.ENGLISH.getLanguage(), getDefaultLocale().getLanguage());
    assertEquals(Locale.ENGLISH.getLanguage(), getDefaultLocale().getLanguage());
  }

  @Test
  public void testRequestWithNonSupportedLocaleReturnsDefaultLocaleErrorMessages() throws Exception {
    UserModel faultyUserModel = new UserModel();
    faultyUserModel.setFirstname("Stephane");
    faultyUserModel.setLastname("Eybert");
    faultyUserModel.setEmail("notvalidemail");
    MvcResult mvcResult = this.mockMvc
        .perform(post(RESTConstants.SLASH + DomainConstants.USERS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).locale(Locale.GERMAN)
            .content(jacksonObjectMapper.writeValueAsString(faultyUserModel)))
        .andDo(print()).andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message").value(localizeErrorMessage("error.failed.controller.validation", getDefaultLocale())))
        .andReturn();
    ErrorFormInfo retrievedMessage = deserialize(mvcResult, ErrorFormInfo.class);
    assertEquals(retrievedMessage.getMessage(),
        localizeErrorMessage("error.failed.controller.validation", getDefaultLocale()));
  }

  @Test
  public void testSearchShouldReturnSome() throws Exception {
    this.mockMvc
        .perform(get(RESTConstants.SLASH + DomainConstants.USERS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).param("searchTerm", "irstnam").param("page", "1").param("size", "10")
            .param("sort", "lastname,asc").param("sort", "firstname,asc"))
        .andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.page.number").value(1)).andExpect(jsonPath("$.page.size").value(10))
        .andExpect(jsonPath("$.page.totalPages").value(3)).andExpect(jsonPath("$.page.totalElements").value(30))
        .andExpect(jsonPath("$._links.self.href").exists()).andExpect(jsonPath("$._links.next.href").exists())
        .andExpect(jsonPath("$._links.prev.href").exists())
        .andExpect(jsonPath("$._embedded.userModelList[0].firstname").exists())
        .andExpect(jsonPath("$._embedded.userModelList[0].firstname").value(manyUserModels.get(10).getFirstname()))
        .andExpect(jsonPath("$._embedded.userModelList[0].lastname").value(manyUserModels.get(10).getLastname()))
        .andExpect(jsonPath("$._embedded.userModelList[0].email").value(manyUserModels.get(10).getEmail()))
        .andExpect(header().string("Location", Matchers.containsString("/users?searchTerm="))).andReturn();
  }

  @Test
  public void testPaginationIsZeroIndexed() throws Exception {
    this.mockMvc
        .perform(get(RESTConstants.SLASH + DomainConstants.USERS).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).param("searchTerm", "irstnam").param("page", "1").param("size", "10")
            .param("sort", "lastname,asc").param("sort", "firstname,asc"))
        .andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$._links.first.href", Matchers.containsString("page=0")))
        .andExpect(jsonPath("$._links.prev.href", Matchers.containsString("page=0")))
        .andExpect(jsonPath("$._links.self.href", Matchers.containsString("/users?searchTerm=irstnam")))
        .andExpect(jsonPath("$._links.next.href", Matchers.containsString("page=2")))
        .andExpect(jsonPath("$.page.size").value(10)).andExpect(jsonPath("$.page.totalElements").value(30))
        .andExpect(jsonPath("$.page.totalPages").value(3))
        // TODO https://jira.spring.io/browse/DATACMNS-563
        .andExpect(jsonPath("$.page.number").value(1)).andReturn();
  }

}
