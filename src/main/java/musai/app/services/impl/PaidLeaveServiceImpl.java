package musai.app.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.NoResultException;
import musai.app.DTO.MessageResponse;
import musai.app.DTO.PaidLeaveDTO;
import musai.app.exception.BadRequestException;
import musai.app.exception.NotFoundException;
import musai.app.models.PaidLeave;
import musai.app.repositories.PaidLeaveResposity;
import musai.app.services.PaidLeaveService;

@Service
public class PaidLeaveServiceImpl implements PaidLeaveService {
	private final PaidLeaveResposity paidLeaveRepository;

	public PaidLeaveServiceImpl(PaidLeaveResposity paidLeaveRepository) {
		this.paidLeaveRepository = paidLeaveRepository;
	}

	@Override
	public MessageResponse createAddPaidLeave(PaidLeaveDTO paidLeaveDTO) {

		if (paidLeaveRepository.existsByName(paidLeaveDTO.getName())) {
			return new MessageResponse("Error: Name is already taken!");
		}

		// Create new PaidLeave
		PaidLeave paidLeave = new PaidLeave(paidLeaveDTO.getName());

		paidLeaveRepository.save(paidLeave);

		// PaidLeave createdPaidLeave = paidLeaveRepository.save(paidLeave);

		return new MessageResponse("Add paidleave successfully!");
	}

	@Override
	public MessageResponse updatePaidLeave(Long id, PaidLeaveDTO paidLeaveDTO) {

		// Fetch the existing PaidLeave
		PaidLeave existingPaidLeave = paidLeaveRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Error: PaidLeave not found"));

		if (paidLeaveRepository.existsByName(paidLeaveDTO.getName())) {
			throw new BadRequestException("Error: Name is already taken!");
		}
		// Update fields of the PaidLeave
		existingPaidLeave.setName(paidLeaveDTO.getName());

		// Save the updated PaidLeave to the database
		paidLeaveRepository.save(existingPaidLeave);

		return new MessageResponse("PaidLeave updated successfully!");
	}

	@Override
	public MessageResponse deletePaidLeave(Long id) throws NoResultException {

		// Fetch the existing PaidLeave
		PaidLeave existingPaidLeave = paidLeaveRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Error: PaidLeave not found"));
		
		if (existingPaidLeave.getDeletedAt()!= null) {
			return new MessageResponse("Error: Not found!!");
		}

		// Save Delete to database
		paidLeaveRepository.save(existingPaidLeave);

		return new MessageResponse("Paid leave with ID " + id + " was soft deleted");
	}
	
	@Override
	public List<PaidLeaveDTO> getAllPaidLeaves (){
		
		 // Fetch all PaidLeave entities from the repository
		List<PaidLeave> paidLeaves = paidLeaveRepository.findAll();
		
		// Map each PaidLeave entity to PaidLeaveDTO
		return paidLeaves.stream()
						.map(leave -> new PaidLeaveDTO(leave.getId(), leave.getName()))
						.toList();
	}
}
