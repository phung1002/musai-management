package musai.app.services;

import java.util.List;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.PaidLeaveDTO;

public interface PaidLeaveService {
	
	MessageResponse createAddPaidLeave(PaidLeaveDTO paidLeave);

	MessageResponse updatePaidLeave(Long id, PaidLeaveDTO paidLeaveDTO);

	MessageResponse deletePaidLeave(Long id);
	
	List<PaidLeaveDTO> getAllPaidLeaves();

}