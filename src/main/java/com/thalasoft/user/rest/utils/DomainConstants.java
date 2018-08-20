package com.thalasoft.user.rest.utils;

public final class DomainConstants {

    public static final String AUTH = "auth";
    public static final String USERS = "users";
    public static final String ADMINS = "admins";
    public static final String ERRORS = "errors";
    public static final String ADDRESSES = "addresses";
    public static final String PASSWORD = "password";
    public static final String CONFIRM_EMAIL = "confirm-email";
    public static final String EMAIL_CONFIRMATION_MAIL = "email-confirmation-mail";
    public static final String LOGIN = "login";
    public static final String ROLES = "roles";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String TOKEN_REFRESH = "token-refresh";

    public static final String SERVLET_NAME = "UserREST";

    private DomainConstants() {
        throw new AssertionError();
    }
    
}

