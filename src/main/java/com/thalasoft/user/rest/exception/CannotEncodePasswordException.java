package com.thalasoft.user.rest.exception;

import com.thalasoft.user.data.exception.EnrichableException;

@SuppressWarnings("serial")
public class CannotEncodePasswordException extends EnrichableException {

  public CannotEncodePasswordException() {
    super("The password could not be encoded.");
  }

  public CannotEncodePasswordException(String message) {
    super(message);
  }

}