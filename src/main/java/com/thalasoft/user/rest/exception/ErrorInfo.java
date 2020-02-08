package com.thalasoft.user.rest.exception;

import org.springframework.http.HttpStatus;

public class ErrorInfo {

  private String url;
  private HttpStatus httpStatus;
  private String message;
  private String developerMessage;
  private String errorCode;

  public ErrorInfo(String url, HttpStatus httpStatus, String message) {
    this.url = url;
    this.httpStatus = httpStatus;
    this.message = message;
    this.developerMessage = "";
    this.errorCode = "";
  }

  public ErrorInfo(String url, HttpStatus httpStatus, String errorCode, String message, String developerMessage) {
    this.url = url;
    this.httpStatus = httpStatus;
    this.message = message;
    this.developerMessage = developerMessage;
    this.errorCode = errorCode;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDeveloperMessage() {
    return developerMessage;
  }

  public void setDeveloperMessage(String developerMessage) {
    this.developerMessage = developerMessage;
  }

}