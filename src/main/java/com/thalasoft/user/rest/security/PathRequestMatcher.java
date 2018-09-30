package com.thalasoft.user.rest.security;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class PathRequestMatcher implements RequestMatcher {

    private OrRequestMatcher orRequestMatcher;
    private RequestMatcher requestMatcher;

    public PathRequestMatcher(List<String> pathsToSkip, String pathToConsider) {
        List<RequestMatcher> toSkipRequestMatchers = pathsToSkip.stream().map(path -> new AntPathRequestMatcher(path)).collect(Collectors.toList());
        orRequestMatcher = new OrRequestMatcher(toSkipRequestMatchers);
        requestMatcher = new AntPathRequestMatcher(pathToConsider);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        if (orRequestMatcher.matches(request)) {
            return false;
        }
        return requestMatcher.matches(request) ? true : false;
    }
}
