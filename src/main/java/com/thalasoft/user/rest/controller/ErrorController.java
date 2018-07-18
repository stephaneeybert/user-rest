package com.thalasoft.user.rest.controller;

import com.thalasoft.user.rest.exception.CannotEncodePasswordException;
import com.thalasoft.user.rest.resource.UserResource;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ExposesResourceFor(UserResource.class)
@RequestMapping(RESTConstants.SLASH + RESTConstants.ERROR)
public class ErrorController {

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void error() {
        int i = 0; // TODO why is this not called on ?
    }

    @RequestMapping(value = "/cannotEncodePassword", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void errorCannotEncodePasswordException() {
    	throw new CannotEncodePasswordException();
    }

    @RequestMapping(value = "/illegalArgument", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void errorIllegalArgumentException() {
    	throw new IllegalArgumentException();
    }

    @RequestMapping(value = "/httpRequestMethodNotSupported", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void errorHttpRequestMethodNotSupportedException() throws HttpRequestMethodNotSupportedException {
    	throw new HttpRequestMethodNotSupportedException("");
    }

    @RequestMapping(value = "/httpBody", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void errorHTTPInput() {
    	throw new HttpMessageNotReadableException(null);
    }
    
    @RequestMapping(value = "/dao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void errorDAO() {
    	throw new InvalidDataAccessApiUsageException(null);
    }
    
    @RequestMapping(value = "/nfe", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void errorNFE() {
    	throw new NumberFormatException();
    }
    
    @RequestMapping(value = "/npe", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void errorNPE() {
    	throw new NullPointerException();
    }
    
    @RequestMapping(value = "/rte", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void errorRTE() {
    	throw new RuntimeException();
    }
    
}
