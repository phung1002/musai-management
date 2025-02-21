package musai.app.services;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.UserLeaveRequestDTO;
import musai.app.DTO.response.UserLeaveResponseDTO;
import musai.app.DTO.response.UserResponseDTO;
import musai.app.models.UserLeave;
import java.util.List;

public interface UserLeaveService {
	// Create new user leave
	UserLeave createUserLeave(UserLeaveRequestDTO userLeaveRequestDTO);
	
	UserLeave editUserLeave(UserLeaveRequestDTO userLeaveRequestDTO);
	
	List<UserLeaveResponseDTO> getAllUserLeaves();

}