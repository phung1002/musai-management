package musai.app.services;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.UserLeaveRequestDTO;
import musai.app.DTO.response.UserLeaveResponseDTO;
import musai.app.models.UserLeave;
import musai.app.security.services.UserDetailsImpl;

import java.util.List;

public interface UserLeaveService {
	
	List<UserLeaveResponseDTO> getUserLeaveForMember(Long leaveTypeId, UserDetailsImpl principal);
	
	MessageResponse updateUsedDays(Long id, int usedDay);
	
	// Create new user leave
	UserLeave createUserLeave(UserLeaveRequestDTO userLeaveRequestDTO);
}