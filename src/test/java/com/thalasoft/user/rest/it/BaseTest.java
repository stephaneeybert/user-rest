package com.thalasoft.user.rest.it;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.config.TestConfiguration;
import com.thalasoft.user.rest.config.WebConfiguration;
import com.thalasoft.user.rest.resource.AbstractResource;
import com.thalasoft.user.rest.resource.UserResource;
import com.thalasoft.user.rest.resource.UserRoleResource;
import com.thalasoft.user.rest.security.AuthoritiesConstants;
import com.thalasoft.user.rest.security.NoSecurityConfiguration;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@SpringBootTest(classes = { NoSecurityConfiguration.class, TestConfiguration.class, WebConfiguration.class })
@RunWith(SpringRunner.class)
public abstract class BaseTest {

    protected UserResource userResource0;

	protected List<UserResource> manyUserResources;

	@Autowired
    private UserService userService;

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
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();
		httpHeaders = new HttpHeaders();
	}

	@Before
    public void createOneUserResource() throws Exception {
        userResource0 = new UserResource();
        userResource0.setFirstname("Cyril");
        userResource0.setLastname("Eybert");
        userResource0.setEmail("cyril@yahoo.es");
        Set<UserRoleResource> userRoleResources = new HashSet<UserRoleResource>();
        UserRoleResource user0AdminRoleResource = new UserRoleResource();
        user0AdminRoleResource.setRole(AuthoritiesConstants.ROLE_ADMIN.getRole());
        userRoleResources.add(user0AdminRoleResource);
        UserRoleResource user0UserRoleResource = new UserRoleResource();
        user0UserRoleResource.setRole(AuthoritiesConstants.ROLE_USER.getRole());
        userRoleResources.add(user0UserRoleResource);
        userResource0.setUserRoles(userRoleResources);
	}

	@Before
    public void createManyUserResource() throws Exception {
		manyUserResources = new ArrayList<UserResource>();
        for (int i = 0; i < 30; i++) {
            String index = intToString(i, 2);
            UserResource oneUserResource = new UserResource();
            oneUserResource.setFirstname("zfirstname" + index);
            oneUserResource.setLastname("zlastname" + index);
            oneUserResource.setEmail("zemail" + index + "@nokia.com");
            // User createdUser = userService.add(resourceService.toUser(oneUserResource));
            // oneUserResource.setResourceId(createdUser.getId());
            manyUserResources.add(oneUserResource);
        }
	}

	@After
    public void deleteUserResources() throws Exception {
        if (null != userResource0.getResourceId()) {
            userService.delete(userResource0.getResourceId());
        }
        for (UserResource userResource : manyUserResources) {
			if (null != userResource.getResourceId()) {
				userService.delete(userResource.getResourceId());
			}
        }
    }

	protected String localizeErrorMessage(String errorCode, Object args[], Locale locale) {
		return messageSource.getMessage(errorCode, args, locale);
	}

	protected String localizeErrorMessage(String errorCode, Locale locale) {
		return messageSource.getMessage(errorCode, null, locale);
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

	protected <T extends AbstractResource> T deserializeResource(final MvcResult mvcResult, Class<T> clazz) throws Exception {
    	return jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
	}

	protected <T extends AbstractResource> List<T> deserializeResources(final MvcResult mvcResult, Class<T> clazz) throws Exception {
		final CollectionType javaType = jacksonObjectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
		return jacksonObjectMapper.readValue(mvcResult.getResponse().getContentAsString(), javaType);
	}

}
