package com.thalasoft.user.rest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    
    private static final String PREFIX_OUTPUT = "\n";
    private static final String PREFIX_REQUEST = "|>";
    private static final String PREFIX_RESPONSE = "|<";
    
    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
        MediaType.valueOf("text/*"),
        MediaType.APPLICATION_FORM_URLENCODED,
        MediaType.APPLICATION_JSON,
        MediaType.APPLICATION_XML,
        MediaType.valueOf("application/*+json"),
        MediaType.valueOf("application/*+xml"),
        MediaType.MULTIPART_FORM_DATA
        );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
        }
    }

    protected void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            beforeRequest(request, response);
            filterChain.doFilter(request, response);
        } finally {
            afterRequest(request, response);
            // The response body needs to be copied back to the response as the wrapper clears the response body when it caches it
            response.copyBodyToResponse();
        }
    }

    protected void beforeRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        if (log.isInfoEnabled() || (response.getStatus() == HttpStatus.BAD_REQUEST.value() && log.isWarnEnabled())) {
            logRequestHeader(request, PREFIX_OUTPUT + request.getRemoteAddr() + PREFIX_REQUEST);
        }
    }

    protected void afterRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        if (log.isInfoEnabled() || (response.getStatus() == HttpStatus.BAD_REQUEST.value() && log.isWarnEnabled())) {
            logRequestHeader(request, PREFIX_OUTPUT + request.getRemoteAddr() + PREFIX_REQUEST);
            logRequestBody(request, PREFIX_OUTPUT + request.getRemoteAddr() + PREFIX_REQUEST);
            logResponse(response, PREFIX_OUTPUT + request.getRemoteAddr() + PREFIX_RESPONSE);
        }
    }

    private static void logRequestHeader(ContentCachingRequestWrapper request, String prefix) {
        String queryString = request.getQueryString();
        if (queryString == null) {
            log.info("{} {} {}", prefix, request.getMethod(), request.getRequestURI());
        } else {
            log.info("{} {} {}?{}", prefix, request.getMethod(), request.getRequestURI(), queryString);
        }
        StringBuffer headersStr = new StringBuffer();
        Collections.list(request.getHeaderNames())
            .forEach(headerName -> Collections.list(request.getHeaders(headerName))
                .forEach(headerValue -> headersStr.append(PREFIX_OUTPUT + headerName + " : " + headerValue)));
        log.info("{}: {}", prefix, headersStr.toString());
    }

    private static void logRequestBody(ContentCachingRequestWrapper request, String prefix) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, request.getContentType(), request.getCharacterEncoding(), prefix);
        }
    }

    private static void logResponse(ContentCachingResponseWrapper response, String prefix) {
        int status = response.getStatus();
        log.info("{} {} {}", prefix, status, HttpStatus.valueOf(status).getReasonPhrase());
        response.getHeaderNames().forEach(headerName -> response.getHeaders(headerName)
                .forEach(headerValue -> log.info("{} {}: {}", prefix, headerName, headerValue)));
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, response.getContentType(), response.getCharacterEncoding(), prefix);
        }
    }

    private static void logContent(byte[] content, String contentType, String contentEncoding, String prefix) {
        MediaType mediaType = MediaType.valueOf(contentType);
        boolean visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        if (visible) {
            try {
                String contentString = new String(content, contentEncoding);
                log.info("{} {}", prefix, contentString);
            } catch (UnsupportedEncodingException e) {
                log.info("{} [{} bytes content]", prefix, content.length);
            }
        } else {
            log.info("{} [{} bytes content]", prefix, content.length);
        }
    }

    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }
}
