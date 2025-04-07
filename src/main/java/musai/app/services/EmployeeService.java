package musai.app.services;

import java.util.List;

import musai.app.DTO.request.ChangePasswordRequestDTO;
import musai.app.DTO.request.EmployeeRequestDTO;
import musai.app.DTO.response.MessageResponse;
import musai.app.DTO.response.EmployeeResponseDTO;
import musai.app.security.services.UserDetailsImpl;

public interface EmployeeService {
	List<EmployeeResponseDTO> getEmployees(String keyword);

	MessageResponse createEmployee(EmployeeRequestDTO employeeRequestDTO);

	MessageResponse updateEmployee(Long employeeId, EmployeeRequestDTO employeeRequestDTO, UserDetailsImpl principal);

	MessageResponse deleteEmployee(Long employeeId, UserDetailsImpl principal);

	MessageResponse changePassword(ChangePasswordRequestDTO changePasswordRequestDTO, UserDetailsImpl principal);

}
