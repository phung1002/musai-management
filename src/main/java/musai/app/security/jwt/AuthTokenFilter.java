package musai.app.security.jwt;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import musai.app.security.services.UserDetailsServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter class for JWT authentication. It intercepts requests and validates the
 * JWT token to set the authentication in the security context.
 */
public class AuthTokenFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	/**
	 * Method to filter incoming HTTP requests and set authentication based on the
	 * JWT token.
	 *
	 * @param request     The incoming HTTP request.
	 * @param response    The HTTP response.
	 * @param filterChain The filter chain to continue request processing.
	 * @throws ServletException If a servlet-related error occurs.
	 * @throws IOException      If an I/O error occurs.
	 */
	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		try {
			// Extract JWT token from cookies
			String jwt = jwtUtils.getJwtFromCookies(request);

			// Validate the token and process it if valid
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				// Extract username from the token
				String username = jwtUtils.getUserNameFromJwtToken(jwt);

				// Load user details using the username
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				// Create an authentication object using user details and authorities
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, // No credentials required
						userDetails.getAuthorities());

				// Attach authentication details from the request
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// Set the authentication object in the security context
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}else if (jwt != null) {
                logger.warn("Invalid JWT token: {}", jwt);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"invalid_token\"");
                return;
            }
		} catch (Exception e) {
			// Log any exceptions during the authentication process
			logger.error("Cannot set user authentication: {}", e);
		}

		// Continue processing the request
		filterChain.doFilter(request, response);
	}
}
