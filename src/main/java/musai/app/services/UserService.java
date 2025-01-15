package musai.app.services;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.UserDTO;

public interface UserService {
	MessageResponse addUser(UserDTO userDTO);

	MessageResponse editUser(Long userId, UserDTO userDTO);
	
	MessageResponse deleteUser(Long userId);
}
