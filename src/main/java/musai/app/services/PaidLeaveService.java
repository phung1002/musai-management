package musai.app.services;

import java.util.List;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.PaidLeaveRequestDTO;
import musai.app.DTO.response.PaidLeaveResponseDTO;

public interface PaidLeaveService {
	
	MessageResponse createAddPaidLeave(PaidLeaveRequestDTO paidLeave);

	MessageResponse updatePaidLeave(Long id, PaidLeaveRequestDTO paidLeaveDTO);

	MessageResponse deletePaidLeave(Long id);
	
	//List<PaidLeaveRequestDTO> getAllPaidLeaves();

	//PaidLeaveRequestDTO getPaidLeaveDetail(Long id);
	
	//PaidLeaveResponseDTO getPaidLeaveDetail(Long id);
	
	//List<PaidLeaveRequestDTO> searchPaidLeave(String keyword);
	
	List<PaidLeaveResponseDTO> getAllPaidLeaves(); 
	
    PaidLeaveResponseDTO getPaidLeaveDetail(Long id);

    List<PaidLeaveResponseDTO> searchPaidLeave(String keyword); 
	
}