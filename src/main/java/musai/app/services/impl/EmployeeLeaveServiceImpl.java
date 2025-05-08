package musai.app.services.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import musai.app.DTO.request.EmployeeLeaveRequestDTO;
import musai.app.DTO.response.EmployeeLeaveResponseDTO;
import musai.app.exception.BadRequestException;
import musai.app.exception.NotFoundException;
import musai.app.models.LeaveType;
import musai.app.models.Employee;
import musai.app.models.EmployeeLeave;
import musai.app.repositories.LeaveTypeRepository;
import musai.app.repositories.EmployeeLeaveRepository;
import musai.app.repositories.EmployeeRepository;
import musai.app.services.EmployeeLeaveService;

@Service
@AllArgsConstructor
public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {
	private final EmployeeLeaveRepository employeeLeaveRepository;
	@Autowired
	private final EmployeeRepository employeeRepository;
	@Autowired
	private final LeaveTypeRepository leaveTypeRepository;

	// List All
	@Override
	public List<EmployeeLeaveResponseDTO> getEmployeeLeaves(String keyword) {
		List<EmployeeLeave> employeeLeaves = StringUtils.hasText(keyword)
				? employeeLeaveRepository.findActiveByKeyContaining(keyword)
				: employeeLeaveRepository.findAllActive();
		return employeeLeaves.stream().map(this::convertToDTOAll).collect(Collectors.toList());
	}

	private EmployeeLeaveResponseDTO convertToDTOAll(EmployeeLeave employeeLeave) {
		return EmployeeLeaveResponseDTO.builder().id(employeeLeave.getId())
				.employeeId(employeeLeave.getEmployee().getId())
				.employeeFullName(employeeLeave.getEmployee().getFullName())
				.leaveTypeId(employeeLeave.getLeaveType().getId()).leaveTypeName(employeeLeave.getLeaveType().getName())
				.leaveTypeValue(employeeLeave.getLeaveType().getValue()).totalDays(employeeLeave.getTotalDays())
				.usedDays(employeeLeave.getUsedDays()).remainedDays(employeeLeave.getRemainedDays())
				.validFrom(employeeLeave.getValidFrom()).validTo(employeeLeave.getValidTo()).build();
	}

	// Service get list employee leave with leaveTypeId, employeeId
	@Override
	public List<EmployeeLeaveResponseDTO> getEmployeeLeavesForMember(Long leaveTypeId, Long employeeId) {
		LocalDate today = LocalDate.now();
		// get employee leave of employee logging in + filter employee leave is valid
		// today
		// (validFrom < today < validTo)
		List<EmployeeLeave> employeeLeaves = employeeLeaveRepository.findByEmployeeId(employeeId).stream()
				.filter(employeeLeave -> (employeeLeave.getRemainedDays() > 0)
						&& !employeeLeave.getValidFrom().isAfter(today) && !employeeLeave.getValidTo().isBefore(today)
						&& (leaveTypeId == null || employeeLeave.getLeaveType().getId().equals(leaveTypeId)))
				.sorted(Comparator.comparing(EmployeeLeave::getValidTo)).collect(Collectors.toList());
		return employeeLeaves.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	// Service get list employee leave for member to show in screen
	@Override
	public List<EmployeeLeaveResponseDTO> getEmployeeLeavesForMember(Long employeeId) {
		LocalDate today = LocalDate.now();
		List<EmployeeLeave> employeeLeaves = employeeLeaveRepository.findByEmployeeId(employeeId).stream()
				.filter(employeeLeave -> !employeeLeave.getValidFrom().isAfter(today)
						&& !employeeLeave.getValidTo().isBefore(today))
				.sorted(Comparator.comparing(EmployeeLeave::getValidTo)).collect(Collectors.toList());
		List<EmployeeLeaveResponseDTO> responseDTO = employeeLeaves.stream().map(this::convertToDTO)
				.collect(Collectors.toList());
		return responseDTO;
	}

	private EmployeeLeaveResponseDTO convertToDTO(EmployeeLeave employeeLeave) {
		return new EmployeeLeaveResponseDTO(employeeLeave.getId(), employeeLeave.getEmployee().getId(),
				employeeLeave.getEmployee().getFullName(), employeeLeave.getLeaveType().getId(),
				employeeLeave.getLeaveType().getName(), employeeLeave.getLeaveType().getValue(),
				employeeLeave.getTotalDays(), employeeLeave.getUsedDays(), employeeLeave.getRemainedDays(),
				employeeLeave.getValidFrom(), employeeLeave.getValidTo());
	}

	// Service update Used days
	@Override
	public void updateUsedDaysRemainedDays(Long id, double usedDay) {
		EmployeeLeave employeeLeave = employeeLeaveRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("employee_leave_not_found"));
		employeeLeave.setUsedDays(usedDay);
		employeeLeave.setRemainedDays(employeeLeave.getTotalDays() - usedDay);
	}

	// Service create EmployeeLeave
	public EmployeeLeave createEmployeeLeave(EmployeeLeaveRequestDTO employeeLeaveRequestDTO) {
		// employeeId
		Employee employee = employeeRepository.findById(employeeLeaveRequestDTO.getEmployeeId())
				.orElseThrow(() -> new NotFoundException("employee_not_exist"));
		// leaveTypeId
		LeaveType leaveType = leaveTypeRepository.findById(employeeLeaveRequestDTO.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("leave_type_not_found"));
		if (employeeLeaveRequestDTO.getValidTo().isBefore(employeeLeaveRequestDTO.getValidFrom())) {
			throw new BadRequestException("requested_day_unavailble");
		}
		EmployeeLeave employeeLeave = new EmployeeLeave();

		employeeLeave.setEmployee(employee);
		employeeLeave.setLeaveType(leaveType);
		employeeLeave.setTotalDays(employeeLeaveRequestDTO.getTotalDays());
		employeeLeave.setUsedDays(0.0);
		employeeLeave.setRemainedDays(employeeLeave.getTotalDays());
		employeeLeave.setValidFrom(employeeLeaveRequestDTO.getValidFrom());
		employeeLeave.setValidTo(employeeLeaveRequestDTO.getValidTo());

		return employeeLeaveRepository.save(employeeLeave);
	}

	// Service update EmployeeLeave
	public EmployeeLeave updateEmployeeLeave(Long id, EmployeeLeaveRequestDTO employeeLeaveRequestDTO) {
		// Fetch exiting employee leave
		EmployeeLeave employeeLeave = employeeLeaveRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("employee_leave_not_found"));
		// employeeId
		Employee employee = employeeRepository.findById(employeeLeaveRequestDTO.getEmployeeId())
				.orElseThrow(() -> new NotFoundException("employee_not_exist"));
		// leaveTypeId
		LeaveType leaveType = leaveTypeRepository.findById(employeeLeaveRequestDTO.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("leave_type_not_found"));

		if (employeeLeaveRequestDTO.getValidTo().isBefore(employeeLeaveRequestDTO.getValidFrom())) {
			throw new BadRequestException("requested_day_unavailble");
		}
		if(employeeLeaveRequestDTO.getTotalDays() < employeeLeave.getUsedDays()) {
			throw new BadRequestException("exceeded_leave_days_limit");
		}
		// update information of employee
		employeeLeave.setEmployee(employee);
		employeeLeave.setLeaveType(leaveType);
		employeeLeave.setTotalDays(employeeLeaveRequestDTO.getTotalDays());
		employeeLeave.setUsedDays(employeeLeaveRequestDTO.getUsedDays());

		// recalculate remainedDays
		employeeLeave.setRemainedDays(employeeLeaveRequestDTO.getTotalDays() - employeeLeaveRequestDTO.getUsedDays());
		employeeLeave.setValidFrom(employeeLeaveRequestDTO.getValidFrom());
		employeeLeave.setValidTo(employeeLeaveRequestDTO.getValidTo());

		return employeeLeaveRepository.save(employeeLeave);
	}
}
