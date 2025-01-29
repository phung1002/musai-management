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
import musai.app.DTO.request.PaidLeaveRequestDTO;
import musai.app.DTO.response.PaidLeaveResponseDTO;
import musai.app.services.PaidLeaveService;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/paid-leaves")
public class PaidLeaveController {

	private final PaidLeaveService paidLeaveService;

	public PaidLeaveController(PaidLeaveService paidLeaveService) {
		this.paidLeaveService = paidLeaveService;
	}
	
	// add a new paid leave request
	@PostMapping("/add")
	public ResponseEntity<?> addPaidLeave(@RequestBody PaidLeaveRequestDTO request) {

		// Call the service layer to process the update
		MessageResponse addResponse = paidLeaveService.createAddPaidLeave(request);

		// Return the response with HTTP status
		return new ResponseEntity<>(addResponse, HttpStatus.CREATED);
	}

	// update paid leave request
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updatePaidLeave(@PathVariable Long id, @RequestBody PaidLeaveRequestDTO request) {

		// Call the service layer to process the update
		MessageResponse updateResponse = paidLeaveService.updatePaidLeave(id, request);

		// Return the response with HTTP status
		return new ResponseEntity<>(updateResponse, HttpStatus.OK);
	}

	// delete paid leave request
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deletePaidLeave(@PathVariable Long id) {

		// Call the service layer to process the update
		MessageResponse deleteResponse = paidLeaveService.deletePaidLeave(id);

		return new ResponseEntity<>(deleteResponse, HttpStatus.OK);

	}

	// Create API list
	@GetMapping("/list")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> getAllPaidLeaves() {

		// Call the service layer
		List<PaidLeaveResponseDTO> paidLeaves = paidLeaveService.getAllPaidLeaves();

		// Return the list with HTTP status
		return new ResponseEntity<>(paidLeaves, HttpStatus.OK);
	}

	// Get Detail
	@GetMapping("/detail/{id}")
	public ResponseEntity<?> getPaidLeaveDetail(@PathVariable Long id) {

		// Call the service layer
		PaidLeaveResponseDTO getPaidLeaveDetail = paidLeaveService.getPaidLeaveDetail(id);

		// Return the list with HTTP status
		return new ResponseEntity<>(getPaidLeaveDetail, HttpStatus.OK);
	}

	// @GetMapping("/search")
	@GetMapping("/search")
	public List<PaidLeaveResponseDTO> searchPaidLeaves(@RequestParam String keyword) {
		// Call the searchPaidLeave method from the service layer and return the result
		return paidLeaveService.searchPaidLeave(keyword);
	}

}
