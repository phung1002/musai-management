package musai.app.services;

import java.util.List;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveApplicationRequestDTO;
import musai.app.DTO.response.LeaveApplicationResponseDTO;
import musai.app.security.services.UserDetailsImpl;

public interface LeaveApplicationService {
	MessageResponse applyLeave(LeaveApplicationRequestDTO request, UserDetailsImpl principal);
	MessageResponse respondToLeave(Long id, String status, UserDetailsImpl principal);
	MessageResponse cancelLeave(Long id);
	MessageResponse updateLeaveApplication(Long id, LeaveApplicationRequestDTO request);
	List<LeaveApplicationResponseDTO> getAllLeaveApplications();
	List<LeaveApplicationResponseDTO> getLeaveApplicationsForMember(UserDetailsImpl principal);
}
