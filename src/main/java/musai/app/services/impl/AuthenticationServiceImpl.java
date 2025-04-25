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
import musai.app.DTO.response.EmployeeResponseDTO;
import musai.app.exception.BadRequestException;
import musai.app.models.Employee;
import musai.app.repositories.EmployeeRepository;
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
	private EmployeeRepository employeeRepository;

	/**
	 * Authenticate employee and generate JWT token.
	 *
	 * @param loginRequest The login request containing employee id and password.
	 * @param response     The HTTP response to set the cookie.
	 */
	@Override
	public void login(LoginRequest loginRequest, HttpServletResponse response) {
		// Authenticate employee credentials
		Authentication authentication = authenticateUser(loginRequest);

		// Generate JWT token
		String jwt = jwtUtils.generateJwtToken(authentication);

		// Get employee details from authentication
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		validateUserAccount(userDetails);

		// Set JWT cookie manually
		setJwtCookie(response, jwt);

	}

	/**
	 * Logout employee by removing JWT token from cookies.
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
	 * Validate the current employee based on the JWT token in the cookies.
	 *
	 * @param request The HTTP request containing the cookies.
	 */
	@Override
	public void validateEmployee(HttpServletRequest request) {
		String jwt = jwtUtils.getJwtFromCookies(request);

		if (jwt == null) {
		    throw new BadRequestException("auth_required_login_message");
		}
		if (!jwtUtils.isTokenExpired(jwt)) {
		    throw new BadRequestException("auth_required_login_message");
		}
		if (!jwtUtils.validateJwtToken(jwt)) {
		    throw new BadRequestException("Invalid token.");
		}

	}

	/**
	 * Authenticate employee credentials using AuthenticationManager.
	 *
	 * @param loginRequest The login request.
	 * @return Authentication object.
	 */
	private Authentication authenticateUser(LoginRequest loginRequest) {
		try {
			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmployeeId(), loginRequest.getPassword()));
		} catch (Exception ex) {
			throw new BadRequestException("Invalid employee id or password.");
		}
	}

	/**
	 * Validate employee account status.
	 *
	 * @param userDetails The user details.
	 */
	private void validateUserAccount(UserDetailsImpl userDetails) {
		if (userDetails.getDeletedAt() != null) {
			throw new BadRequestException("Invalid employee id or password.");
		}
	}

	/**
	 * Get profile of employee logging in
	 * 
	 * @param request
	 * @return
	 */
	public EmployeeResponseDTO getProfile(HttpServletRequest request) {
		// Get JWT from cookies
		String jwt = jwtUtils.getJwtFromCookies(request);

		if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
			throw new BadRequestException("Invalid or expired token.");
		}
		String employeeId = jwtUtils.getUserNameFromJwtToken(jwt);

		Employee employee = employeeRepository.findByEmployeeId(employeeId)
				.orElseThrow(() -> new UsernameNotFoundException("employee_not_exist"));

		EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO();
		employeeResponseDTO.setId(employee.getId());
		employeeResponseDTO.setEmployeeId(employee.getEmployeeId());
		employeeResponseDTO.setFullName(employee.getFullName());
		employeeResponseDTO.setFullNameFurigana(employee.getFullNameFurigana());
		employeeResponseDTO.setEmail(employee.getEmail());
		employeeResponseDTO.setGender(employee.getGender());
		employeeResponseDTO.setJoinDate(employee.getJoinDate());
		employeeResponseDTO.setBirthday(employee.getBirthday());
		employeeResponseDTO.setDepartment(employee.getDepartment());
		employeeResponseDTO.setWorkPlace(employee.getWorkPlace());
		employeeResponseDTO
				.setRoles(employee.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()));

		return employeeResponseDTO;
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
