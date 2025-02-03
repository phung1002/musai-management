package musai.app.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.NoResultException;
import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveTypeRequestDTO;
import musai.app.DTO.response.LeaveTypeResponseDTO;
import musai.app.exception.BadRequestException;
import musai.app.exception.NotFoundException;
import musai.app.models.LeaveType;
import musai.app.repositories.LeaveTypeResposity;
import musai.app.services.LeaveTypeService;

@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {
	private final LeaveTypeResposity leaveTypeRepository;

	public LeaveTypeServiceImpl(LeaveTypeResposity leaveTypeRepository) {
		this.leaveTypeRepository = leaveTypeRepository;
	}

	@Override
	public MessageResponse createAddLeaveType(LeaveTypeRequestDTO leaveTypeDTO) {

		if (leaveTypeRepository.existsByName(leaveTypeDTO.getName())) {
			throw new BadRequestException("Error: Name is already taken!");
		}

		LeaveType parent = null;
        if (leaveTypeDTO.getParentId() != null) {
            parent = leaveTypeRepository.findById(leaveTypeDTO.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent not found!"));
        }
        
		// Create new LeaveType
		LeaveType leaveType = new LeaveType(leaveTypeDTO.getName(), parent);

		leaveTypeRepository.save(leaveType);

		// LeaveType createdLeaveType = LeaveTypeRepository.save(LeaveType);

		return new MessageResponse("Add Leave Type successfully!");
	}

	@Override
	public MessageResponse updateLeaveType(Long id, LeaveTypeRequestDTO LeaveTypeDTO) {

		// Fetch the existing LeaveType
		LeaveType existingLeaveType = leaveTypeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Error: LeaveType not found"));
		
		if (existingLeaveType.getDeletedAt() != null) {
			throw new NotFoundException("Error: LeaveType not found");
		}

		if (leaveTypeRepository.existsByName(LeaveTypeDTO.getName())) {
			throw new BadRequestException("Error: Name is already taken!");
		}
		// Update fields of the LeaveType
		existingLeaveType.setName(LeaveTypeDTO.getName());

		// Save the updated LeaveType to the database
		leaveTypeRepository.save(existingLeaveType);

		return new MessageResponse("Leave type updated successfully!");
	}

	@Override
	public MessageResponse deleteLeaveType(Long id) throws NoResultException {

		// Fetch the existing LeaveType
		LeaveType existingLeaveType = leaveTypeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Error: LeaveType not found"));

		if (existingLeaveType.getDeletedAt() != null) {
			 throw new NotFoundException("Error: LeaveType not found");
		}
		
		existingLeaveType.setDeletedAt(LocalDateTime.now());
		// Save Delete to database
		leaveTypeRepository.save(existingLeaveType);

		return new MessageResponse("Paid leave with ID " + id + " was soft deleted");
	}

	@Override
	public List<LeaveTypeResponseDTO> getAllLeaveTypes() {

		// Fetch all LeaveType entities from the repository
		List<LeaveType> LeaveTypes = leaveTypeRepository.findAll();

		// Map each LeaveType entity to LeaveTypeDTO
		return LeaveTypes.stream()
				.filter(leave -> leave.getParent() == null)
				.map(leave -> new LeaveTypeResponseDTO(leave.getId(), leave.getName(), leave.getChildren()))
				.toList();
	}

	@Override
	public LeaveTypeResponseDTO getLeaveTypeDetail(Long id) {

		LeaveType leaveType = leaveTypeRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new NotFoundException("Error: LeaveType not found"));

		if (leaveType.getDeletedAt() != null) {
			throw new NotFoundException("Error: LeaveType not found");
		}
		
		return new LeaveTypeResponseDTO(leaveType.getId(), leaveType.getName(), leaveType.getChildren());
	}

	@Override
	public List<LeaveTypeResponseDTO> searchLeaveType(String keyword) {

		List<LeaveTypeResponseDTO> filteredLeaves = leaveTypeRepository.findAll().stream()
				.filter(leave -> leave.getName().toLowerCase().contains(keyword.toLowerCase()))
				.map(leave -> new LeaveTypeResponseDTO(leave.getId(), leave.getName(), leave.getChildren()))
				.collect(Collectors.toList());

		return filteredLeaves;
	}
	
}
