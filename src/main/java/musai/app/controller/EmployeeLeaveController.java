package musai.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import musai.app.DTO.request.EmployeeLeaveRequestDTO;
import musai.app.DTO.response.MessageResponse;
import musai.app.DTO.response.EmployeeLeaveResponseDTO;
import musai.app.security.services.UserDetailsImpl;
import musai.app.services.EmployeeLeaveService;

@RestController
@RequestMapping("/api/employee-leaves")
@AllArgsConstructor
public class EmployeeLeaveController {
	@Autowired
	private EmployeeLeaveService employeeLeaveService;

	// API List All
	@PreAuthorize("hasRole('MANAGER')")
	@GetMapping("/all")
	public ResponseEntity<?> listAll(@RequestParam String keyword) {
		List<EmployeeLeaveResponseDTO> employeeLeaves = employeeLeaveService.getEmployeeLeaves(keyword);
		return ResponseEntity.ok(employeeLeaves);
	}
	
	// API List all for member
	@PreAuthorize("hasRole('MEMBER')")
	@GetMapping
	public ResponseEntity<?> getEmployeeLeavesForMember(@AuthenticationPrincipal UserDetailsImpl principal) {
		List<EmployeeLeaveResponseDTO> response = employeeLeaveService.getEmployeeLeavesForMember(principal.getId());
		return ResponseEntity.ok(response);
	}

	// API create
	@PreAuthorize("hasRole('MANAGER')")
	@PostMapping
	public ResponseEntity<?> create(@RequestBody EmployeeLeaveRequestDTO request) {
		employeeLeaveService.createEmployeeLeave(request);
		return ResponseEntity.ok(new MessageResponse("Add employee leaves successfully"));
	}

	// API update
	@PreAuthorize("hasRole('MANAGER')")
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody EmployeeLeaveRequestDTO request) {
		employeeLeaveService.updateEmployeeLeave(id, request);
		return ResponseEntity.ok(new MessageResponse("Update employee leaves successfully"));
	}

}