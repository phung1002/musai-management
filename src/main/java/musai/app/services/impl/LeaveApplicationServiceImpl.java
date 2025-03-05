package musai.app.services.impl;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveApplicationRequestDTO;
import musai.app.DTO.response.LeaveApplicationResponseDTO;
import musai.app.DTO.response.UserLeaveResponseDTO;
import musai.app.exception.BadRequestException;
import musai.app.exception.NotFoundException;
import musai.app.models.ELeaveStatus;
import musai.app.models.LeaveApplication;
import musai.app.models.LeaveType;
import musai.app.models.User;
import musai.app.repositories.LeaveApplicationRepository;
import musai.app.repositories.LeaveTypeResposity;
import musai.app.repositories.UserRepository;
import musai.app.security.services.UserDetailsImpl;
import musai.app.services.LeaveApplicationService;
import musai.app.services.UserLeaveService;

@Service
public class LeaveApplicationServiceImpl implements LeaveApplicationService {

	private LeaveApplicationRepository leaveApplicationRepository;
	private UserRepository userRepository;
	private LeaveTypeResposity leaveTypeResposity;

	@Autowired
	private UserLeaveService userLeaveService;

	public LeaveApplicationServiceImpl(LeaveApplicationRepository leaveApplicationRepository,
			UserRepository userRepository, LeaveTypeResposity leaveTypeResposity, UserLeaveService userLeaveService) {
		super();
		this.leaveApplicationRepository = leaveApplicationRepository;
		this.userRepository = userRepository;
		this.leaveTypeResposity = leaveTypeResposity;
		this.userLeaveService = userLeaveService;
	}

	/**
	 * Service get all leave applications
	 */
	@Override
	public List<LeaveApplicationResponseDTO> getAllLeaveApplications() {
		List<LeaveApplication> leaveApplications = leaveApplicationRepository.findAllByOrderByCreatedAtDesc();
		if (leaveApplications.isEmpty()) {
			throw new NotFoundException("No leave applications found.");
		}

		List<LeaveApplicationResponseDTO> responseDTOs = leaveApplications.stream().map(this::convertToDTO)
				.collect(Collectors.toList());
		return responseDTOs;
	}

	// Convert LeaveApplication to LeaveApplicationResponseDTO
	private LeaveApplicationResponseDTO convertToDTO(LeaveApplication leaveApplication) {
		return new LeaveApplicationResponseDTO(leaveApplication.getId(),
				leaveApplication.getUser() != null ? leaveApplication.getUser().getFullName() : "Unknown",
				leaveApplication.getLeaveType().getName() != null ? leaveApplication.getLeaveType().getName()
						: "Unknown",
				leaveApplication.getStartDate(), leaveApplication.getEndDate(), leaveApplication.getReason(),
				leaveApplication.getStatus(), leaveApplication.getRespondedAt(),
				Optional.ofNullable(leaveApplication.getRespondedBy()).map(User::getFullName).orElse(null),
				leaveApplication.getCreatedAt());
	}

	/**
	 * Service get leave applications of user are logging in
	 */
	@Override
	public List<LeaveApplicationResponseDTO> getLeaveApplicationsForMember(UserDetailsImpl principal) {
		// Find leave application of user are logging in
		List<LeaveApplication> leaveApplications = leaveApplicationRepository.findByUserId(principal.getId());

		if (leaveApplications.isEmpty()) {
			throw new NotFoundException("No leave applications found for this member.");
		}

		List<LeaveApplicationResponseDTO> responseDTOs = leaveApplications.stream().map(this::convertToDTO)
				.collect(Collectors.toList());

		return responseDTOs;
	}

