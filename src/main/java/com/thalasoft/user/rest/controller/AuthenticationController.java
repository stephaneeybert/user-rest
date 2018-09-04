package com.thalasoft.user.rest.controller;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.assembler.UserResourceAssembler;
import com.thalasoft.user.rest.resource.UserResource;
import com.thalasoft.user.rest.service.CredentialsService;
import com.thalasoft.user.rest.service.TokenAuthenticationService;
import com.thalasoft.user.rest.service.resource.CredentialsResource;
import com.thalasoft.user.rest.utils.DomainConstants;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(RESTConstants.SLASH + DomainConstants.AUTH)
public class AuthenticationController {

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private UserResourceAssembler userResourceAssembler;

    // TODO Do I need this login if there is already a CustomAuthenticationProvider
    // in use ?
    @PostMapping(value = RESTConstants.SLASH + DomainConstants.LOGIN)
    @ResponseBody
    public ResponseEntity<UserResource> login(@Valid @RequestBody CredentialsResource credentialsResource,
            UriComponentsBuilder builder) {
        HttpHeaders responseHeaders = new HttpHeaders();
        try {
            User user = credentialsService.checkPassword(credentialsResource);
            userService.clearReadablePassword(user);
            tokenAuthenticationService.addAccessTokenToResponseHeader(responseHeaders, credentialsResource.getEmail());
            URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
            .buildAndExpand(user.getId()).toUri();
            UserResource createdUserResource = userResourceAssembler.toResource(user);
            return ResponseEntity.created(location).headers(responseHeaders).body(createdUserResource);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = RESTConstants.SLASH + DomainConstants.TOKEN_REFRESH)
    @ResponseBody
    public ResponseEntity<ResourceSupport> refreshToken(HttpServletRequest request, HttpServletResponse response,
            UriComponentsBuilder builder) throws IOException, ServletException {
        Authentication authentication = tokenAuthenticationService.authenticateFromRefreshToken(request);
        tokenAuthenticationService.addAccessTokenToResponseHeader(response, authentication);
        ResourceSupport resource = new ResourceSupport();
        URI location = builder.path(RESTConstants.SLASH + DomainConstants.TOKEN_REFRESH).buildAndExpand().toUri();
        return ResponseEntity.created(location).body(resource);
        // TODO finish this endpoint
    }

}
