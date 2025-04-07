package musai.app.services;

import java.util.List;

import musai.app.DTO.request.EmployeeLeaveRequestDTO;
import musai.app.DTO.response.EmployeeLeaveResponseDTO;
import musai.app.models.EmployeeLeave;

public interface EmployeeLeaveService {

	List<EmployeeLeaveResponseDTO> getEmployeeLeavesForMember(Long leaveTypeId, Long employeeId);

	void updateUsedDaysRemainedDays(Long id, double usedDay);

	EmployeeLeave createEmployeeLeave(EmployeeLeaveRequestDTO employeeLeaveRequestDTO);

	EmployeeLeave updateEmployeeLeave(Long id, EmployeeLeaveRequestDTO employeeLeaveRequestDTO);

	List<EmployeeLeaveResponseDTO> getEmployeeLeaves(String keyword);

	List<EmployeeLeaveResponseDTO> getEmployeeLeavesForMember(Long employeeId);

}