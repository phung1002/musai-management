package musai.app.services.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import musai.app.DTO.JwtResponse;
import musai.app.DTO.LoginRequest;
import musai.app.DTO.response.UserResponseDTO;
import musai.app.exception.BadRequestException;
import musai.app.models.User;
import musai.app.repositories.UserRepository;
import musai.app.security.jwt.JwtUtils;
import musai.app.security.services.UserDetailsImpl;
import musai.app.security.services.UserDetailsServiceImpl;
import musai.app.services.AuthenticationService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * Authenticate user and generate JWT token.
	 *
	 * @param loginRequest The login request containing username and password.
	 * @param response     The HTTP response to set the cookie.
	 * @return JwtResponse containing user information and token details.
	 */
	@Override
	public JwtResponse login(LoginRequest loginRequest, HttpServletResponse response) {
		// Authenticate user credentials
		Authentication authentication = authenticateUser(loginRequest);

		// Generate JWT token
		String jwt = jwtUtils.generateJwtToken(authentication);

		// Get user details from authentication
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		validateUserAccount(userDetails);

		// Set JWT token as an HttpOnly cookie
		setJwtCookie(response, jwt);

		// Build and return JWT response
		return buildJwtResponse(userDetails, jwt);
	}

	/**
	 * Logout user by removing JWT token from cookies.
	 *
	 * @param response The HTTP response to remove the cookie.
	 */
	@Override
	public void logout(HttpServletResponse response) {
		Cookie cookie = new Cookie("access_token", null);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(0); // Expire the cookie immediately
		response.addCookie(cookie);
	}

	/**
	 * Validate the current user based on the JWT token in the cookies.
	 *
	 * @param request The HTTP request containing the cookies.
	 * @return JwtResponse containing user information if validation is successful.
	 */
	@Override
	public JwtResponse validateUser(HttpServletRequest request) {
		String jwt = jwtUtils.getJwtFromCookies(request);

		if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
			throw new BadRequestException("Invalid or expired token.");
		}

		String username = jwtUtils.getUserNameFromJwtToken(jwt);
		UserDetailsImpl userDetails = loadUserDetails(username);

		return buildJwtResponse(userDetails, jwt);
	}

	// Helper Methods

	/**
	 * Authenticate user credentials using AuthenticationManager.
	 *
	 * @param loginRequest The login request.
	 * @return Authentication object.
	 */
	private Authentication authenticateUser(LoginRequest loginRequest) {
		try {
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		} catch (Exception ex) {
			throw new BadRequestException("Invalid username or password.");
		}
	}

	/**
	 * Validate user account status.
	 *
	 * @param userDetails The user details.
	 */
	private void validateUserAccount(UserDetailsImpl userDetails) {
		if (userDetails.getDeletedAt() != null) {
			throw new BadRequestException("Invalid username or password.");
		}
	}

	/**
	 * Set JWT token as an HttpOnly cookie in the response.
	 *
	 * @param response The HTTP response.
	 * @param jwt      The JWT token.
	 */
	private void setJwtCookie(HttpServletResponse response, String jwt) {
		Cookie cookie = new Cookie("access_token", jwt);
		cookie.setHttpOnly(true); // Prevent JavaScript access
		cookie.setSecure(true); // Ensure cookie is sent only over HTTPS
		cookie.setPath("/"); // Available throughout the application
		cookie.setMaxAge(jwtUtils.getJwtExpirationMs() / 1000); // Match JWT expiration time

		// Add cookie to response
		response.addCookie(cookie);

		// Add SameSite attribute to Set-Cookie header
		response.setHeader("Set-Cookie", "access_token=" + jwt + "; HttpOnly; Secure; Path=/; Max-Age="
				+ (jwtUtils.getJwtExpirationMs() / 1000) + "; SameSite=Lax");
	}

	/**
	 * Load user details by username.
	 *
	 * @param username The username.
	 * @return UserDetailsImpl object.
	 */
	private UserDetailsImpl loadUserDetails(String username) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (!(userDetails instanceof UserDetailsImpl)) {
			throw new IllegalArgumentException("User details implementation mismatch.");
		}
		return (UserDetailsImpl) userDetails;
	}

	/**
	 * Build a JwtResponse object.
	 *
	 * @param userDetails The user details.
	 * @param jwt         The JWT token.
	 * @return JwtResponse object.
	 */
	private JwtResponse buildJwtResponse(UserDetailsImpl userDetails, String jwt) {
		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		return new JwtResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles, userDetails.getFullName());
	}
	
	/**
	 * Get profile of user logging in
	 * @param request
	 * @return
	 */
	public UserResponseDTO getProfile(HttpServletRequest request) {
	    // Get JWT from cookies
	    String jwt = jwtUtils.getJwtFromCookies(request);

	    if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
	        throw new BadRequestException("Invalid or expired token.");
	    }
	    String username = jwtUtils.getUserNameFromJwtToken(jwt);

	    User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

	    UserResponseDTO userResponseDTO = new UserResponseDTO();
	    userResponseDTO.setUsername(user.getUsername());
	    userResponseDTO.setFullName(user.getFullName());
	    userResponseDTO.setFullNameFurigana(user.getFullNameFurigana());
	    userResponseDTO.setEmail(user.getEmail());
	    userResponseDTO.setGender(user.getGender());
	    userResponseDTO.setJoinDate(user.getJoinDate());
	    userResponseDTO.setBirthday(user.getBirthday());
	    userResponseDTO.setDepartment(user.getDepartment());
	    userResponseDTO.setWorkPlace(user.getWorkPlace());
	    userResponseDTO.setRoles(user.getRoles().stream().map(role -> role.getName().name())
				.collect(Collectors.toSet()));

	    return userResponseDTO;
	}

}
