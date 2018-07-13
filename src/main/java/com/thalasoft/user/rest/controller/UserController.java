package com.thalasoft.user.rest.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.validation.Valid;

import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.assembler.UserResourceAssembler;
import com.thalasoft.user.rest.resource.UserResource;
import com.thalasoft.user.rest.security.TokenAuthenticationService;
import com.thalasoft.user.rest.service.CredentialsService;
import com.thalasoft.user.rest.service.ResourceService;
import com.thalasoft.user.rest.service.UserActionService;
import com.thalasoft.user.rest.service.resource.CredentialsResource;
import com.thalasoft.user.rest.utils.RESTConstants;
import com.thalasoft.user.rest.utils.UserDomainConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(RESTConstants.SLASH + UserDomainConstants.USERS)
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserActionService userActionService;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private UserResourceAssembler userResourceAssembler;

    @RequestMapping(value = RESTConstants.SLASH
            + "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResource> findById(@PathVariable Long id, UriComponentsBuilder builder) {
        HttpHeaders responseHeaders = new HttpHeaders();
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<UserResource>(responseHeaders, HttpStatus.NOT_FOUND);
        } else {
            UserResource userResource = userResourceAssembler.toResource(user);
            responseHeaders.setLocation(
                    builder.path(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH + "{id}")
                            .buildAndExpand(user.getId()).toUri());
            ResponseEntity<UserResource> responseEntity = new ResponseEntity<UserResource>(userResource,
                    responseHeaders, HttpStatus.OK);
            return responseEntity;
        }
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResource> add(@Valid @RequestBody UserResource userResource,
            UriComponentsBuilder builder) {
        User user = userService.add(resourceService.toUser(userResource));
        HttpHeaders responseHeaders = new HttpHeaders();
        UserResource createdUserResource = null;
        if (user != null) {
            userActionService.sendEmailConfirmationMail(user);
            responseHeaders.setLocation(
                    builder.path(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH + "{id}")
                            .buildAndExpand(user.getId()).toUri());
            createdUserResource = userResourceAssembler.toResource(user);
        }
        ResponseEntity<UserResource> responseEntity = new ResponseEntity<UserResource>(createdUserResource,
                responseHeaders, HttpStatus.CREATED);
        return responseEntity;
    }

    @RequestMapping(value = RESTConstants.SLASH
            + "{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResource> update(@PathVariable Long id, @Valid @RequestBody UserResource userResource,
            UriComponentsBuilder builder) {
        HttpHeaders responseHeaders = new HttpHeaders();
        User user = userService.update(id, resourceService.toUser(userResource));
        responseHeaders.setLocation(
                builder.path(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH + "{id}")
                        .buildAndExpand(user.getId()).toUri());
        UserResource updatedUserResource = userResourceAssembler.toResource(user);
        return new ResponseEntity<UserResource>(updatedUserResource, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = RESTConstants.SLASH
            + "{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResource> delete(@PathVariable Long id) {
        User user = userService.delete(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        UserResource userResource = userResourceAssembler.toResource(user);
        return new ResponseEntity<UserResource>(userResource, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<UserResource>> all(Pageable pageable,
            PagedResourcesAssembler<User> pagedResourcesAssembler, UriComponentsBuilder builder) {
        Page<User> foundUsers = userService.all(pageable);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(builder.path(RESTConstants.SLASH + UserDomainConstants.USERS)
                .queryParam("page", pageable.getPageNumber()).queryParam("size", pageable.getPageSize())
                .buildAndExpand().toUri());
        Link selfLink = linkTo(methodOn(UserController.class).all(pageable, pagedResourcesAssembler, builder))
                .withRel("all");
        PagedResources<UserResource> userPagedResources = pagedResourcesAssembler.toResource(foundUsers,
                userResourceAssembler, selfLink);
        return new ResponseEntity<PagedResources<UserResource>>(userPagedResources, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(params = "searchTerm", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PagedResources<UserResource>> search(@RequestParam(value = "searchTerm") String searchTerm,
            Pageable pageable, PagedResourcesAssembler<User> pagedResourcesAssembler, UriComponentsBuilder builder) {
        Page<User> foundUsers = userService.search(searchTerm, pageable);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(builder.path(RESTConstants.SLASH + UserDomainConstants.USERS)
                .queryParam("searchTerm", searchTerm).queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize()).buildAndExpand().toUri());
        Link selfLink = linkTo(
                methodOn(UserController.class).search(searchTerm, pageable, pagedResourcesAssembler, builder))
                        .withRel("search");
        PagedResources<UserResource> userPagedResources = pagedResourcesAssembler.toResource(foundUsers,
                userResourceAssembler, selfLink);
        return new ResponseEntity<PagedResources<UserResource>>(userPagedResources, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = RESTConstants.SLASH
            + UserDomainConstants.LOGIN, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResource> login(@Valid @RequestBody CredentialsResource credentialsResource,
            UriComponentsBuilder builder) {
        HttpHeaders responseHeaders = new HttpHeaders();
        User user = credentialsService.checkPassword(credentialsResource);
        userService.clearReadablePassword(user);
        if (user == null) {
            return new ResponseEntity<UserResource>(responseHeaders, HttpStatus.NOT_FOUND);
        } else {
            tokenAuthenticationService.addTokenToResponseHeader(responseHeaders, credentialsResource.getEmail());
            responseHeaders.setLocation(
                    builder.path(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH + "{id}")
                            .buildAndExpand(user.getId()).toUri());
            UserResource createdUserResource = userResourceAssembler.toResource(user);
            ResponseEntity<UserResource> responseEntity = new ResponseEntity<UserResource>(createdUserResource,
                    responseHeaders, HttpStatus.CREATED);
            return responseEntity;
        }
    }

    @RequestMapping(value = RESTConstants.SLASH + "{id}" + RESTConstants.SLASH
            + UserDomainConstants.PASSWORD, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResource> updatePassword(@PathVariable Long id, @RequestBody String password,
            UriComponentsBuilder builder) {
        HttpHeaders responseHeaders = new HttpHeaders();
        User updatedUser;
        try {
            updatedUser = credentialsService.updatePassword(id, password);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<UserResource>(responseHeaders, HttpStatus.NOT_FOUND);
        }
        responseHeaders.setLocation(
                builder.path(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH + "{id}")
                        .buildAndExpand(updatedUser.getId()).toUri());
        UserResource updatedUserResource = userResourceAssembler.toResource(updatedUser);
        return new ResponseEntity<UserResource>(updatedUserResource, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = RESTConstants.SLASH + "{id}" + RESTConstants.SLASH
            + UserDomainConstants.EMAIL_CONFIRMATION_MAIL, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> requestEmailConfirmationMail(@PathVariable Long id, UriComponentsBuilder builder) {
        HttpHeaders responseHeaders = new HttpHeaders();
        User user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<Void>(responseHeaders, HttpStatus.NOT_FOUND);
        } else {
            userActionService.sendEmailConfirmationMail(user);
            responseHeaders.setLocation(
                    builder.path(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH + "{id}")
                            .buildAndExpand(user.getId()).toUri());
            return new ResponseEntity<Void>(responseHeaders, HttpStatus.OK);
        }
    }

    @RequestMapping(value = RESTConstants.SLASH + "{id}" + RESTConstants.SLASH
            + UserDomainConstants.CONFIRM_EMAIL, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResource> confirmEmail(@PathVariable Long id,
            @RequestParam(value = "sialToken") String sialToken, UriComponentsBuilder builder) {
        HttpHeaders responseHeaders = new HttpHeaders();
        User user;
        try {
            user = userActionService.confirmEmail(sialToken, id);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<UserResource>(responseHeaders, HttpStatus.NOT_FOUND);
        }
        responseHeaders.setLocation(
                builder.path(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH + "{id}")
                        .buildAndExpand(user.getId()).toUri());
        UserResource userResource = userResourceAssembler.toResource(user);
        return new ResponseEntity<UserResource>(userResource, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(params = "email", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserResource> findByEmail(@RequestParam(value = "email") String email,
            UriComponentsBuilder builder) {
        HttpHeaders responseHeaders = new HttpHeaders();
        User user = userService.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<UserResource>(responseHeaders, HttpStatus.NOT_FOUND);
        } else {
            UserResource userResource = userResourceAssembler.toResource(user);
            responseHeaders.setLocation(
                    builder.path(RESTConstants.SLASH + UserDomainConstants.USERS + RESTConstants.SLASH + "{id}")
                            .buildAndExpand(user.getId()).toUri());
            ResponseEntity<UserResource> responseEntity = new ResponseEntity<UserResource>(userResource,
                    responseHeaders, HttpStatus.OK);
            return responseEntity;
        }
    }

}
