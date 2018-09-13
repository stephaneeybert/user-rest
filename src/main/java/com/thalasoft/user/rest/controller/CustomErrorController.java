package com.thalasoft.user.rest.controller;

import com.thalasoft.user.rest.exception.CannotEncodePasswordException;
import com.thalasoft.user.rest.utils.DomainConstants;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(RESTConstants.SLASH + DomainConstants.ERROR)
public class CustomErrorController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/";
    }
    
    @GetMapping
    @ResponseBody
    public String error() {
        return "Error handling...";
    }

    @GetMapping(value = "/cannotEncodePassword")
    @ResponseBody
    public void errorCannotEncodePasswordException() {
    	throw new CannotEncodePasswordException();
    }

    @GetMapping(value = "/illegalArgument")
    @ResponseBody
    public void errorIllegalArgumentException() {
    	throw new IllegalArgumentException();
    }

    @GetMapping(value = "/httpRequestMethodNotSupported")
    @ResponseBody
    public void errorHttpRequestMethodNotSupportedException() throws HttpRequestMethodNotSupportedException {
    	throw new HttpRequestMethodNotSupportedException("");
    }

    @GetMapping(value = "/httpBody")
    @ResponseBody
    public void errorHTTPInput() {
    	throw new HttpMessageNotReadableException(null);
    }
    
    @GetMapping(value = "/dao")
    @ResponseBody
    public void errorDAO() {
    	throw new InvalidDataAccessApiUsageException(null);
    }
    
    @GetMapping(value = "/nfe")
    @ResponseBody
    public void errorNFE() {
    	throw new NumberFormatException();
    }

    @GetMapping(value = "/npe")
    @ResponseBody
    public void errorNPE() {
    	throw new NullPointerException();
    }

    @GetMapping(value = "/rte")
    @ResponseBody
    public void errorRTE() {
    	throw new RuntimeException();
    }

}
