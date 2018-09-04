package com.thalasoft.user.rest.utils;

public final class CommonConstants {

	public static final String JSESSIONID = "JSESSIONID";
	public static final String ACCESS_TOKEN_HEADER_NAME = "Authorization";
	public static final String REFRESH_TOKEN_HEADER_NAME = "TokenRefresh";
	public static final String CLIENT_ID_HEADER_NAME = "ClientId";
    public static final String AUTH_BEARER_HEADER = "Bearer";
    public static final String AUTH_BASIC = "Basic";
	public static final String EXPORT_FILENAME_HEADER_NAME = "X-Filename";
	
    private CommonConstants() {
        throw new AssertionError();
    }
    
}
