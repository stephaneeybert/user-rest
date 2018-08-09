package com.thalasoft.user.rest.it;

import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.thalasoft.user.rest.config.TestConfiguration;
import com.thalasoft.user.rest.config.WebConfiguration;
import com.thalasoft.user.rest.resource.AbstractResource;
import com.thalasoft.user.rest.security.WebSecurityTestConfiguration;

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

@SpringBootTest(classes = { TestConfiguration.class, WebConfiguration.class })
@RunWith(SpringRunner.class)
public abstract class BaseTest {

	@Autowired
	protected WebApplicationContext webApplicationContext;

	@Autowired
	protected FilterChainProxy springSecurityFilterChain;

	@Autowired
	protected ObjectMapper jacksonObjectMapper;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	AcceptHeaderLocaleResolver localeResolver;

	protected MockHttpSession session;

	protected MockHttpServletRequest request;

	protected MockMvc mockMvc;

	protected HttpHeaders httpHeaders;

	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();
		httpHeaders = new HttpHeaders();
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
