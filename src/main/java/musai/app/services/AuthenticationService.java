package musai.app.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import musai.app.DTO.JwtResponse;
import musai.app.DTO.LoginRequest;
import musai.app.DTO.response.UserResponseDTO;

public interface AuthenticationService {
	
	JwtResponse login(LoginRequest loginRequest, HttpServletResponse response);
	
	void logout(HttpServletResponse response);

	JwtResponse validateUser(HttpServletRequest request);
	
	UserResponseDTO getProfile(HttpServletRequest request);

}
