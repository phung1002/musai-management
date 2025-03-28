package musai.app.services.impl;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import musai.app.DTO.request.LoginRequest;
import musai.app.DTO.response.UserResponseDTO;
import musai.app.exception.BadRequestException;
import musai.app.models.User;
import musai.app.repositories.UserRepository;
import musai.app.security.jwt.JwtUtils;
import musai.app.security.services.UserDetailsImpl;
import musai.app.services.AuthenticationService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserRepository userRepository;

	/**
	 * Authenticate user and generate JWT token.
	 *
	 * @param loginRequest The login request containing username and password.
	 * @param response     The HTTP response to set the cookie.
	 */
	@Override
	public void login(LoginRequest loginRequest, HttpServletResponse response) {
		// Authenticate user credentials
		Authentication authentication = authenticateUser(loginRequest);

		// Generate JWT token
		String jwt = jwtUtils.generateJwtToken(authentication);

		// Get user details from authentication
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		validateUserAccount(userDetails);

		// Set JWT cookie manually
		setJwtCookie(response, jwt);

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
	 */
	@Override
	public void validateUser(HttpServletRequest request) {
		String jwt = jwtUtils.getJwtFromCookies(request);

		if (jwt == null) {
		    throw new BadRequestException("Token not found.");
		}
		if (!jwtUtils.isTokenExpired(jwt)) {
		    throw new BadRequestException("Token has expired.");
		}
		if (!jwtUtils.validateJwtToken(jwt)) {
		    throw new BadRequestException("Invalid token.");
		}

	}

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
	 * Get profile of user logging in
	 * 
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
		userResponseDTO.setId(user.getId());
		userResponseDTO.setUsername(user.getUsername());
		userResponseDTO.setFullName(user.getFullName());
		userResponseDTO.setFullNameFurigana(user.getFullNameFurigana());
		userResponseDTO.setEmail(user.getEmail());
		userResponseDTO.setGender(user.getGender());
		userResponseDTO.setJoinDate(user.getJoinDate());
		userResponseDTO.setBirthday(user.getBirthday());
		userResponseDTO.setDepartment(user.getDepartment());
		userResponseDTO.setWorkPlace(user.getWorkPlace());
		userResponseDTO
				.setRoles(user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()));

		return userResponseDTO;
	}

	private void setJwtCookie(HttpServletResponse response, String jwt) {
		Cookie cookie = new Cookie("access_token", jwt);
		cookie.setHttpOnly(true); // Prevent JavaScript access
		cookie.setPath("/"); // Available throughout the application
		cookie.setMaxAge(jwtUtils.getJwtExpirationMs() / 1000); // Match JWT expiration time

		// Add cookie to response
		response.addCookie(cookie);

		// Add SameSite attribute to Set-Cookie header
		response.setHeader("Set-Cookie", "access_token=" + jwt + "; HttpOnly; Path=/; Max-Age="
				+ (jwtUtils.getJwtExpirationMs() / 1000) + "; SameSite=Lax");

		// Set Secure to run in https
//		response.setHeader("Set-Cookie", "access_token=" + jwt + "; HttpOnly; Secure; Path=/; Max-Age="
//				+ (jwtUtils.getJwtExpirationMs() / 1000) + "; SameSite=Lax");
	}
}
