package com.thalasoft.user.rest.filter;

import com.thalasoft.user.rest.utils.CommonConstants;

import org.springframework.stereotype.Component;

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

@Component
@WebFilter(filterName = "simpleCORSFilter")
public class SimpleCORSFilter implements Filter {

	private static final String ORIGIN = "Origin";
	private static final String OPTIONS = "OPTIONS";
	private static final String OK = "OK";

	private FilterConfig config = null;

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		config.getServletContext().log("Initializing SimpleCORSFilter");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

		if (httpServletRequest.getHeader(ORIGIN) != null) {
			String origin = httpServletRequest.getHeader(ORIGIN);
			httpServletResponse.setHeader("Access-Control-Allow-Origin", origin);
			httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
			httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
			httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
			httpServletResponse.setHeader("Access-Control-Allow-Headers",
					"Accept-Language,Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization,Content-Disposition,Content-Length,Pragma,Cache-Control,"
							+ CommonConstants.EXPORT_FILENAME_HEADER_NAME + "," + CommonConstants.ACCESS_TOKEN_HEADER_NAME + "," + CommonConstants.REFRESH_TOKEN_HEADER_NAME);
			// Allow more than the 6 default headers to be returned, as the content length
			// is required for a download file request to get the file size
			httpServletResponse.setHeader("Access-Control-Expose-Headers",
					"Accept-Language,Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization,Content-Disposition,Content-Length,"
							+ CommonConstants.EXPORT_FILENAME_HEADER_NAME + "," + CommonConstants.ACCESS_TOKEN_HEADER_NAME + "," + CommonConstants.REFRESH_TOKEN_HEADER_NAME);
		}

		if (httpServletRequest.getMethod().equals(OPTIONS)) {
			try {
				httpServletResponse.getWriter().print(OK);
				httpServletResponse.getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	public void destroy() {
		config.getServletContext().log("Destroying SimpleCORSFilter");
	}

}
