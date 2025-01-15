package musai.app.controller;

import java.util.List;
import java.util.stream.Collectors;
import musai.app.DTO.JwtResponse;
import musai.app.DTO.LoginRequest;
import musai.app.DTO.MessageResponse;
import musai.app.security.jwt.JwtUtils;
import musai.app.security.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest) {
	try {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();	
		
		//Check feild deleted at
		if (userDetails.getDeletedAt() != null) {
			 return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                 .body(new MessageResponse("User not exist."));
	    }
		List<String> roles = userDetails.getAuthorities().stream()
			.map(item -> item.getAuthority())
			.collect(Collectors.toList());
	
		return ResponseEntity.ok(new JwtResponse(jwt, 
							 userDetails.getId(), 
							 userDetails.getUsername(), 
							 userDetails.getEmail(), 
							 roles));
		} catch (BadCredentialsException ex){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new MessageResponse("Invalid username or password."));
		}
	}
}
