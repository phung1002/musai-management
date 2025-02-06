package musai.app.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.NoResultException;
import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveTypeRequestDTO;
import musai.app.DTO.response.LeaveTypeChildrenResponseDTO;
import musai.app.DTO.response.LeaveTypeParentResponseDTO;
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
	public MessageResponse updateLeaveType(Long id, LeaveTypeRequestDTO leaveTypeDTO) {

		// Fetch the existing LeaveType
		LeaveType existingLeaveType = leaveTypeRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new NotFoundException("Error: LeaveType not found"));
		if (!leaveTypeDTO.getName().equals(existingLeaveType.getName())) {
			if (leaveTypeRepository.existsByName(leaveTypeDTO.getName())) {
				throw new BadRequestException("Error: Name is already taken!");
			}
			// Update fields of the LeaveType
			existingLeaveType.setName(leaveTypeDTO.getName());
		}

		LeaveType parent = null;
		// check parent
		if (leaveTypeDTO.getParentId() == id) {
			throw new BadRequestException("Error: Itself can not be parent");
		}
		if (leaveTypeDTO.getParentId() != null) {
			parent = leaveTypeRepository.findByIdAndDeletedAtIsNull(leaveTypeDTO.getParentId())
					.orElseThrow(() -> new NotFoundException("Error: LeaveType parent not found"));
		}

		existingLeaveType.setParent(parent);

		// Save the updated LeaveType to the database
		leaveTypeRepository.save(existingLeaveType);

		return new MessageResponse("Leave type updated successfully!");
	}

	@Override
	public MessageResponse deleteLeaveType(Long id) throws NoResultException {

		// Fetch the existing LeaveType
		LeaveType existingLeaveType = leaveTypeRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new NotFoundException("Error: LeaveType not found"));

		if(existingLeaveType.getChildren() != null) {
			throw new BadRequestException("Error: LeaveType had children can't be delete");
		}
		existingLeaveType.setDeletedAt(LocalDateTime.now());
		// Save Delete to database
		leaveTypeRepository.save(existingLeaveType);

		return new MessageResponse("Paid leave with ID " + id + " was soft deleted");
	}

	@Override
	public List<LeaveTypeResponseDTO> getAllLeaveTypes() {

		// Fetch all LeaveType entities from the repository
		List<LeaveType> LeaveTypes = leaveTypeRepository.findAllByDeletedAtIsNull();

		// Map each LeaveType entity to LeaveTypeDTO
		return LeaveTypes.stream().filter(leave -> leave.getParent() == null)
				.map(leave -> new LeaveTypeResponseDTO(leave.getId(), leave.getName())).collect(Collectors.toList());
	}

	@Override
	public List<LeaveTypeChildrenResponseDTO> getAllLeaveTypeTree() {

		// Fetch all LeaveType entities from the repository
		List<LeaveType> LeaveTypes = leaveTypeRepository.findAll();

		// Map each LeaveType entity to LeaveTypeDTO
		return LeaveTypes.stream().filter(leave -> leave.getParent() == null)
				.map(leave -> new LeaveTypeChildrenResponseDTO(leave.getId(), leave.getName(), leave.getChildren()))
				.toList();
	}
	@Override
	public LeaveTypeParentResponseDTO getLeaveTypeDetail(Long id) {
		LeaveType leaveType = leaveTypeRepository.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new NotFoundException("Error: LeaveType not found"));

		Long parentId = null;
		if (leaveType.getParent() != null) {
			parentId = leaveType.getParent().getId();
		}

		return new LeaveTypeParentResponseDTO(leaveType.getId(), leaveType.getName(), parentId);
	}

	@Override
	public List<LeaveTypeChildrenResponseDTO> searchLeaveType(String keyword) {

		List<LeaveTypeChildrenResponseDTO> filteredLeaves = leaveTypeRepository.findAll().stream()
				.filter(leave -> leave.getName().toLowerCase().contains(keyword.toLowerCase()))
				.map(leave -> new LeaveTypeChildrenResponseDTO(leave.getId(), leave.getName(), leave.getChildren()))
				.collect(Collectors.toList());

		return filteredLeaves;
	}

}
