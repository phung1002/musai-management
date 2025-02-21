package musai.app.services;

import java.util.List;
import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.UserRequestDTO;
import musai.app.DTO.response.UserResponseDTO;
import musai.app.security.services.UserDetailsImpl;

public interface UserService {
	List<UserResponseDTO> getAllUsers();
	
	MessageResponse addUser(UserRequestDTO userRequestDTO);

	MessageResponse editUser(Long userId, UserRequestDTO userRequestDTO, UserDetailsImpl principal);
	
	MessageResponse deleteUser(Long userId, UserDetailsImpl principal);
	
	UserResponseDTO detailUser(Long userId);

	List<UserResponseDTO> searchUser(String keyword);

}
