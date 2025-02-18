package musai.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.UserLeaveRequestDTO;
import musai.app.services.UserLeaveService;

@RestController
@RequestMapping("/api/user-leaves")
public class UserLeaveController {
	@Autowired
	private UserLeaveService userLeaveService;
	
	public UserLeaveController(UserLeaveService userLeaveService) {
		this.userLeaveService = userLeaveService;
	}

	// add
	@PostMapping("/add")
	public ResponseEntity<?> createLeave(@RequestBody UserLeaveRequestDTO request) {
		userLeaveService.createUserLeave(request);

		return ResponseEntity.ok(new MessageResponse("Add user leaves successfully"));
	}

}