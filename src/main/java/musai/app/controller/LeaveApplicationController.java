package musai.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveApplicationRequestDTO;
import musai.app.security.services.UserDetailsImpl;
import musai.app.services.LeaveApplicationService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
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
	 * API member apply leave application
	 */

	@PreAuthorize("hasRole('MEMBER')")
	@PostMapping("/leave-applications")
	public ResponseEntity<?> applyLeave(@Validated @RequestBody LeaveApplicationRequestDTO request) {
		MessageResponse response = leaveApplicationService.applyLeave(request);
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

}
