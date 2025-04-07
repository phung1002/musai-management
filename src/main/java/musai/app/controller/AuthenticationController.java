package musai.app.controller;

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
import musai.app.DTO.request.LoginRequest;
import musai.app.DTO.response.MessageResponse;
import musai.app.DTO.response.EmployeeResponseDTO;
import musai.app.security.jwt.JwtUtils;
import musai.app.services.AuthenticationService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;
    
	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@Validated @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
		try {
			authenticationService.login(loginRequest, response);
            return ResponseEntity.ok("login_success");
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new MessageResponse("invalid_employee_id_or_password."));
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		authenticationService.logout(response);
        return ResponseEntity.ok(new MessageResponse("logout_success"));
	}

	@GetMapping("/validate")
    public ResponseEntity<?> validate(HttpServletRequest request) {
		authenticationService.validateEmployee(request);
        return ResponseEntity.ok("validate_success");
    }

	@GetMapping("/profile")
    public ResponseEntity<?> profile(HttpServletRequest request) {
		EmployeeResponseDTO response = authenticationService.getProfile(request);
        return ResponseEntity.ok(response);
    }
}
