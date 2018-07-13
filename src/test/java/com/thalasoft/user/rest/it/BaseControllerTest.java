package com.thalasoft.user.rest.it;

import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.thalasoft.user.rest.resource.AbstractResource;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

public abstract class BaseControllerTest extends AbstractControllerTest {

	@Autowired
    AcceptHeaderLocaleResolver localeResolver;
    
	protected MockHttpSession session;

    protected MockHttpServletRequest request;

	protected MockMvc mockMvc;

	protected final String USER = "stephane";
    protected final String PASSWORD = "mypassword";

    @Before
   	public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

		createHeaders();
		addBase64UserPasswordHeaders(USER, PASSWORD, httpHeaders);
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
