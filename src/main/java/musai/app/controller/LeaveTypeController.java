package musai.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveTypeRequestDTO;
import musai.app.DTO.response.LeaveTypeChildrenResponseDTO;
import musai.app.DTO.response.LeaveTypeParentResponseDTO;
import musai.app.DTO.response.LeaveTypeResponseDTO;
import musai.app.services.LeaveTypeService;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeController {

	private final LeaveTypeService leaveTypeService;

	public LeaveTypeController(LeaveTypeService LeaveTypeService) {
		this.leaveTypeService = LeaveTypeService;
	}

	// add a new paid leave request
	@PostMapping("/add")
	public ResponseEntity<?> addLeaveType(@RequestBody LeaveTypeRequestDTO request) {

		MessageResponse addResponse = leaveTypeService.createAddLeaveType(request);

		return new ResponseEntity<>(addResponse, HttpStatus.CREATED);
	}

	// update paid leave request
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateLeaveType(@PathVariable Long id, @RequestBody LeaveTypeRequestDTO request) {

		MessageResponse updateResponse = leaveTypeService.updateLeaveType(id, request);

		return new ResponseEntity<>(updateResponse, HttpStatus.OK);
	}

	// delete paid leave request
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteLeaveType(@PathVariable Long id) {

		MessageResponse deleteResponse = leaveTypeService.deleteLeaveType(id);

		return new ResponseEntity<>(deleteResponse, HttpStatus.OK);
	}

	// Create API list
	@GetMapping("/list")
//	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> getAllLeaveTypes() {

		List<LeaveTypeParentResponseDTO> leaveTypes = leaveTypeService.getAllLeaveTypes();

		return new ResponseEntity<>(leaveTypes, HttpStatus.OK);
	}

	// Create API list tree
	@GetMapping("/tree")
	public ResponseEntity<?> getAllLeaveTypeTree() {

		List<LeaveTypeChildrenResponseDTO> leaveTypes = leaveTypeService.getAllLeaveTypeTree();

		return new ResponseEntity<>(leaveTypes, HttpStatus.OK);
	}

	// Get Detail
	@GetMapping("/detail/{id}")
	public ResponseEntity<?> getLeaveTypeDetail(@PathVariable Long id) {

		LeaveTypeParentResponseDTO getLeaveTypeDetail = leaveTypeService.getLeaveTypeDetail(id);

		return new ResponseEntity<>(getLeaveTypeDetail, HttpStatus.OK);
	}

	// Get search
	@GetMapping("/search")
	public List<LeaveTypeChildrenResponseDTO> searchLeaveTypes(@RequestParam String keyword) {
		
		return leaveTypeService.searchLeaveType(keyword);
	}

}
