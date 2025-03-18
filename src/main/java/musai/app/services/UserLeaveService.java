package musai.app.services;

import java.util.List;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.UserLeaveRequestDTO;
import musai.app.DTO.response.UserLeaveResponseDTO;
import musai.app.models.UserLeave;

public interface UserLeaveService {

	List<UserLeaveResponseDTO> getUserLeaveForMember(Long leaveTypeId, Long userId);

	MessageResponse updateUsedDaysRemainedDays(Long id, double usedDay);

	UserLeave createUserLeave(UserLeaveRequestDTO userLeaveRequestDTO);

	UserLeave editUserLeave(UserLeaveRequestDTO userLeaveRequestDTO);

	List<UserLeaveResponseDTO> getAllUserLeaves();

	List<UserLeaveResponseDTO> searchUserLeaves(String keyword);

	List<UserLeaveResponseDTO> getUserLeaveForMember(Long userId);

}