	/**
	 * Service appply leave application
	 */
	@Override
	public MessageResponse applyLeave(LeaveApplicationRequestDTO request, UserDetailsImpl principal) {

		User user = userRepository.findByIdAndDeletedAtIsNull(principal.getId())
				.orElseThrow(() -> new NotFoundException("User not exist."));
		LeaveType leaveType = leaveTypeResposity.findByIdAndDeletedAtIsNull(request.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("Leave type not exist"));

		// if (leaveType.getValue() == ELeaveValue.)
		// check condition: remainDays > requestDays
		List<UserLeaveResponseDTO> userLeaves = userLeaveService.getUserLeaveForMember(request.getLeaveTypeId(),
				principal);
		double remainDays = userLeaves.stream().mapToDouble(userLeave -> userLeave.getTotalDays() - userLeave.getUsedDays())
				.sum();
		double requestDays = (double) IntStream.rangeClosed(0, (double) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()))
		        .mapToObj(request.getStartDate()::plusDays)
		        .filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY)
		        .count();

		if (requestDays > remainDays) {
			throw new BadRequestException("Requested days exceed the remaining days.");
		}

		// update usedDays to UserLeave
		for (UserLeaveResponseDTO item : userLeaves) {
			if (item.getTotalDays() - item.getUsedDays() >= requestDays) {
				// item.usedDays += requestDays
				userLeaveService.updateUsedDays(item.getId(), item.getUsedDays() + requestDays);
				break;
			} else {
				requestDays -= (item.getTotalDays() - item.getUsedDays());
				// item.usedDays = item.totalDays
				userLeaveService.updateUsedDays(item.getId(), item.getTotalDays());
			}
		}

		LeaveApplication leaveApplication = new LeaveApplication();
		leaveApplication.setUser(user);
		leaveApplication.setLeaveType(leaveType);
		leaveApplication.setStartDate(request.getStartDate());
		leaveApplication.setEndDate(request.getEndDate());
		leaveApplication.setReason(request.getReason());
		leaveApplication.setStatus(ELeaveStatus.PENDING);
		leaveApplicationRepository.save(leaveApplication);
		return new MessageResponse("Apply success");
	}

	/**
	 * Servive answer leave application
	 */
	@Override
	public MessageResponse respondToLeave(Long id, String status, UserDetailsImpl principal) {
		LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Leave application not found"));

		// Check status is valid
		if (!ELeaveStatus.isValidStatus(status)) {
			throw new BadRequestException("Invalid status");
		}
		ELeaveStatus eStatus = ELeaveStatus.valueOf(status.toUpperCase());

		// Can only be revoked if the status is 'approved'.
		if (status.equals(ELeaveStatus.REVOKED) && !leaveApplication.getStatus().equals(ELeaveStatus.APPROVED)) {
			throw new BadRequestException("Can only be revoked if the status is 'approved'.");
		}

		// Can only be revoked if the status is 'pending'.
		EnumSet<ELeaveStatus> updatableStatuses = EnumSet.of(ELeaveStatus.APPROVED, ELeaveStatus.REJECTED,
				ELeaveStatus.REQUESTED_CHANGE);
		if (updatableStatuses.contains(eStatus) && !leaveApplication.getStatus().equals(ELeaveStatus.PENDING)) {
			throw new BadRequestException("Can only be responded to if the status is 'pending'.");
		}
		leaveApplication.setStatus(eStatus);
		leaveApplication.setRespondedBy(userRepository.findById(principal.getId()).orElse(null));
		leaveApplication.setRespondedAt(LocalDateTime.now());

		leaveApplicationRepository.save(leaveApplication);

		return new MessageResponse("Leave application is " + status + " now.");
	}

	/**
	 * Service member cancel pending leave application
	 */
	@Override
	public MessageResponse cancelLeave(Long id, UserDetailsImpl principal) {
		LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Leave application not found"));

		// Can only be cancel if the status is 'pending'.
		if (!leaveApplication.getStatus().equals(ELeaveStatus.PENDING)) {
			throw new BadRequestException(" Can only be cancel if the status is 'pending'.");
		}
		int cancelDays = (int) IntStream.rangeClosed(0, (int) ChronoUnit.DAYS.between(leaveApplication.getStartDate(), leaveApplication.getEndDate()))
		        .mapToObj(leaveApplication.getStartDate()::plusDays)
		        .filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY)
		        .count();

		// Find user leave and update usedDays
		List<UserLeaveResponseDTO> userLeaves = userLeaveService.getUserLeaveForMember(leaveApplication.getLeaveType().getId(), principal);
		Collections.reverse(userLeaves);
		for (UserLeaveResponseDTO item : userLeaves) {
			if (item.getUsedDays() >= cancelDays) {
				// item.usedDays -= cancelDays
				userLeaveService.updateUsedDays(item.getId(), item.getUsedDays() - cancelDays);
				break;
			} else {
				cancelDays -= item.getUsedDays();
				// item.usedDays = 0
				userLeaveService.updateUsedDays(item.getId(), 0);
			}
		}
		
		leaveApplication.setStatus(ELeaveStatus.CANCELED);
		leaveApplicationRepository.save(leaveApplication);
		return new MessageResponse("Leave application is canceled");
	}

	@Override
	public MessageResponse updateLeaveApplication(Long id, LeaveApplicationRequestDTO request) {
		LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Leave application not found"));

		// Can only be update if the status is 'pending'.
		// ???????????????????????????????
		System.out.println(leaveApplication.getStatus().equals(ELeaveStatus.REQUESTED_CHANGE));
		if (!leaveApplication.getStatus().equals(ELeaveStatus.PENDING)
				|| !leaveApplication.getStatus().equals(ELeaveStatus.REQUESTED_CHANGE)) {
			throw new BadRequestException(" Can only be update if the status is PENDING or REQUESTED_CHANGE.");
		}
		LeaveType leaveType = leaveTypeResposity.findByIdAndDeletedAtIsNull(request.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("Leave type not exist"));

		// check điều kiện: số ngày không vượt quá

		// update usedDays

		leaveApplication.setLeaveType(leaveType);
		leaveApplication.setStartDate(request.getStartDate());
		leaveApplication.setEndDate(request.getEndDate());
		leaveApplication.setReason(request.getReason());
		leaveApplication.setStatus(ELeaveStatus.PENDING);
		leaveApplicationRepository.save(leaveApplication);

		return new MessageResponse("Leave application update success");
	}

}
