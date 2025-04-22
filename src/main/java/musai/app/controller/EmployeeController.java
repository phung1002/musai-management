package musai.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import musai.app.DTO.request.ChangePasswordRequestDTO;
import musai.app.DTO.request.EmployeeRequestDTO;
import musai.app.DTO.response.MessageResponse;
import musai.app.DTO.response.EmployeeResponseDTO;
import musai.app.security.services.UserDetailsImpl;
import musai.app.services.EmployeeService;
import musai.app.validation.ValidationGroups;

@RestController
@RequestMapping("/api/employees")
@AllArgsConstructor
public class EmployeeController {
	private final EmployeeService employeeService;

	// API get all
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<?> list(@RequestParam String keyword) {
		List<EmployeeResponseDTO> response = employeeService.getEmployees(keyword);
		return ResponseEntity.ok(response);
	}

	// API get all members
	@PreAuthorize("hasRole('MANAGER')")
	@GetMapping("/list-member")
	public ResponseEntity<?> listMembers(@RequestParam String keyword) {
		List<EmployeeResponseDTO> response = employeeService.getMembers(keyword);
		return ResponseEntity.ok(response);
	}
	
	// API create
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> create(
			@Validated(ValidationGroups.CreateEmployee.class) @RequestBody EmployeeRequestDTO employeeRequestDTO) {
		MessageResponse response = employeeService.createEmployee(employeeRequestDTO);
		return ResponseEntity.ok(response);
	}

	// API update
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id,
			@Validated @RequestBody EmployeeRequestDTO employeeRequestDTO,
			@AuthenticationPrincipal UserDetailsImpl principal) {
		MessageResponse response = employeeService.updateEmployee(id, employeeRequestDTO, principal);
		return ResponseEntity.ok(response);
	}

	// API delete
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl principal) {
		MessageResponse response = employeeService.deleteEmployee(id, principal);
		return ResponseEntity.ok(response);
	}

	// API Change password
	@PutMapping("/change-password")
	public ResponseEntity<?> changePassword(@Validated @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO,
			@AuthenticationPrincipal UserDetailsImpl principal) {
		MessageResponse response = employeeService.changePassword(changePasswordRequestDTO, principal);
		return ResponseEntity.ok(response);
	}
}
