package com.thalasoft.user.rest.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import com.thalasoft.user.data.exception.EntityNotFoundException;
import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.assembler.UserModelAssembler;
import com.thalasoft.user.rest.resource.UserModel;
import com.thalasoft.user.rest.service.CredentialsService;
import com.thalasoft.user.rest.service.ModelService;
import com.thalasoft.user.rest.service.UserActionService;
import com.thalasoft.user.rest.utils.RESTConstants;
import com.thalasoft.user.rest.utils.CommonUtils;
import com.thalasoft.user.rest.utils.DomainConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
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
  private ModelService modelService;

  @Autowired
  private UserService userService;

  @Autowired
  private UserActionService userActionService;

  @Autowired
  private CredentialsService credentialsService;

  @Autowired
  private UserModelAssembler userModelAssembler;

  private static Logger logger = LoggerFactory.getLogger(UserController.class);

  private static final Set<String> nonSortableColumns = new HashSet<String>(Arrays.asList("id", "confirmedEmail"));

  @GetMapping(value = RESTConstants.SLASH + "{id}")
  @ResponseBody
  public ResponseEntity<UserModel> findById(@PathVariable Long id, UriComponentsBuilder builder) {
    try {
      User user = userService.findById(id);
      UserModel userModel = userModelAssembler.toModel(user);
      URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
          .buildAndExpand(user.getId()).toUri();
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.setLocation(location);
      return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(userModel);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  @ResponseBody
  public ResponseEntity<UserModel> add(@Valid @RequestBody UserModel userModel, UriComponentsBuilder builder) {
    User user = userService.add(modelService.toUser(userModel));
    UserModel createdUserModel = null;
    userActionService.sendEmailConfirmationMail(user);
    URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
        .buildAndExpand(user.getId()).toUri();
    createdUserModel = userModelAssembler.toModel(user);
    return ResponseEntity.created(location).body(createdUserModel);
  }

  @PutMapping(value = RESTConstants.SLASH + "{id}")
  @ResponseBody
  public ResponseEntity<UserModel> update(@PathVariable Long id, @Valid @RequestBody UserModel userModel,
      UriComponentsBuilder builder) {
    User user = userService.update(id, modelService.toUser(userModel));
    UserModel updatedUserModel = userModelAssembler.toModel(user);
    URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
        .buildAndExpand(user.getId()).toUri();
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(location);
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(updatedUserModel);
  }

  @PatchMapping(value = RESTConstants.SLASH + "{id}")
  @ResponseBody
  public ResponseEntity<UserModel> partialUpdate(@PathVariable Long id, @Valid @RequestBody UserModel userModel,
      UriComponentsBuilder builder) {
    User user = userService.partialUpdate(id, modelService.toUser(userModel));
    UserModel updatedUserModel = userModelAssembler.toModel(user);
    URI location = builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
        .buildAndExpand(user.getId()).toUri();
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(location);
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(updatedUserModel);
  }

  @DeleteMapping(value = RESTConstants.SLASH + "{id}")
  @ResponseBody
  public ResponseEntity<UserModel> delete(@PathVariable Long id) {
    User user = userService.delete(id);
    UserModel userModel = userModelAssembler.toModel(user);
    return ResponseEntity.ok(userModel);
  }

  @GetMapping(value = DomainConstants.STREAM_ALL)
  @ResponseBody
  public ResponseEntity<PagedModel<UserModel>> streamAll(
      @PageableDefault(sort = { "lastname", "firstname" }, direction = Sort.Direction.ASC) Pageable pageable, Sort sort,
      PagedResourcesAssembler<User> pagedResourcesAssembler, UriComponentsBuilder builder) {
    sort = CommonUtils.stripColumnsFromSorting(sort, nonSortableColumns);
    userService.addSortToPageable(pageable, sort);
    Page<User> foundUsers = getPage(userService.streamAll(pageable), pageable, sort);
    PagedModel<UserModel> userPagedModels = pagedResourcesAssembler.toModel(foundUsers, userModelAssembler);
    UriComponentsBuilder uriComponentsBuilder = builder.path(RESTConstants.SLASH + DomainConstants.USERS);
    modelService.addPageableToUri(uriComponentsBuilder, pageable);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(uriComponentsBuilder.buildAndExpand().toUri());
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(userPagedModels);
  }

  private Page<User> getPage(List<User> users, Pageable pageable, Sort sort) {
    int start = (int) pageable.getOffset();
    int end = (int) ((start + pageable.getPageSize()) > users.size() ? users.size() : (start + pageable.getPageSize()));
    Page<User> page = new PageImpl<User>(users.subList(start, end), pageable, users.size());
    return page;
  }

  @GetMapping
  @ResponseBody
  public ResponseEntity<PagedModel<UserModel>> all(
      @PageableDefault(sort = { "lastname", "firstname" }, direction = Sort.Direction.ASC) Pageable pageable, Sort sort,
      PagedResourcesAssembler<User> pagedResourcesAssembler, UriComponentsBuilder builder) {
    sort = CommonUtils.stripColumnsFromSorting(sort, nonSortableColumns);
    userService.addSortToPageable(pageable, sort);
    Page<User> foundUsers = userService.all(pageable);
    PagedModel<UserModel> userPagedModels = pagedResourcesAssembler.toModel(foundUsers, userModelAssembler);
    UriComponentsBuilder uriComponentsBuilder = builder.path(RESTConstants.SLASH + DomainConstants.USERS);
    modelService.addPageableToUri(uriComponentsBuilder, pageable);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(uriComponentsBuilder.buildAndExpand().toUri());
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(userPagedModels);
  }

  @GetMapping(params = "searchTerm")
  @ResponseBody
  public ResponseEntity<PagedModel<UserModel>> search(@RequestParam(value = "searchTerm") String searchTerm,
      @PageableDefault(sort = { "lastname", "firstname" }, direction = Sort.Direction.ASC) Pageable pageable, Sort sort,
      PagedResourcesAssembler<User> pagedResourcesAssembler, UriComponentsBuilder builder) {
    sort = CommonUtils.stripColumnsFromSorting(sort, nonSortableColumns);
    userService.addSortToPageable(pageable, sort);
    Page<User> foundUsers = userService.search(searchTerm, pageable);
    // TODO https://jira.spring.io/browse/DATAREST-1117
    Link selfLink = linkTo(
        methodOn(UserController.class).search(searchTerm, pageable, sort, pagedResourcesAssembler, builder)).withSelfRel();
    PagedModel<UserModel> userPagedModels = pagedResourcesAssembler.toModel(foundUsers, userModelAssembler, selfLink);
    UriComponentsBuilder uriComponentsBuilder = builder.path(RESTConstants.SLASH + DomainConstants.USERS);
    uriComponentsBuilder.queryParam("searchTerm", searchTerm);
    modelService.addPageableToUri(uriComponentsBuilder, pageable);
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setLocation(uriComponentsBuilder.buildAndExpand().toUri());
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(userPagedModels);
  }

  @PutMapping(value = RESTConstants.SLASH + "{id}" + RESTConstants.SLASH + DomainConstants.PASSWORD)
  @ResponseBody
  public ResponseEntity<UserModel> updatePassword(@PathVariable Long id, @RequestBody String password,
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
    UserModel updatedUserModel = userModelAssembler.toModel(updatedUser);
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(updatedUserModel);
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
  public ResponseEntity<UserModel> confirmEmail(@PathVariable Long id,
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
    UserModel userModel = userModelAssembler.toModel(user);
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(userModel);
  }

  @GetMapping(params = "email")
  @ResponseBody
  public ResponseEntity<UserModel> findByEmail(@RequestParam(value = "email") String email,
      UriComponentsBuilder builder) {
    HttpHeaders responseHeaders = new HttpHeaders();
    try {
      User user = userService.findByEmail(email);
      UserModel userModel = userModelAssembler.toModel(user);
      responseHeaders
          .setLocation(builder.path(RESTConstants.SLASH + DomainConstants.USERS + RESTConstants.SLASH + "{id}")
              .buildAndExpand(user.getId()).toUri());
      return ResponseEntity.status(HttpStatus.OK).headers(responseHeaders).body(userModel);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

}
