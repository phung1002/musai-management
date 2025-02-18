package musai.app.services;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveApplicationRequestDTO;
import musai.app.security.services.UserDetailsImpl;

public interface LeaveApplicationService {
	MessageResponse applyLeave(LeaveApplicationRequestDTO request);
	MessageResponse respondToLeave(Long id, String status, UserDetailsImpl principal);
	MessageResponse cancelLeave(Long id);
}
