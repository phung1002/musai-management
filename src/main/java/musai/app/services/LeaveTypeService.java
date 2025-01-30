package musai.app.services;

import java.util.List;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveTypeRequestDTO;
import musai.app.DTO.response.LeaveTypeResponseDTO;

public interface LeaveTypeService {
	
	MessageResponse createAddLeaveType(LeaveTypeRequestDTO leaveType);

	MessageResponse updateLeaveType(Long id, LeaveTypeRequestDTO leaveTypeDTO);

	MessageResponse deleteLeaveType(Long id);
	
	List<LeaveTypeResponseDTO> getAllLeaveTypes(); 
	
    LeaveTypeResponseDTO getLeaveTypeDetail(Long id);

    List<LeaveTypeResponseDTO> searchLeaveType(String keyword); 
	
}