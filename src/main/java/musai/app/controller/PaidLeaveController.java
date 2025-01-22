package musai.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import musai.app.DTO.MessageResponse;
import musai.app.DTO.PaidLeaveDTO;
import musai.app.services.PaidLeaveService;

@RestController
@RequestMapping("/api/paid-leaves")
@RequiredArgsConstructor
public class PaidLeaveController {

	private final PaidLeaveService paidLeaveService;

	// add a new paid leave request
	@PostMapping("/add")
	public ResponseEntity<?> addPaidLeave(@RequestBody PaidLeaveDTO request) {

		// Call the service layer to process the update
		MessageResponse addResponse = paidLeaveService.createAddPaidLeave(request);

		// Return the response with HTTP status
		return new ResponseEntity<>(addResponse, HttpStatus.CREATED);
	}

	// update paid leave request
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updatePaidLeave(@PathVariable Long id, @RequestBody PaidLeaveDTO request) {

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

		// Call the service layer to process the update
		// DeletePaidLeaveResponse response = new
		// DeletePaidLeaveResponse(deleteRespone.getMessage());

		// Return the response with HTTP status
		return new ResponseEntity<>(deleteResponse, HttpStatus.OK);

	}

	// Create API list
	@GetMapping("/list")
	public ResponseEntity<?> getAllPaidLeaves() {

		// Call the service layer
		List<PaidLeaveDTO> paidLeaves = paidLeaveService.getAllPaidLeaves();

		// Return the list with HTTP status
		return new ResponseEntity<>(paidLeaves, HttpStatus.OK);
	}
}
