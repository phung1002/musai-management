package musai.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.UserLeaveRequestDTO;
import musai.app.DTO.response.UserLeaveResponseDTO;
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

	// update
	@PutMapping("/update/{id}")
	public ResponseEntity<?> editUserLeave(@PathVariable Long id,@RequestBody UserLeaveRequestDTO request) {
		userLeaveService.editUserLeave(request);

		return ResponseEntity.ok(new MessageResponse("Update user leaves successfully"));
	}
	
	// List All
	@GetMapping("/list")
	public ResponseEntity<?> getAllUserLeaves() {
		List<UserLeaveResponseDTO> userLeave = userLeaveService.getAllUserLeaves();
		
		return ResponseEntity.ok(userLeave);
		
	}

	
}