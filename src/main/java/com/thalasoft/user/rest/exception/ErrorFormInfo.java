package com.thalasoft.user.rest.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

public class ErrorFormInfo {

  private String url;
  private HttpStatus httpStatus;
  private String message;
  private List<ErrorFormField> fieldErrors = new ArrayList<ErrorFormField>();

  public ErrorFormInfo() {
  }

  public ErrorFormInfo(String url, HttpStatus httpStatus, String message) {
    this.url = url;
    this.httpStatus = httpStatus;
    this.message = message;
  }

  public ErrorFormInfo(String url, HttpStatus httpStatus, String message, List<ErrorFormField> fieldErrors) {
    this.url = url;
    this.httpStatus = httpStatus;
    this.message = message;
    this.fieldErrors = fieldErrors;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<ErrorFormField> getFieldErrors() {
    return fieldErrors;
  }

  public void setFieldErrors(List<ErrorFormField> fieldErrors) {
    this.fieldErrors = fieldErrors;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }

}
