package musai.app.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
	private final LeaveTypeResposity leaveTypeResposity;

	public LeaveTypeServiceImpl(LeaveTypeResposity leaveTypeRepository) {
		this.leaveTypeResposity = leaveTypeRepository;
	}

	@Override
	public MessageResponse createAddLeaveType(LeaveTypeRequestDTO leaveTypeDTO) {

		if (leaveTypeResposity.existsByName(leaveTypeDTO.getName())) {
			throw new BadRequestException("Error: Name is already taken!");
		}

		LeaveType parent = findParent(leaveTypeDTO.getParentId());

		// Create new LeaveType
		LeaveType leaveType = new LeaveType(leaveTypeDTO.getName(), parent);

		leaveTypeResposity.save(leaveType);

		return new MessageResponse("Add Leave Type successfully!");
	}

	@Override
	public MessageResponse updateLeaveType(Long id, LeaveTypeRequestDTO leaveTypeDTO) {

		// Fetch the existing LeaveType
		LeaveType existingLeaveType = leaveTypeResposity.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new NotFoundException("Error: LeaveType not found"));
		if (!leaveTypeDTO.getName().equals(existingLeaveType.getName())) {
			if (leaveTypeResposity.existsByName(leaveTypeDTO.getName())) {
				throw new BadRequestException("Error: Name is already taken!");
			}
			// Update fields of the LeaveType
			existingLeaveType.setName(leaveTypeDTO.getName());
		}
		// check parent
		if (leaveTypeDTO.getParentId() == id) {
			throw new BadRequestException("Error: Itself can not be parent");
		}
		LeaveType parent = findParent(leaveTypeDTO.getParentId());

		existingLeaveType.setParent(parent);

		// Save the updated LeaveType to the database
		leaveTypeResposity.save(existingLeaveType);

		return new MessageResponse("Leave type updated successfully!");
	}

	@Override
	public MessageResponse deleteLeaveType(Long id) throws NoResultException {

		// Fetch the existing LeaveType
		LeaveType existingLeaveType = leaveTypeResposity.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new NotFoundException("Error: LeaveType not found"));

		if (existingLeaveType.getChildren() != null) {
			throw new BadRequestException("Error: LeaveType had children can't be delete");
		}
		existingLeaveType.setDeletedAt(LocalDateTime.now());
		// Save Delete to database
		leaveTypeResposity.save(existingLeaveType);

		return new MessageResponse("Paid leave with ID " + id + " was soft deleted");
	}

	@Override
	public List<LeaveTypeResponseDTO> getAllLeaveTypes() {

		// Fetch all LeaveType entities from the repository
		List<LeaveType> leaveTypes = leaveTypeResposity.findAllByDeletedAtIsNull();

		// Map each LeaveType entity to LeaveTypeDTO
		return leaveTypes.stream().map(leave -> new LeaveTypeResponseDTO(leave.getId(), leave.getName()))
				.collect(Collectors.toList());
	}

	@Override
	public List<LeaveTypeChildrenResponseDTO> getAllLeaveTypeTree() {
		List<LeaveType> leaveTypes = leaveTypeResposity.findAll(); // includes deleted and not-deleted

		List<LeaveType> activeLeaveTypes = leaveTypes.stream().filter(leave -> leave.getDeletedAt() == null).toList(); // not

		return activeLeaveTypes.stream().filter(leave -> leave.getParent() == null)
				.map(leave -> new LeaveTypeChildrenResponseDTO(leave.getId(), leave.getName(),
						filterChildren(leave.getChildren(), activeLeaveTypes)))
				.toList();
	}

	private List<LeaveType> filterChildren(List<LeaveType> children, List<LeaveType> activeLeaveTypes) {
		if (children == null)
			return new ArrayList<>();
		return children.stream().filter(activeLeaveTypes::contains).toList();
	}

	@Override
	public LeaveTypeParentResponseDTO getLeaveTypeDetail(Long id) {
		LeaveType leaveType = leaveTypeResposity.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new NotFoundException("Error: LeaveType not found"));

		Long parentId = null;
		if (leaveType.getParent() != null) {
			parentId = leaveType.getParent().getId();
		}

		return new LeaveTypeParentResponseDTO(leaveType.getId(), leaveType.getName(), parentId);
	}

	@Override
	public List<LeaveTypeChildrenResponseDTO> searchLeaveType(String keyword) {

		List<LeaveTypeChildrenResponseDTO> filteredLeaves = leaveTypeResposity.findAll().stream()
				.filter(leave -> leave.getName().toLowerCase().contains(keyword.toLowerCase()))
				.map(leave -> new LeaveTypeChildrenResponseDTO(leave.getId(), leave.getName(), leave.getChildren()))
				.collect(Collectors.toList());

		return filteredLeaves;
	}

	private LeaveType findParent(Long id) {
		if (id == null)
			return null;
		return leaveTypeResposity.findByIdAndDeletedAtIsNull(id)
				.orElseThrow(() -> new NotFoundException("Parent not found!"));
	}

}
