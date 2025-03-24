package musai.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
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
@RequestMapping("/api/leave-applications")
@AllArgsConstructor
public class LeaveApplicationController {
	private final LeaveApplicationService leaveApplicationService;
	
	/**
	 * API management get all leave application
	 */
	@PreAuthorize("hasRole('MANAGER')")
	@GetMapping("/all")
	public ResponseEntity<?> getAllLeaveApplications(@RequestParam(required = false) String keyword) {
		List<LeaveApplicationResponseDTO> response = leaveApplicationService.getAllLeaveApplications(keyword);
		return ResponseEntity.ok(response);
	}

	/**
	 * API member get their leave application
	 */
	@PreAuthorize("hasRole('MEMBER')")
	@GetMapping
	public ResponseEntity<?> getLeaveApplicationOfMember(@AuthenticationPrincipal UserDetailsImpl principal,
			@RequestParam(required = false) String keyword) {
		List<LeaveApplicationResponseDTO> response = leaveApplicationService.getLeaveApplicationsForMember(principal, keyword);
		return ResponseEntity.ok(response);
	}
	/**
	 * API leave application approved to show in calendar
	 */
	@GetMapping("/approved")
	public ResponseEntity<?> getApprovedLeaveApplications() {
		List<LeaveApplicationResponseDTO> response = leaveApplicationService.getApprovedLeaveApplications();
		return ResponseEntity.ok(response);
	}
	
	/**
	 * API member apply leave application
	 */
	@PreAuthorize("hasRole('MEMBER')")
	@PostMapping
	public ResponseEntity<?> applyLeave(@Validated @RequestBody LeaveApplicationRequestDTO request,
			@AuthenticationPrincipal UserDetailsImpl principal) {
		MessageResponse response = leaveApplicationService.applyLeave(request, principal);
		return ResponseEntity.ok(response);
	}

	/**
	 * API management answer leave application
	 */
	@PreAuthorize("hasRole('MANAGER')")
	@PutMapping("/respond-to-leave/{id}")
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
	@PutMapping("/cancel/{id}")
	public ResponseEntity<?> cancelLeave(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl principal) {
		MessageResponse response = leaveApplicationService.cancelLeave(id, principal);
		return ResponseEntity.ok(response);
	}

	/**
	 * API member update leave application
	 */
	@PreAuthorize("hasRole('MEMBER')")
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateLeaveApplication(@PathVariable Long id,
			@Validated @RequestBody LeaveApplicationRequestDTO request,
			@AuthenticationPrincipal UserDetailsImpl principal) {
		MessageResponse response = leaveApplicationService.updateLeaveApplication(id, request, principal);
		return ResponseEntity.ok(response);
	}
}
