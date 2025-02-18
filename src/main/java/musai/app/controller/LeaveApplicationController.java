package musai.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveApplicationRequestDTO;
import musai.app.services.LeaveApplicationService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
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
	 * API member apply LeaveApplication
	 */
	@PostMapping("/leave-applications")
	public ResponseEntity<?> apply(@Validated @RequestBody LeaveApplicationRequestDTO request) {
		MessageResponse response = leaveApplicationService.apply(request);
		return ResponseEntity.ok(response);
	}
	
}
