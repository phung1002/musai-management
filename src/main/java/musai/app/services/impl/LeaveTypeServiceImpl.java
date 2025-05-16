package musai.app.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import musai.app.DTO.request.LeaveTypeRequestDTO;
import musai.app.DTO.response.LeaveTypeChildrenResponseDTO;
import musai.app.DTO.response.LeaveTypeParentResponseDTO;
import musai.app.DTO.response.MessageResponse;
import musai.app.exception.BadRequestException;
import musai.app.exception.NotFoundException;
import musai.app.models.ELeaveValue;
import musai.app.models.EmployeeLeave;
import musai.app.models.LeaveApplication;
import musai.app.models.LeaveType;
import musai.app.repositories.EmployeeLeaveRepository;
import musai.app.repositories.LeaveApplicationRepository;
import musai.app.repositories.LeaveTypeRepository;
import musai.app.services.LeaveTypeService;

@Service
@RequiredArgsConstructor
public class LeaveTypeServiceImpl implements LeaveTypeService {

	private final LeaveTypeRepository leaveTypeRepository;
	private final EmployeeLeaveRepository employeeLeaveRepository; // Add
	private final LeaveApplicationRepository leaveApplicationRepository; // Add

	// add
	@Override
	public MessageResponse createLeaveType(LeaveTypeRequestDTO leaveTypeDTO) {

		if (leaveTypeRepository.existsByName(leaveTypeDTO.getName())) {
			throw new BadRequestException("name_already_taken");
		}
		LeaveType parent = findParent(leaveTypeDTO.getParentId());
		LeaveType leaveType = new LeaveType(leaveTypeDTO.getName(), parent, null);
		leaveTypeRepository.save(leaveType);
		return new MessageResponse("Add Leave Type successfully!");
	}

	// update
	@Override
	public MessageResponse updateLeaveType(Long id, LeaveTypeRequestDTO leaveTypeDTO) {

		LeaveType existingLeaveType = leaveTypeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("leave_type_not_found"));
		if (!leaveTypeDTO.getName().equals(existingLeaveType.getName())) {
			if (leaveTypeRepository.existsByName(leaveTypeDTO.getName())) {
				throw new BadRequestException("name_already_taken");
			}
			existingLeaveType.setName(leaveTypeDTO.getName());
		}
		if (leaveTypeDTO.getParentId() == id) {
			throw new BadRequestException("itself_can_not_be_parent");
		}
		LeaveType parent = findParent(leaveTypeDTO.getParentId());
		existingLeaveType.setParent(parent);
		leaveTypeRepository.save(existingLeaveType);
		return new MessageResponse("Leave type updated successfully!");
	}

	// delete
	@Override
	public MessageResponse deleteLeaveType(Long id) throws NoResultException {

		LeaveType existingLeaveType = leaveTypeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("leave_type_not_found"));

		List<LeaveType> childrenTypes = leaveTypeRepository.findByParentId(id);

		// Can not delete summer day, allday, halfday
		String value = existingLeaveType.getValue();
		if (value != null && (value.equals(ELeaveValue.HALF_DAY.name()) || value.equals(ELeaveValue.FULL_DAY.name())
				|| value.equals(ELeaveValue.SUMMER_DAY.name()))) {
			throw new BadRequestException("cannot_delete_fixed_leave_type");
		}

		if (!childrenTypes.isEmpty()) {
			throw new BadRequestException("cannot_delete_leave_type_have_children");
		}

		// Soft delete EmployeeLeave, LeaveApplication inside leaveType
		List<EmployeeLeave> leaves = employeeLeaveRepository.findByLeaveTypeId(id);
		leaves.forEach(leave -> leave.setDeletedAt(LocalDateTime.now()));
		employeeLeaveRepository.saveAll(leaves);

		List<LeaveApplication> applications = leaveApplicationRepository.findByLeaveTypeId(id);
		applications.forEach(app -> app.setDeletedAt(LocalDateTime.now()));
		leaveApplicationRepository.saveAll(applications);

		// Soft delete leaveType
		existingLeaveType.setDeletedAt(LocalDateTime.now());
		leaveTypeRepository.save(existingLeaveType);
		return new MessageResponse("Paid leave with ID " + id + " was soft deleted");
	}

	// get all and search
	@Override
	public List<LeaveTypeParentResponseDTO> getAllLeaveTypes(String keyword) {

		// Fetch all LeaveType entities from the repository based on keyword or fetch
		// all if no keyword
		List<LeaveType> leaveTypes = StringUtils.hasText(keyword)
				? leaveTypeRepository.findActiveByKeywordContaining(keyword)
				: leaveTypeRepository.findAllActive();

		// If the list is empty, return an empty list or handle it in another way if
		// needed
		if (leaveTypes.isEmpty()) {
			return Collections.emptyList();
		}

		// Map each LeaveType entity to LeaveTypeParentResponseDTO
		return leaveTypes.stream().map(leave -> new LeaveTypeParentResponseDTO(leave.getId(), leave.getName(),
				leave.getParent() != null ? leave.getParent().getId() : null // Safe check for parent
		)).collect(Collectors.toList());
	}

	// Create API list tree
	@Override
	public List<LeaveTypeChildrenResponseDTO> getAllLeaveTypeTree() {
		List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

		List<LeaveType> activeLeaveTypes = leaveTypes.stream().filter(leave -> leave.getDeletedAt() == null).toList();

		return activeLeaveTypes.stream().filter(leave -> leave.getParent() == null)
				.map(leave -> new LeaveTypeChildrenResponseDTO(leave.getId(), leave.getName(), leave.getValue(),
						filterChildren(leave.getChildren(), activeLeaveTypes)))
				.toList();
	}

	private List<LeaveTypeChildrenResponseDTO> filterChildren(List<LeaveType> children,
			List<LeaveType> activeLeaveTypes) {
		if (children == null) {
			return new ArrayList<>();
		}
		return children.stream().filter(activeLeaveTypes::contains)
				.map(child -> new LeaveTypeChildrenResponseDTO(child.getId(), child.getName(), child.getValue(),
						filterChildren(child.getChildren(), activeLeaveTypes)))
				.toList();
	}

	private LeaveType findParent(Long id) {
		if (id == null)
			return null;
		return leaveTypeRepository.findById(id).orElseThrow(() -> new NotFoundException("leave_type_not_found"));
	}

}
