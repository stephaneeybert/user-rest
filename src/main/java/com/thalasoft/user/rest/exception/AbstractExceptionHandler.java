package com.thalasoft.user.rest.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.MessageSource;

public class AbstractExceptionHandler {

  @Autowired
  private MessageSource messageSource;

  protected String localizeErrorMessage(String errorCode, Object args[]) {
    Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(errorCode, args, locale);
  }

  protected String localizeErrorMessage(String errorCode) {
    return localizeErrorMessage(errorCode, null);
  }

  protected String getStackTrace(Exception e) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    return stringWriter.toString();
  }

  protected String getFullRequestUrl(HttpServletRequest request) {
    return (request.getQueryString() != null) ? String.join("", request.getRequestURL(), "?", request.getQueryString())
        : request.getRequestURL().toString();
  }

}
