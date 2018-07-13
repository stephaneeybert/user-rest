package com.thalasoft.user.rest.filter;

import com.thalasoft.user.rest.service.MetricsService;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@WebFilter("/*")
public class MetricsFilter implements Filter {

	private static Logger logger = LoggerFactory.getLogger(MetricsFilter.class);

	@Autowired
    private MetricsService metricsService;

    private FilterConfig config = null;
    
    @Override
    public void init(FilterConfig config) throws ServletException {
        // Enable autowiring into the filter
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
        this.config = config;
        config.getServletContext().log("Initializing MetricsFilter");
    }
    
    @Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
	    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse; 
        String url = httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI();
        int status = (httpServletResponse).getStatus();
        metricsService.increaseCount(url, status);		
        filterChain.doFilter(servletRequest, servletResponse);
	}

    @Override
    public void destroy() {
        config.getServletContext().log("Destroying MetricsFilter");
    }

}
