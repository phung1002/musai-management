package musai.app.controller;

import musai.app.DTO.JwtResponse;
import musai.app.DTO.LoginRequest;
import musai.app.DTO.MessageResponse;
import musai.app.security.jwt.JwtUtils;
import musai.app.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;
    
	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
		try {
			JwtResponse jwtResponse = authenticationService.login(loginRequest, response);
            return ResponseEntity.ok(jwtResponse);
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new MessageResponse("Invalid username or password."));
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		authenticationService.logout(response);
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
	}

	@GetMapping("/validate")
    public ResponseEntity<?> validateUser(HttpServletRequest request) {
		JwtResponse jwtResponse = authenticationService.validateUser(request);
        return ResponseEntity.ok(jwtResponse);
    }
}
