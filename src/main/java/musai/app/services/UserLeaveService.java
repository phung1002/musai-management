package musai.app.services;

import java.util.List;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.UserLeaveRequestDTO;
import musai.app.DTO.response.UserLeaveResponseDTO;
import musai.app.DTO.response.UserLeaveResponseDTO2;
import musai.app.models.UserLeave;
import musai.app.security.services.UserDetailsImpl;

public interface UserLeaveService {
	
	List<UserLeaveResponseDTO> getUserLeaveForMember(Long leaveTypeId, UserDetailsImpl principal);
	
	MessageResponse updateUsedDays(Long id, double usedDay);
	
	UserLeave createUserLeave(UserLeaveRequestDTO userLeaveRequestDTO);
	
	UserLeave editUserLeave(UserLeaveRequestDTO userLeaveRequestDTO);
	
	List<UserLeaveResponseDTO2> getAllUserLeaves();

	List<UserLeaveResponseDTO2> searchUserLeaves(String keyword);


}