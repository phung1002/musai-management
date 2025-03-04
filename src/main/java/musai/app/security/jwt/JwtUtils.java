package musai.app.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import musai.app.security.services.UserDetailsImpl;

/**
 * Utility class for handling JWT-related operations, such as token generation,
 * validation, and extracting information from tokens.
 */
@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${musai.app.jwtSecret}")
	private String jwtSecret;

	@Value("${musai.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public int getJwtExpirationMs() {
		return jwtExpirationMs;
	}

	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		
		String roles = userPrincipal.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.joining(","));
		
		  return Jwts.builder().setSubject(userPrincipal.getUsername())
	                .claim("roles", roles)
	                .setIssuedAt(new Date())
	                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
	                .signWith(key(), SignatureAlgorithm.HS256)
	                .compact();
	}

	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
	}

	public String getJwtFromCookies(HttpServletRequest request) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("access_token".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	// Validates the given JWT token.
	public boolean validateJwtToken(String authToken) {
		try {
			// Parse and validate the token
			Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}
	
	 public Cookie generateJwtCookie(String jwt) {
	        Cookie cookie = new Cookie("access_token", jwt);
	        cookie.setHttpOnly(true);
	        cookie.setSecure(true);
	        cookie.setPath("/");
	        cookie.setMaxAge(jwtExpirationMs / 1000);
	        return cookie;
	    }
}
