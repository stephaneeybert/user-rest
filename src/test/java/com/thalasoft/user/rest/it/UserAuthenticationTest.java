package com.thalasoft.user.rest.it;

import static com.thalasoft.user.rest.assertion.UserResourceAssert.assertThatUserResource;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.thalasoft.user.rest.resource.UserResource;
import com.thalasoft.user.rest.service.UserActionService;
import com.thalasoft.user.rest.service.resource.CredentialsResource;
import com.thalasoft.user.rest.utils.RESTConstants;
import com.thalasoft.user.rest.utils.DomainConstants;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class UserAuthenticationTest extends SecuredBaseTest {

    @Autowired
    private UserActionService userActionService;

    @Test
    public void testUnsecuredResourceGrantsAccess() throws Exception {
        this.mockMvc.perform(
        get(RESTConstants.SLASH)
        .accept(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        this.mockMvc.perform(
        get(RESTConstants.SLASH + DomainConstants.ERROR + "/npe")
        .accept(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andReturn();

        this.mockMvc.perform(
        post(RESTConstants.SLASH + DomainConstants.AUTH + RESTConstants.SLASH + DomainConstants.LOGIN)
        .accept(MediaType.APPLICATION_JSON)
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();
    }

    @Test
    public void testSecuredResourceDeniesAccessToNonLoggedInUser() throws Exception {
        String password = "mynewpassword";
        MvcResult mvcResult = this.mockMvc
                .perform(put(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH
                + "1" + RESTConstants.SLASH + DomainConstants.PASSWORD)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(jacksonObjectMapper.writeValueAsString(password)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testUpdatePasswordAndLogin() throws Exception {
        MvcResult mvcResult = this.mockMvc
                .perform(post(RESTConstants.SLASH + DomainConstants.USERS)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders)
                .content(jacksonObjectMapper.writeValueAsString(userResource0)))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();
        UserResource retrievedUserResource = deserializeResource(mvcResult, UserResource.class);
        assertThatUserResource(retrievedUserResource).hasId(retrievedUserResource.getResourceId());
        userResource0.setResourceId(retrievedUserResource.getResourceId());

        String password = "mynewpassword";
        mvcResult = this.mockMvc
                .perform(put(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH
                + retrievedUserResource.getResourceId() + RESTConstants.SLASH + DomainConstants.PASSWORD)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders)
                .content(jacksonObjectMapper.writeValueAsString(password)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").exists())
                .andReturn();

        CredentialsResource credentialsResource = new CredentialsResource();
        credentialsResource.setEmail(retrievedUserResource.getEmail());
        credentialsResource.setPassword(password);
        mvcResult = this.mockMvc.perform(
                post(RESTConstants.SLASH + DomainConstants.AUTH + RESTConstants.SLASH + DomainConstants.LOGIN)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders)
                .content(jacksonObjectMapper.writeValueAsString(credentialsResource)))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();
        retrievedUserResource = deserializeResource(mvcResult, UserResource.class);
        assertThatUserResource(retrievedUserResource).hasEmail(retrievedUserResource.getEmail());

        String sialToken = userActionService.signAction(DomainConstants.CONFIRM_EMAIL,
                retrievedUserResource.getResourceId());
        mvcResult = this.mockMvc
                .perform(get(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH
                + retrievedUserResource.getResourceId() + RESTConstants.SLASH
                + DomainConstants.CONFIRM_EMAIL).param("sialToken", sialToken)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .headers(httpHeaders) 
                )
                .andDo(print())
                .andExpect(status().isOk()).andReturn();
        retrievedUserResource = deserializeResource(mvcResult, UserResource.class);
        assertThatUserResource(retrievedUserResource).isEmailConfirmed();
    }

}
