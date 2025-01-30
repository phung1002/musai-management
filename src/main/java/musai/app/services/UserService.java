package musai.app.services;

import java.util.List;
import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.UserRequestDTO;
import musai.app.DTO.response.UserResponseDTO;

public interface UserService {
	List<UserResponseDTO> getAllUsers();
	
	MessageResponse addUser(UserRequestDTO userRequestDTO);

	MessageResponse editUser(Long userId, UserRequestDTO userRequestDTO);
	
	MessageResponse deleteUser(Long userId);
}
