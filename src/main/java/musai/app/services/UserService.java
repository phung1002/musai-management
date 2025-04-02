package musai.app.services;

import java.util.List;

import musai.app.DTO.request.ChangePasswordRequestDTO;
import musai.app.DTO.request.UserRequestDTO;
import musai.app.DTO.response.MessageResponse;
import musai.app.DTO.response.UserResponseDTO;
import musai.app.security.services.UserDetailsImpl;

public interface UserService {
	List<UserResponseDTO> getAllUsers(String keyword);

	MessageResponse addUser(UserRequestDTO userRequestDTO);

	MessageResponse editUser(Long userId, UserRequestDTO userRequestDTO, UserDetailsImpl principal);

	MessageResponse deleteUser(Long userId, UserDetailsImpl principal);

	MessageResponse changePassword(ChangePasswordRequestDTO changePasswordRequestDTO, UserDetailsImpl principal);

}
