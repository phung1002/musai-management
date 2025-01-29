package musai.app.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.NoResultException;
import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.PaidLeaveRequestDTO;
import musai.app.DTO.response.PaidLeaveResponseDTO;
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
	public MessageResponse createAddPaidLeave(PaidLeaveRequestDTO paidLeaveDTO) {

		if (paidLeaveRepository.existsByName(paidLeaveDTO.getName())) {
			return new MessageResponse("Error: Name is already taken!");
		}

		// Create new PaidLeave
		PaidLeave paidLeave = new PaidLeave(paidLeaveDTO.getName(), paidLeaveDTO.getParent(),
				paidLeaveDTO.getChildren());

		paidLeaveRepository.save(paidLeave);

		// PaidLeave createdPaidLeave = paidLeaveRepository.save(paidLeave);

		return new MessageResponse("Add paidleave successfully!");
	}

	@Override
	public MessageResponse updatePaidLeave(Long id, PaidLeaveRequestDTO paidLeaveDTO) {

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

		if (existingPaidLeave.getDeletedAt() != null) {
			return new MessageResponse("Error: Not found!!");
		}

		// Save Delete to database
		paidLeaveRepository.save(existingPaidLeave);

		return new MessageResponse("Paid leave with ID " + id + " was soft deleted");
	}

	@Override
	public List<PaidLeaveResponseDTO> getAllPaidLeaves() {

		// Fetch all PaidLeave entities from the repository
		List<PaidLeave> paidLeaves = paidLeaveRepository.findAll();

		// Map each PaidLeave entity to PaidLeaveDTO
		return paidLeaves.stream()
				.filter(leave -> leave.getParent() == null)
				.map(leave -> new PaidLeaveResponseDTO(leave.getId(), leave.getName(), leave.getChildren()))
				.toList();
	}

	@Override
	public PaidLeaveResponseDTO getPaidLeaveDetail(Long id) {

		PaidLeave paidLeave = paidLeaveRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new NotFoundException("Error: PaidLeave not found"));

		return new PaidLeaveResponseDTO(paidLeave.getId(), paidLeave.getName(), paidLeave.getChildren());
	}

	@Override
	public List<PaidLeaveResponseDTO> searchPaidLeave(String keyword) {

		List<PaidLeaveResponseDTO> filteredLeaves = paidLeaveRepository.findAll().stream()
				.filter(leave -> leave.getName().toLowerCase().contains(keyword.toLowerCase()))
				.map(leave -> new PaidLeaveResponseDTO(leave.getId(), leave.getName(), leave.getChildren()))
				.collect(Collectors.toList());

		// if(filteredLeaves.isEmpty()) {
		// throw new NotFoundException("Error: The keyword not found");
		// }

		return filteredLeaves;
	}

}
