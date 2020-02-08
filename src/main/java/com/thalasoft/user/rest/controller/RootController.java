package com.thalasoft.user.rest.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import javax.servlet.http.HttpServletResponse;

import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.resource.RootModel;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(RESTConstants.SLASH)
public class RootController {

  @GetMapping
  @ResponseBody
  public ResponseEntity<RootModel> root(final PagedResourcesAssembler<User> pagedResourcesAssembler,
      final UriComponentsBuilder builder, final HttpServletResponse response) {
    Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "lastname"),
        new Sort.Order(Sort.Direction.ASC, "firstname"));
    Pageable pageable = PageRequest.of(1, 10, sort);
    Link search = linkTo(
        methodOn(UserController.class).search("searchTerm", pageable, sort, pagedResourcesAssembler, builder))
            .withRel("users search");
    final StringBuilder links = new StringBuilder();
    links.append(search);
    response.addHeader("Link", links.toString());
    RootModel model = new RootModel();
    model.add(search);
    return ResponseEntity.ok(model);
  }

}
