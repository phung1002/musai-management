package musai.app.services;

import java.util.List;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveTypeRequestDTO;
import musai.app.DTO.response.LeaveTypeChildrenResponseDTO;
import musai.app.DTO.response.LeaveTypeParentResponseDTO;
import musai.app.DTO.response.LeaveTypeResponseDTO;

public interface LeaveTypeService {
	
	MessageResponse createAddLeaveType(LeaveTypeRequestDTO leaveType);

	MessageResponse updateLeaveType(Long id, LeaveTypeRequestDTO leaveTypeDTO);

	MessageResponse deleteLeaveType(Long id);

	List<LeaveTypeResponseDTO> getAllLeaveTypes(); 
	
	List<LeaveTypeChildrenResponseDTO> getAllLeaveTypeTree(); 
	
	LeaveTypeParentResponseDTO getLeaveTypeDetail(Long id);

    List<LeaveTypeChildrenResponseDTO> searchLeaveType(String keyword);
	
}