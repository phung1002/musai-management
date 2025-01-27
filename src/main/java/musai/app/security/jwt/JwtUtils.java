package musai.app.security.jwt;

import java.security.Key;
import java.util.Date;

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

	// Secret key for signing JWT, configured in application properties
	@Value("${musai.app.jwtSecret}")
	private String jwtSecret;

	// Expiration time for JWT in milliseconds, configured in application properties
	@Value("${musai.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	/**
	 * Returns the expiration time for JWT tokens.
	 * 
	 * @return jwtExpirationMs Expiration time in milliseconds
	 */
	public int getJwtExpirationMs() {
		return jwtExpirationMs;
	}

	/**
	 * Generates a JWT token for the authenticated user.
	 * 
	 * @param authentication The Authentication object containing user details
	 * @return The generated JWT token as a String
	 */
	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder().setSubject(userPrincipal.getUsername()) // Set the username as the subject
				.setIssuedAt(new Date()) // Set the token issuance time
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Set expiration time
				.signWith(key(), SignatureAlgorithm.HS256) // Sign the token using HMAC-SHA256
				.compact(); // Compact the token into a string
	}

	/**
	 * Constructs the signing key for JWT tokens using the configured secret key.
	 * 
	 * @return A Key object used for signing JWT tokens
	 */
	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	/**
	 * Extracts the username from the given JWT token.
	 * 
	 * @param token The JWT token
	 * @return The username extracted from the token
	 */
	public String getUserNameFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
	}

	/**
	 * Retrieves the JWT token from cookies in the HTTP request.
	 * 
	 * @param request The HttpServletRequest object
	 * @return The JWT token if found in cookies, or null if not found
	 */
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

	/**
	 * Validates the given JWT token.
	 * 
	 * @param authToken The JWT token to validate
	 * @return True if the token is valid, false otherwise
	 */
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
}
