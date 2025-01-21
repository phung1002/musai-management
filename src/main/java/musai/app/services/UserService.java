package musai.app.services;

import java.util.List;
import musai.app.DTO.MessageResponse;
import musai.app.DTO.UserDTO;

public interface UserService {
	List<UserDTO> getAllUsers();
	
	MessageResponse addUser(UserDTO userDTO);

	MessageResponse editUser(Long userId, UserDTO userDTO);
	
	MessageResponse deleteUser(Long userId);
}
