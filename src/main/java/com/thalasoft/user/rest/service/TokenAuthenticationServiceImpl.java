package com.thalasoft.user.rest.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thalasoft.user.data.jpa.domain.User;
import com.thalasoft.user.data.service.UserService;
import com.thalasoft.user.rest.properties.JwtProperties;
import com.thalasoft.user.rest.utils.CommonConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Service
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

	private static Logger logger = LoggerFactory.getLogger(TokenAuthenticationServiceImpl.class);

	private static final String ACCESS_TOKEN_URL_PARAM_NAME = "access-token";
	
    @Autowired
    private JwtProperties jwtProperties;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserService userService;

	@Override
	public void addAccessTokenToResponseHeader(HttpHeaders headers, String username) {
		String token = buildAccessToken(username);
		headers.add(CommonConstants.ACCESS_TOKEN_HEADER_NAME, token);
	}
	
	@Override
	public void addRefreshTokenToResponseHeader(HttpHeaders headers, String username, String clientId) {
		String token = buildRefreshToken(username, clientId);
		headers.add(CommonConstants.REFRESH_TOKEN_HEADER_NAME, token);
	}
	
	@Override
	public void addAccessTokenToResponseHeader(HttpServletResponse response, Authentication authentication) {
		String username = authentication.getName();
		if (username != null) {
			String token = buildAccessToken(username);
			response.addHeader(CommonConstants.ACCESS_TOKEN_HEADER_NAME, token);
		}
	}

	@Override
	public void addRefreshTokenToResponseHeader(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		String username = authentication.getName();
		if (username != null) {
			String clientId = extractClientIdFromRequest(request);
			String token = buildRefreshToken(username, clientId);
			response.addHeader(CommonConstants.REFRESH_TOKEN_HEADER_NAME, token);
		}
	}

	private String buildAccessToken(String username) {
		return CommonConstants.AUTH_BEARER_HEADER + " " + buildAccessTokenValue(username);
	}
	
	private String buildAccessTokenValue(String username) {
		String token = null;
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (userDetails != null) {
			LocalDateTime currentTime = LocalDateTime.now();
			Date expirationDate = Date.from(currentTime
			.plusMinutes(jwtProperties.getAccessTokenExpirationTime())
			.atZone(ZoneId.systemDefault()).toInstant());
			// Date expirationDate = new Date(System.currentTimeMillis() + ONE_WEEK);
			Claims claims = Jwts.claims();
			User user = userService.findByEmail(userDetails.getUsername());
			claims.put("fullname", user.getFirstname() + " " + user.getLastname());
			claims.put("email", user.getEmail().getEmailAddress());
			claims.put("scopes", userDetails.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
			token = Jwts.builder()
			// If calling the setClaims method then call it before all other setters
			.setClaims(claims)
			.setSubject(userDetails.getUsername())
			.setIssuer(jwtProperties.getTokenIssuer())
			.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
			.setExpiration(expirationDate)
			.signWith(SignatureAlgorithm.HS512, getEncodedPrivateKey())
			.compact();
		}
		return token;
	}
	
	private String buildRefreshToken(String username, String clientId) {
		return CommonConstants.AUTH_BEARER_HEADER + " " + buildRefreshTokenValue(username, clientId);
	}
	
	public String buildRefreshTokenValue(String username, String clientId) {
		String token = null;
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (userDetails != null) {
			LocalDateTime currentTime = LocalDateTime.now();
			Date expirationDate = Date.from(currentTime
			.plusMinutes(jwtProperties.getRefreshTokenExpirationTime())
			.atZone(ZoneId.systemDefault()).toInstant());
			Claims claims = Jwts.claims();
			User user = userService.findByEmail(userDetails.getUsername());
			claims.put("email", user.getEmail().getEmailAddress());
			token = Jwts.builder()
			// If calling the setClaims method then call it before all other setters
			.setClaims(claims)
			.setSubject(clientId)
			.setId(UUID.randomUUID().toString())
			.setIssuer(jwtProperties.getTokenIssuer())
			.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
			.setExpiration(expirationDate)
			.signWith(SignatureAlgorithm.HS512, getEncodedPrivateKey())
			.compact();
		}
        return token;
	}

	public Authentication authenticate(HttpServletRequest request) {
		String token = extractAccessTokenFromRequest(request);
        logger.debug("The request should contain an authentication token: " + token);
		if (token != null) {
			if (!token.isEmpty()) {
				try {
					String subject = getSubjectFromToken(token);
					if (subject != null) {
						UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						logger.debug("Security - The request authenticated fine from the JWT Access token");
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

	public Authentication authenticateFromRefreshToken(HttpServletRequest request) {
		String token = extractRefreshTokenFromRequest(request);
		if (token != null) {
			if (!token.isEmpty()) {
				try {
					String jti = getJtiFromToken(token);
					if (jti != null) {
						String subject = getSubjectFromToken(token);
						if (subject != null) {
							Claims claims = getClaimsFromToken(token);
							String email = (String) claims.get("email");
							UserDetails userDetails = userDetailsService.loadUserByUsername(email);
							UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
							authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
							logger.debug("Security - The request authenticated fine from the JWT Refresh token");
							return authentication;
						} else {
							throw new BadCredentialsException("The refresh token " + token + " did not contain a subject.");
						}
					} else {
						throw new BadCredentialsException("The refresh token " + token + " did not contain a JTI.");
					}
				} catch (SignatureException e) {
					throw new BadCredentialsException("The refresh token " + token + " could not be parsed.");
				}
			} else {
				throw new BadCredentialsException("The refresh token was empty.");
			}
		} else {
			throw new BadCredentialsException("The refresh token was missing from the request.");
		}
	}

	private String getSubjectFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		if (null != claims) {
			return claims.getSubject();
		} else {
			return null;
		}
	}

	private String getJtiFromToken(String token) {
		Claims claims = getClaimsFromToken(token);
		if (null != claims) {
			return claims.getId();
		} else {
			return null;
		}
	}

	private Claims getClaimsFromToken(String token) {
		try {
			return Jwts.parser()
			.setAllowedClockSkewSeconds(jwtProperties.getAllowedClockSkewSeconds())
			.setSigningKey(getEncodedPrivateKey())
			.parseClaimsJws(token)
			.getBody();
		} catch (ExpiredJwtException e) {
			throw new BadCredentialsException("The token " + token + " expired.");
		}
	}

	private String extractAccessTokenFromRequest(HttpServletRequest request) {
	    String token = null;
        String header = request.getHeader(CommonConstants.ACCESS_TOKEN_HEADER_NAME);
        if (header != null && header.contains(CommonConstants.AUTH_BEARER_HEADER)) {
			int start = (CommonConstants.AUTH_BEARER_HEADER + " ").length();
            if (header.length() > start) {
                token = header.substring(start);
            }
        } else {
            // The token may be set as an HTTP parameter in case the client could not set it as an HTTP header
			token = request.getParameter(ACCESS_TOKEN_URL_PARAM_NAME);
		}
		return token;
	}

	private String extractClientIdFromRequest(HttpServletRequest request) {
        return request.getHeader(CommonConstants.CLIENT_ID_HEADER_NAME);
	}

	private String extractRefreshTokenFromRequest(HttpServletRequest request) {
	    String token = null;
        String header = request.getHeader(CommonConstants.REFRESH_TOKEN_HEADER_NAME);
        if (header != null && header.contains(CommonConstants.AUTH_BEARER_HEADER)) {
			int start = (CommonConstants.AUTH_BEARER_HEADER + " ").length();
            if (header.length() > start) {
                token = header.substring(start);
            }
		}
		return token;
	}

	private String getEncodedPrivateKey() {
		String privateKey = jwtProperties.getTokenPrivateKey();
		return Base64.getEncoder().encodeToString(privateKey.getBytes(UTF_8));
	}
	
}
