package musai.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveApplicationRequestDTO;
import musai.app.DTO.response.LeaveApplicationResponseDTO;
import musai.app.security.services.UserDetailsImpl;
import musai.app.services.LeaveApplicationService;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
public class LeaveApplicationController {
	private final LeaveApplicationService leaveApplicationService;

	public LeaveApplicationController(LeaveApplicationService leaveApplicationService) {
		super();
		this.leaveApplicationService = leaveApplicationService;
	}

	/**
	 * API management get all leave application
	 */
	@PreAuthorize("hasRole('MANAGER')")
	@GetMapping("/leave-applications")
	public ResponseEntity<?> getAllLeaveApplications() {
		List<LeaveApplicationResponseDTO> response = leaveApplicationService.getAllLeaveApplications();
		return ResponseEntity.ok(response);
	}
	
	/**
	 * API member get their leave application
	 */
	@PreAuthorize("hasRole('MEMBER')")
	@GetMapping("/member/leave-applications")
	public ResponseEntity<?> getLeaveApplicationOfMember(@AuthenticationPrincipal UserDetailsImpl principal) {
		List<LeaveApplicationResponseDTO> response = leaveApplicationService.getLeaveApplicationsForMember(principal);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * API member apply leave application
	 */
	@PreAuthorize("hasRole('MEMBER')")
	@PostMapping("/leave-applications")
	public ResponseEntity<?> applyLeave(@Validated @RequestBody LeaveApplicationRequestDTO request, @AuthenticationPrincipal UserDetailsImpl principal) {
		MessageResponse response = leaveApplicationService.applyLeave(request, principal);
		return ResponseEntity.ok(response);
	}

	/**
	 * API management answer leave application
	 */
	@PreAuthorize("hasRole('MANAGER')")
	@PutMapping("leave-applications/respond-to-leave/{id}")
	public ResponseEntity<?> respondToLeave(@PathVariable Long id,
			@Validated @RequestBody Map<String, String> requestBody,
			@AuthenticationPrincipal UserDetailsImpl principal) {
	    String status = requestBody.get("status");
		MessageResponse response = leaveApplicationService.respondToLeave(id, status, principal);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * API member cancel leave application
	 */
	@PreAuthorize("hasRole('MEMBER')")
	@PutMapping("leave-applications/cancel/{id}")
	public ResponseEntity<?> cancelLeave(@PathVariable Long id) {
		MessageResponse response = leaveApplicationService.cancelLeave(id);
		return ResponseEntity.ok(response);
	}

	/**
	 * API member update leave application
	 */
	@PreAuthorize("hasRole('MEMBER')")
	@PutMapping("leave-applications/update/{id}")
	public ResponseEntity<?> updateLeaveApplication(@PathVariable Long id, @Validated @RequestBody LeaveApplicationRequestDTO request) {
		MessageResponse response = leaveApplicationService.updateLeaveApplication(id, request);
		return ResponseEntity.ok(response);
	}	
}
