package musai.app.services;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveApplicationRequestDTO;

public interface LeaveApplicationService {
	MessageResponse apply(LeaveApplicationRequestDTO request);
}
