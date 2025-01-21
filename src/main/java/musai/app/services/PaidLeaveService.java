package musai.app.services;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.PaidLeaveDTO;

public interface PaidLeaveService {
	
	MessageResponse createAddPaidLeave(PaidLeaveDTO paidLeave);

	MessageResponse updatePaidLeave(Long id, PaidLeaveDTO paidLeaveDTO);

	MessageResponse deletePaidLeave(Long id);

}