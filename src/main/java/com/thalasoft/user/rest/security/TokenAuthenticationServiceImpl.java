package com.thalasoft.user.rest.security;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

import com.thalasoft.user.rest.properties.ApplicationProperties;
import com.thalasoft.user.rest.utils.CommonConstants;

import java.sql.Date;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

@Service
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

	private static Logger logger = LoggerFactory.getLogger(TokenAuthenticationServiceImpl.class);

	private static final long ONE_WEEK = 1000 * 60 * 60 * 24 * 7;
	private static final String TOKEN_URL_PARAM_NAME = "token";
	
    @Autowired
    private ApplicationProperties applicationProperties;

	@Autowired
	private UserDetailsService userDetailsService;

	public void addTokenToResponseHeader(HttpHeaders headers, String username) {
		String token = buildToken(username);
		headers.add(CommonConstants.AUTH_HEADER_NAME, token);
	}
	
	public void addTokenToResponseHeader(HttpServletResponse response, Authentication authentication) {
		String username = authentication.getName();
		if (username != null) {
			String token = buildToken(username);
			response.addHeader(CommonConstants.AUTH_HEADER_NAME, token);
		}
	}

	private String buildToken(String username) {
		String token = null;
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (userDetails != null) {
			Date expirationDate = new Date(System.currentTimeMillis() + ONE_WEEK);
			token = CommonConstants.AUTH_BEARER + " " + Jwts.builder().signWith(HS256, getEncodedPrivateKey()).setExpiration(expirationDate).setSubject(userDetails.getUsername()).compact();		
		}
		return token;
	}
	
	public Authentication authenticateFromToken(HttpServletRequest request) {
		String token = extractAuthTokenFromRequest(request);
        logger.debug("The request contained the JWT token: " + token);
		if (token != null && !token.isEmpty()) {
			try {
				String username = Jwts.parser().setSigningKey(getEncodedPrivateKey()).parseClaimsJws(token).getBody().getSubject();
				if (username != null) {
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
					logger.debug("Security - The filter authenticated fine from the JWT token");
				}
			} catch (SignatureException e) {
				logger.info("The JWT token " + token + " could not be parsed.");
			}
		}
		return null;
	}

	private String extractAuthTokenFromRequest(HttpServletRequest request) {
	    String token = null;
        String header = request.getHeader(CommonConstants.AUTH_HEADER_NAME);
        if (header != null && header.contains(CommonConstants.AUTH_BEARER)) {
			int start = (CommonConstants.AUTH_BEARER + " ").length();
            if (header.length() > start) {
                token = header.substring(start - 1);
            }
        } else {
            // The token may be set as an HTTP parameter in case the client could not set it as an HTTP header
			token = request.getParameter(TOKEN_URL_PARAM_NAME);
		}
		return token;
	}

	private String getEncodedPrivateKey() {
		String privateKey = applicationProperties.getAuthenticationTokenPrivateKey();
		return Base64.getEncoder().encodeToString(privateKey.getBytes());
	}
	
}
