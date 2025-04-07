package musai.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import musai.app.DTO.request.LeaveTypeRequestDTO;
import musai.app.DTO.response.LeaveTypeChildrenResponseDTO;
import musai.app.DTO.response.LeaveTypeParentResponseDTO;
import musai.app.DTO.response.MessageResponse;
import musai.app.services.LeaveTypeService;

@RestController
@RequestMapping("/api/leave-types")
@AllArgsConstructor
public class LeaveTypeController {

	private final LeaveTypeService leaveTypeService;

	// API create
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> create(@RequestBody LeaveTypeRequestDTO request) {

		MessageResponse addResponse = leaveTypeService.createLeaveType(request);

		return new ResponseEntity<>(addResponse, HttpStatus.CREATED);
	}

	// API update 
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody LeaveTypeRequestDTO request) {

		MessageResponse updateResponse = leaveTypeService.updateLeaveType(id, request);

		return new ResponseEntity<>(updateResponse, HttpStatus.OK);
	}

	// API delete 
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {

		MessageResponse deleteResponse = leaveTypeService.deleteLeaveType(id);

		return new ResponseEntity<>(deleteResponse, HttpStatus.OK);
	}

	// API list
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> list(@RequestParam String keyword) {

		List<LeaveTypeParentResponseDTO> leaveTypes = leaveTypeService.getAllLeaveTypes(keyword);

		return new ResponseEntity<>(leaveTypes, HttpStatus.OK);
	}

	// API list tree
	@GetMapping("/tree")
	public ResponseEntity<?> tr() {

		List<LeaveTypeChildrenResponseDTO> leaveTypes = leaveTypeService.getAllLeaveTypeTree();

		return new ResponseEntity<>(leaveTypes, HttpStatus.OK);
	}
}
