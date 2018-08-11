package com.thalasoft.user.rest.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.thalasoft.toolbox.spring.PackageBeanNameGenerator;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Component
@EnableWebMvc
@EnableSpringDataWebSupport
@ComponentScan(nameGenerator = PackageBeanNameGenerator.class, basePackages = { "com.thalasoft.user.rest.exception",
		"com.thalasoft.user.rest.controller", "com.thalasoft.user.rest.assembler" })
public class WebConfiguration implements WebMvcConfigurer {

	private static final int PAGE_DEFAULT_SIZE = 20;
	private static final int PAGE_MAX_SIZE = 50;
	private static final String LOCALE_LANGUAGE_PARAM = "lang";

	private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(Locale.ENGLISH, Locale.FRENCH);

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
		resolver.setFallbackPageable(PageRequest.of(0, PAGE_DEFAULT_SIZE));
		resolver.setMaxPageSize(PAGE_MAX_SIZE);
		argumentResolvers.add(resolver);
		// super.addArgumentResolvers(argumentResolvers); // TODO test that this line is
		// not needed, by checking the pagination is okay
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:messages/messages", "classpath:messages/validation");
		// If true, the key of the message will be displayed if the key is not
		// found, instead of throwing an exception
		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setDefaultEncoding("UTF-8");
		// The value 0 means always reload the messages to be developer friendly
		messageSource.setCacheSeconds(0);
		return messageSource;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/*").addResourceLocations("/");
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public LocaleResolver localeResolver() {
		AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
		localeResolver.setSupportedLocales(SUPPORTED_LOCALES);
		localeResolver.setDefaultLocale(Locale.ENGLISH);
		return localeResolver;
	}

	// The locale interceptor provides a way to switch the language in any page
	// just by passing the lang=’en’, lang=’fr’, and so on to the url
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName(LOCALE_LANGUAGE_PARAM);
		return localeChangeInterceptor;
	}

	// Avoid caching issue with AngularJS on IE
	@Bean
	public WebContentInterceptor webContentInterceptor() {
		WebContentInterceptor interceptor = new WebContentInterceptor();
		interceptor.setCacheSeconds(0);
		return interceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(webContentInterceptor());
	}

}
