package com.thalasoft.user.rest.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.assembler.UserResourceAssembler;
import com.thalasoft.user.rest.resource.UserResource;
import com.thalasoft.user.rest.security.service.CredentialsService;
import com.thalasoft.user.rest.service.ResourceService;
import com.thalasoft.user.rest.service.UserActionService;
import com.thalasoft.user.rest.utils.RESTConstants;
import com.thalasoft.user.rest.utils.CommonUtils;
import com.thalasoft.user.rest.utils.DomainConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(RESTConstants.SLASH + DomainConstants.USERS)
public class UserController {

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

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    private static final Set<String> nonSortableColumns = new HashSet<String>(Arrays.asList("id", "confirmedEmail"));
  
    @GetMapping(value = RESTConstants.SLASH + "{id}")
    @ResponseBody
    public ResponseEntity<UserResource> findById(@PathVariable Long id, UriComponentsBuilder builder) {
        try {
            User user = userService.findById(id);
            UserResource userResource = userResourceAssembler.toResource(user);
            URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
                    .buildAndExpand(user.getId()).toUri();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setLocation(location);
            return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(userResource);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<UserResource> add(@Valid @RequestBody UserResource userResource,
            UriComponentsBuilder builder) {
        User user = userService.add(resourceService.toUser(userResource));
        UserResource createdUserResource = null;
        userActionService.sendEmailConfirmationMail(user);
        URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
                .buildAndExpand(user.getId()).toUri();
        createdUserResource = userResourceAssembler.toResource(user);
        return ResponseEntity.created(location).body(createdUserResource);
    }

    @PutMapping(value = RESTConstants.SLASH + "{id}")
    @ResponseBody
    public ResponseEntity<UserResource> update(@PathVariable Long id, @Valid @RequestBody UserResource userResource,
            UriComponentsBuilder builder) {
        User user = userService.update(id, resourceService.toUser(userResource));
        UserResource updatedUserResource = userResourceAssembler.toResource(user);
        URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
                .buildAndExpand(user.getId()).toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(updatedUserResource);
    }

    @PatchMapping(value = RESTConstants.SLASH + "{id}")
    @ResponseBody
    public ResponseEntity<UserResource> partialUpdate(@PathVariable Long id,
            @Valid @RequestBody UserResource userResource, UriComponentsBuilder builder) {
        User user = userService.partialUpdate(id, resourceService.toUser(userResource));
        UserResource updatedUserResource = userResourceAssembler.toResource(user);
        URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
                .buildAndExpand(user.getId()).toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(updatedUserResource);
    }

    @DeleteMapping(value = RESTConstants.SLASH + "{id}")
    @ResponseBody
    public ResponseEntity<UserResource> delete(@PathVariable Long id) {
        User user = userService.delete(id);
        UserResource userResource = userResourceAssembler.toResource(user);
        return ResponseEntity.ok(userResource);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<PagedResources<UserResource>> all(@PageableDefault(sort = { "lastname", "firstname" }, direction = Sort.Direction.ASC) Pageable pageable, Sort sort,
            PagedResourcesAssembler<User> pagedResourcesAssembler, UriComponentsBuilder builder) {
        sort = CommonUtils.stripColumnsFromSorting(sort, nonSortableColumns);
        userService.addSortToPageable(pageable, sort);
        Page<User> foundUsers = userService.all(pageable);
        PagedResources<UserResource> userPagedResources = pagedResourcesAssembler.toResource(foundUsers,
        userResourceAssembler);
        UriComponentsBuilder uriComponentsBuilder = builder.path(RESTConstants.SLASH + DomainConstants.USERS);
        resourceService.addPageableToUri(uriComponentsBuilder, pageable);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(uriComponentsBuilder.buildAndExpand().toUri());
        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(userPagedResources);
    }

    @GetMapping(params = "searchTerm")
    @ResponseBody
    public ResponseEntity<PagedResources<UserResource>> search(@RequestParam(value = "searchTerm") String searchTerm,
    @PageableDefault(sort = { "lastname", "firstname" }, direction = Sort.Direction.ASC) Pageable pageable, Sort sort, PagedResourcesAssembler<User> pagedResourcesAssembler, UriComponentsBuilder builder) {
      sort = CommonUtils.stripColumnsFromSorting(sort, nonSortableColumns);
      userService.addSortToPageable(pageable, sort);
        Page<User> foundUsers = userService.search(searchTerm, pageable);
        // TODO https://jira.spring.io/browse/DATAREST-1117
        Link selfLink = linkTo(methodOn(UserController.class).search(searchTerm, pageable, sort, pagedResourcesAssembler, builder)).withSelfRel();
        PagedResources<UserResource> userPagedResources = pagedResourcesAssembler.toResource(foundUsers,
                userResourceAssembler, selfLink);
        UriComponentsBuilder uriComponentsBuilder = builder.path(RESTConstants.SLASH + DomainConstants.USERS);
        uriComponentsBuilder.queryParam("searchTerm", searchTerm);
        resourceService.addPageableToUri(uriComponentsBuilder, pageable);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(uriComponentsBuilder.buildAndExpand().toUri());
        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(userPagedResources);
    }

    @PutMapping(value = RESTConstants.SLASH + "{id}" + RESTConstants.SLASH + DomainConstants.PASSWORD)
    @ResponseBody
    public ResponseEntity<UserResource> updatePassword(@PathVariable Long id, @RequestBody String password,
            UriComponentsBuilder builder) {
        User updatedUser;
        try {
            updatedUser = credentialsService.updatePassword(id, password);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
                .buildAndExpand(updatedUser.getId()).toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        UserResource updatedUserResource = userResourceAssembler.toResource(updatedUser);
        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(updatedUserResource);
    }

    @GetMapping(value = RESTConstants.SLASH + "{id}" + RESTConstants.SLASH + DomainConstants.EMAIL_CONFIRMATION_MAIL)
    @ResponseBody
    public ResponseEntity<Void> requestEmailConfirmationMail(@PathVariable Long id, UriComponentsBuilder builder) {
        try {
            User user = userService.findById(id);
            userActionService.sendEmailConfirmationMail(user);
            URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
                    .buildAndExpand(user.getId()).toUri();
            return ResponseEntity.ok().location(location).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = RESTConstants.SLASH + "{id}" + RESTConstants.SLASH + DomainConstants.CONFIRM_EMAIL)
    @ResponseBody
    public ResponseEntity<UserResource> confirmEmail(@PathVariable Long id,
            @RequestParam(value = "sialToken") String sialToken, UriComponentsBuilder builder) {
        User user;
        try {
            user = userActionService.confirmEmail(sialToken, id);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
                .buildAndExpand(user.getId()).toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        UserResource userResource = userResourceAssembler.toResource(user);
        return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(userResource);
    }

    @GetMapping(params = "email")
    @ResponseBody
    public ResponseEntity<UserResource> findByEmail(@RequestParam(value = "email") String email,
            UriComponentsBuilder builder) {
        HttpHeaders responseHeaders = new HttpHeaders();
        try {
            User user = userService.findByEmail(email);
            UserResource userResource = userResourceAssembler.toResource(user);
            responseHeaders.setLocation(
                    builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
                            .buildAndExpand(user.getId()).toUri());
            return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(userResource);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
