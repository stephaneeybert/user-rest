package com.thalasoft.user.rest.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.servlet.http.HttpServletResponse;

import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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
	public HttpEntity<ResourceSupport> root(final PagedResourcesAssembler<User> pagedResourcesAssembler, final UriComponentsBuilder builder, final HttpServletResponse response) {
		Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "lastname"), new Sort.Order(Sort.Direction.ASC, "firstname"));
		Pageable pageable = new PageRequest(1, 10, sort);
		Link search = linkTo(methodOn(UserController.class).search("searchTerm", pageable, pagedResourcesAssembler, builder)).withRel("users search");
		
		final StringBuilder links = new StringBuilder();
		links.append(search);
        response.addHeader("Link", links.toString());

        ResourceSupport resource = new ResourceSupport();
        resource.add(search);
        
        return new ResponseEntity<ResourceSupport>(resource, HttpStatus.OK);
	}

}
