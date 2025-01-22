package musai.app.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import musai.app.DTO.JwtResponse;
import musai.app.DTO.LoginRequest;
import musai.app.security.jwt.JwtUtils;
import musai.app.security.services.UserDetailsImpl;
import musai.app.security.services.UserDetailsServiceImpl;
import musai.app.services.AuthenticationService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	public JwtResponse login(LoginRequest loginRequest, HttpServletResponse response) throws BadCredentialsException {
		// Xử lý xác thực người dùng
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		// Tạo JWT token
		String jwt = jwtUtils.generateJwtToken(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		
		// Check if the user is deleted
		if (userDetails.getDeletedAt() != null) {
			throw new BadCredentialsException("User not exist.");
		}

		// Get roles of user
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		
		// Set JWT token in HttpOnly cookie
		Cookie cookie = new Cookie("access_token", jwt);
		cookie.setHttpOnly(false);  // Prevent JavaScript access					->DEV ENVIROMENT :false, change to true when it done
		cookie.setSecure(false);		// Ensure it is sent only over HTTPS		->DEV ENVIROMENT :false, change to true when it done
		cookie.setPath("/");	   // Cookie will be available throughout the app
		cookie.setMaxAge(60 * 60); // Set expiry time for 1 hour

		// Add SameSite to cookie
		String sameSite = "Strict"; 
		String cookieHeader = String.format("access_token=%s; HttpOnly; Secure; Path=/; Max-Age=%d; SameSite=%s",
				jwt, 60 * 60, sameSite);
		
		// Add header Set-Cookie to response
		response.addHeader("Set-Cookie", cookieHeader);

		// Gửi cookie cho client
		response.addCookie(cookie);
		
		// Return JwtResponse
		return new JwtResponse(
				userDetails.getId(),
				userDetails.getUsername(),
				userDetails.getEmail(),
				roles,
				true
				);
	}

	public void logout(HttpServletResponse response) {
		// Delete cookie
		Cookie cookie = new Cookie("access_token", null);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	@Override
	public JwtResponse validateUser(HttpServletRequest request) {
		// Lấy JWT từ cookie
		String jwt = jwtUtils.getJwtFromCookies(request);
		if (jwt == null || !jwtUtils.validateJwtToken(jwt)) {
			throw new BadCredentialsException("Invalid or expired token.");
		}

		// Lấy thông tin người dùng từ JWT
		String username = jwtUtils.getUserNameFromJwtToken(jwt);
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		// Ép kiểu về UserDetailsImpl (hoặc lớp cụ thể của bạn)
		if (!(userDetails instanceof UserDetailsImpl)) {
			throw new IllegalArgumentException("User details implementation mismatch.");
		}
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;

		// Lấy danh sách vai trò từ đối tượng userDetails
		List<String> roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		// Trả về thông tin người dùng
		return new JwtResponse(
				userDetailsImpl.getId(),	  // Lấy id từ lớp UserDetailsImpl
				userDetailsImpl.getUsername(),
				userDetailsImpl.getEmail(),
				roles,
				true
		);
	}
}

