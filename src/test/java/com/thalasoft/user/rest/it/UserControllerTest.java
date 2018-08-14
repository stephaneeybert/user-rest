package com.thalasoft.user.rest.it;

import static com.thalasoft.user.rest.assertion.UserResourceAssert.assertThatUserResource;
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
import com.thalasoft.user.rest.resource.UserResource;
import com.thalasoft.user.rest.service.UserActionService;
import com.thalasoft.user.rest.service.resource.CredentialsResource;
import com.thalasoft.user.rest.utils.RESTConstants;
import com.thalasoft.user.rest.utils.UserDomainConstants;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class UserControllerTest extends BaseTest {

    @Autowired
    private UserActionService userActionService;

    @Test
    public void testCrudOperations() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(post(RESTConstants.SLASH + UserDomainConstants.USERS)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders)
                .content(jacksonObjectMapper.writeValueAsString(userResource0)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").exists())
                .andExpect(jsonPath("$.firstname").value(userResource0.getFirstname()))
                .andExpect(jsonPath("$.lastname").value(userResource0.getLastname()))
                .andExpect(jsonPath("$.email").value(userResource0.getEmail()))
                .andExpect(header().string("Location", Matchers.containsString("/users/"))).andReturn();
        UserResource retrievedUserResource = deserializeResource(mvcResult, UserResource.class);
        assertThatUserResource(retrievedUserResource).hasEmail(userResource0.getEmail());
        assertThatUserResource(retrievedUserResource)
                .hasRole(retrievedUserResource.getUserRoles().iterator().next().getRole());
        userResource0.setResourceId(retrievedUserResource.getResourceId());

        mvcResult = this.mockMvc
                .perform(get(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH
                + retrievedUserResource.getResourceId())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        retrievedUserResource = deserializeResource(mvcResult, UserResource.class);
        assertThatUserResource(retrievedUserResource).hasEmail(userResource0.getEmail());

        String changedFirstname = "Cyril";
        retrievedUserResource.setFirstname(changedFirstname);
        mvcResult = this.mockMvc
                .perform(put(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH
                + retrievedUserResource.getResourceId())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(jacksonObjectMapper.writeValueAsString(retrievedUserResource)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.firstname").value(changedFirstname)).andReturn();
        retrievedUserResource = deserializeResource(mvcResult, UserResource.class);
        assertThatUserResource(retrievedUserResource).hasFirstname(changedFirstname);

        this.mockMvc
                .perform(delete(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH
                + retrievedUserResource.getResourceId())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        userResource0.setResourceId(null);

        this.mockMvc
                .perform(get(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH
                + retrievedUserResource.getResourceId())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPostInvalidUserShouldReturnValidationErrorMessages() throws Exception {
        UserResource faultyUserResource = new UserResource();
        faultyUserResource.setFirstname("Stephane");
        faultyUserResource.setLastname("Eybert");
        faultyUserResource.setEmail("notvalidemail");
        MvcResult mvcResult = this.mockMvc
                .perform(post(RESTConstants.SLASH + UserDomainConstants.USERS)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).locale(Locale.FRENCH)
                .headers(httpHeaders)
                .content(jacksonObjectMapper.writeValueAsString(faultyUserResource)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(localizeErrorMessage("error.failed.controller.validation", Locale.FRENCH)))
                .andReturn();
        ErrorFormInfo retrievedMessage = deserialize(mvcResult, ErrorFormInfo.class);
        assertEquals(retrievedMessage.getHttpStatus(), HttpStatus.BAD_REQUEST);
        assertEquals(retrievedMessage.getMessage(), localizeErrorMessage("error.failed.controller.validation", Locale.FRENCH));
        assertEquals(retrievedMessage.getFieldErrors().size(), 1);
    }

    @Test
    public void testSearchShouldReturnSome() throws Exception {
        this.mockMvc
                .perform(get(RESTConstants.SLASH + UserDomainConstants.USERS)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .param("searchTerm", "irstnam").param("page", "1").param("size", "10")
                .param("sort", "lastname,asc").param("sort", "firstname,asc"))
                .andDo(print())
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.page.number").value(1)).andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.totalPages").value(3)).andExpect(jsonPath("$.page.totalElements").value(30))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.next.href").exists())
                .andExpect(jsonPath("$._links.prev.href").exists())
                .andExpect(jsonPath("$._embedded.userResourceList[0].firstname").exists())
                .andExpect(jsonPath("$._embedded.userResourceList[0].firstname").value(manyUserResources.get(10).getFirstname()))
                .andExpect(jsonPath("$._embedded.userResourceList[0].lastname").value(manyUserResources.get(10).getLastname()))
                .andExpect(jsonPath("$._embedded.userResourceList[0].email").value(manyUserResources.get(10).getEmail()))
                .andExpect(header().string("Location", Matchers.containsString("/users?searchTerm="))).andReturn();
    }

    @Test
    public void testUpdatePasswordAndLogin() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(post(RESTConstants.SLASH + UserDomainConstants.USERS)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(jacksonObjectMapper.writeValueAsString(userResource0)))
                .andExpect(status().isCreated()).andReturn();
        UserResource retrievedUserResource = deserializeResource(mvcResult, UserResource.class);
        assertThatUserResource(retrievedUserResource).hasId(retrievedUserResource.getResourceId());
        userResource0.setResourceId(retrievedUserResource.getResourceId());

        String password = "mynewpassword";
        mvcResult = this.mockMvc
                .perform(put(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH
                + retrievedUserResource.getResourceId() + RESTConstants.SLASH + UserDomainConstants.PASSWORD)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(jacksonObjectMapper.writeValueAsString(password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").exists())
                .andReturn();

        CredentialsResource credentialsResource = new CredentialsResource();
        credentialsResource.setEmail(retrievedUserResource.getEmail());
        credentialsResource.setPassword(password);
        mvcResult = this.mockMvc.perform(
                post(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH + UserDomainConstants.LOGIN)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(jacksonObjectMapper.writeValueAsString(credentialsResource)))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();
        retrievedUserResource = deserializeResource(mvcResult, UserResource.class);
        assertThatUserResource(retrievedUserResource).hasEmail(retrievedUserResource.getEmail());

        String sialToken = userActionService.signAction(UserDomainConstants.CONFIRM_EMAIL,
                retrievedUserResource.getResourceId());
        mvcResult = this.mockMvc
                .perform(get(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH
                + retrievedUserResource.getResourceId() + RESTConstants.SLASH
                + UserDomainConstants.CONFIRM_EMAIL)
                .param("sialToken", sialToken)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        retrievedUserResource = deserializeResource(mvcResult, UserResource.class);
        assertThatUserResource(retrievedUserResource).isEmailConfirmed();
    }

}
