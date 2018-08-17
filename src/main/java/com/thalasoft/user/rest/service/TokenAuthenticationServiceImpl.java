package com.thalasoft.user.rest.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import com.thalasoft.user.rest.properties.JwtProperties;
import com.thalasoft.user.rest.security.AuthoritiesConstants;
import com.thalasoft.user.rest.utils.CommonConstants;

import java.sql.Date;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
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
    private JwtProperties jwtProperties;

	@Autowired
	private UserDetailsService userDetailsService;

	public void addTokenToResponseHeader(HttpHeaders headers, String username) {
		String token = buildAccessToken(username);
		headers.add(CommonConstants.AUTH_HEADER_NAME, token);
	}
	
	public void addTokenToResponseHeader(HttpServletResponse response, Authentication authentication) {
		String username = authentication.getName();
		if (username != null) {
			String token = buildAccessToken(username);
			response.addHeader(CommonConstants.AUTH_HEADER_NAME, token);
		}
	}

	private String buildAccessToken(String username) {
		return CommonConstants.AUTH_BEARER + " " + buildAccessTokenValue(username);
	}
	
	private String buildAccessTokenValue(String username) {
		String token = null;
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (userDetails != null) {
			Date expirationDate = new Date(System.currentTimeMillis() + ONE_WEEK);
			token = Jwts.builder().signWith(SignatureAlgorithm.HS512, getEncodedPrivateKey()).setExpiration(expirationDate).setSubject(userDetails.getUsername()).compact();
		}
		return token;
	}
	
	public String buildRefreshToken(String username) {
		String token = null;
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (userDetails != null) {
			DateTime currentTime = new DateTime();
			Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
			claims.put("scopes", Arrays.asList(AuthoritiesConstants.ROLE_ADMIN.getRole()));
			token = Jwts.builder()
			// If calling the setClaims method then call it before all other setters
			.setClaims(claims)
			.setIssuer(jwtProperties.getTokenIssuer())
			.setId(UUID.randomUUID().toString())
			.setIssuedAt(currentTime.toDate())
			.setExpiration(currentTime.plusMinutes(jwtProperties.getRefreshTokenExpirationTime()).toDate())
			.signWith(SignatureAlgorithm.HS512, jwtProperties.getTokenPrivateKey())
			.compact();
		}
        return token;
	}

	public Authentication authenticate(HttpServletRequest request) {
		String token = extractAuthTokenFromRequest(request);
        logger.debug("The request contained the authentication token: " + token);
		if (token != null) {
			if (!token.isEmpty()) {
				try {
					String subject = Jwts.parser().setSigningKey(getEncodedPrivateKey()).parseClaimsJws(token).getBody().getSubject();
					if (subject != null) {
						UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						logger.debug("Security - The filter authenticated fine from the JWT token");
						return authentication;
					} else {
						throw new BadCredentialsException("The authentication token " + token + " did not contain a subject.");
					}
				} catch (SignatureException e) {
					throw new BadCredentialsException("The authentication token " + token + " could not be parsed.");
				}
			} else {
				throw new BadCredentialsException("The authentication token was empty.");
			}
		} else {
			throw new BadCredentialsException("The authentication token was missing.");
		}
	}

	private String extractAuthTokenFromRequest(HttpServletRequest request) {
	    String token = null;
        String header = request.getHeader(CommonConstants.AUTH_HEADER_NAME);
        if (header != null && header.contains(CommonConstants.AUTH_BEARER)) {
			int start = (CommonConstants.AUTH_BEARER + " ").length();
            if (header.length() > start) {
                token = header.substring(start);
            }
        } else {
            // The token may be set as an HTTP parameter in case the client could not set it as an HTTP header
			token = request.getParameter(TOKEN_URL_PARAM_NAME);
		}
		return token;
	}

	private String getEncodedPrivateKey() {
		String privateKey = jwtProperties.getTokenPrivateKey();
		return Base64.getEncoder().encodeToString(privateKey.getBytes(UTF_8));
	}
	
}
