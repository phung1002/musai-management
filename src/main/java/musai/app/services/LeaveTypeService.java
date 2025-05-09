package musai.app.services;

import java.util.List;

import musai.app.DTO.request.LeaveTypeRequestDTO;
import musai.app.DTO.response.LeaveTypeChildrenResponseDTO;
import musai.app.DTO.response.LeaveTypeParentResponseDTO;
import musai.app.DTO.response.MessageResponse;

public interface LeaveTypeService {

	MessageResponse createLeaveType(LeaveTypeRequestDTO leaveType);

	MessageResponse updateLeaveType(Long id, LeaveTypeRequestDTO leaveTypeDTO);

	MessageResponse deleteLeaveType(Long id);

	List<LeaveTypeParentResponseDTO> getAllLeaveTypes(String keyword);

	List<LeaveTypeChildrenResponseDTO> getAllLeaveTypeTree();

}