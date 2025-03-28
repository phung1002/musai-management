package musai.app.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import musai.app.DTO.request.LoginRequest;
import musai.app.DTO.response.UserResponseDTO;

public interface AuthenticationService {
	
	void login(LoginRequest loginRequest, HttpServletResponse response);
	
	void logout(HttpServletResponse response);

	void validateUser(HttpServletRequest request);
	
	UserResponseDTO getProfile(HttpServletRequest request);

}
