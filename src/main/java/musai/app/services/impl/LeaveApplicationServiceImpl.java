package musai.app.services.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import musai.app.DTO.request.LeaveApplicationRequestDTO;
import musai.app.DTO.response.LeaveApplicationResponseDTO;
import musai.app.DTO.response.MessageResponse;
import musai.app.DTO.response.UserLeaveResponseDTO;
import musai.app.exception.BadRequestException;
import musai.app.exception.NotFoundException;
import musai.app.models.ELeaveStatus;
import musai.app.models.ELeaveValue;
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
@RequiredArgsConstructor
public class LeaveApplicationServiceImpl implements LeaveApplicationService {

	private final LeaveApplicationRepository leaveApplicationRepository;
	@Autowired
	private final UserRepository userRepository;
	@Autowired
	private final LeaveTypeResposity leaveTypeResposity;
	@Autowired
	private final UserLeaveService userLeaveService;

	/**
	 * Service get all leave applications
	 */
	@Override
	public List<LeaveApplicationResponseDTO> getAllLeaveApplications(String keyword) {
	    List<LeaveApplication> leaveApplications = StringUtils.hasText(keyword)
	        ? leaveApplicationRepository.findActiveByKeywordContaining(keyword)
	        : leaveApplicationRepository.findAllActive();

	    return leaveApplications.stream()
	        .map(this::convertToDTO)
	        .collect(Collectors.toList());
	}

	// Convert LeaveApplication to LeaveApplicationResponseDTO
	private LeaveApplicationResponseDTO convertToDTO(LeaveApplication leaveApplication) {
		return new LeaveApplicationResponseDTO(leaveApplication.getId(),
				Optional.ofNullable(leaveApplication.getUser()).map(User::getFullName).orElse("Unknown"),
				leaveApplication.getLeaveType().getId(),
				Optional.ofNullable(leaveApplication.getLeaveType()).map(LeaveType::getName).orElse("Unknown"),
				Optional.ofNullable(leaveApplication.getLeaveType()).map(LeaveType::getValue).orElse("Unknown"),
				leaveApplication.getStartDate(), leaveApplication.getEndDate(), leaveApplication.getReason(),
				leaveApplication.getStatus(), leaveApplication.getRespondedAt(),
				Optional.ofNullable(leaveApplication.getRespondedBy()).map(User::getFullName).orElse(null),
				leaveApplication.getCreatedAt());
	}

	/**
	 * Service get leave applications of user are logging in
	 */
	@Override
	public List<LeaveApplicationResponseDTO> getLeaveApplicationsForMember(UserDetailsImpl principal, String keyword) {
		// Find leave application of user are logging in
		List<LeaveApplication> leaveApplications = StringUtils.hasText(keyword)
		        ? leaveApplicationRepository.findActiveByUserIdByKeywordContaining(principal.getId(), keyword)
		        : leaveApplicationRepository.findActiveByUserId(principal.getId());

		    return leaveApplications.stream()
		        .map(this::convertToDTO)
		        .collect(Collectors.toList());
	}

	/**
	 * Service appply leave application
	 */
	@Override
	public MessageResponse applyLeave(LeaveApplicationRequestDTO request, UserDetailsImpl principal) {

		User user = userRepository.findById(principal.getId())
				.orElseThrow(() -> new NotFoundException("user_not_exist"));
		LeaveType leaveType = leaveTypeResposity.findById(request.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("leave_type_not_exist"));
		if (leaveType.getValue() != null) {
			// check condition: remainDays > requestDays
			List<UserLeaveResponseDTO> userLeaves = getUserLeaveForMember(leaveType, principal.getId());
			double remainingDays = calculateRemainingDays(userLeaves);
			// count request days
			double requestDays = calculateLeaveDays(leaveType, request.getStartDate(), request.getEndDate());
			if( requestDays == 0) {
				throw new BadRequestException("requested_day_unavailble");
			}
			if (requestDays > remainingDays) {
				throw new BadRequestException("requested_days_exceed");
			}
			// update usedDays to UserLeave
			updateUsedDaysRemainedDays(userLeaves, requestDays, false);
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
		LeaveApplication application = leaveApplicationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("leave_application_not_exist"));
		// Check status is valid
		if (!ELeaveStatus.isValidStatus(status)) {
			throw new BadRequestException("invalid_status");
		}
		ELeaveStatus eStatus = ELeaveStatus.valueOf(status.toUpperCase());
		// Can only be revoked if the status is 'approved'.
		if (status.equals(ELeaveStatus.REVOKED.name()) && !application.getStatus().equals(ELeaveStatus.APPROVED)) {
			throw new BadRequestException("can_only_be_revoked_if_approved");
		}
		// Can only be revoked if the status is 'pending'.
		EnumSet<ELeaveStatus> updatableStatuses = EnumSet.of(ELeaveStatus.APPROVED, ELeaveStatus.REJECTED,
				ELeaveStatus.REQUESTED_CHANGE);
		if (updatableStatuses.contains(eStatus) && !application.getStatus().equals(ELeaveStatus.PENDING)) {
			throw new BadRequestException("can_only_be_cancel_or_response_if_pending");
		}
		// case REJECTED, REVOKED: update usedDays
		if (status.equals(ELeaveStatus.REJECTED.name()) || status.equals(ELeaveStatus.REVOKED.name())) {
			LeaveType leaveType = leaveTypeResposity.findById(application.getLeaveType().getId())
					.orElseThrow(() -> new NotFoundException("leave_type_not_exist"));
			List<UserLeaveResponseDTO> userLeaves = getUserLeaveForMember(leaveType, application.getUser().getId());
			double cancelDays = calculateLeaveDays(leaveType, application.getStartDate(), application.getEndDate());
			updateUsedDaysRemainedDays(userLeaves, cancelDays, true);
		}
		application.setStatus(eStatus);
		application.setRespondedBy(userRepository.findById(principal.getId()).orElse(null));
		application.setRespondedAt(LocalDateTime.now());
		leaveApplicationRepository.save(application);
		return new MessageResponse("Leave application is " + status + " now.");
	}

	/**
	 * Service member cancel pending leave application
	 */
	@Override
	public MessageResponse cancelLeave(Long id, UserDetailsImpl principal) {
		LeaveApplication application = leaveApplicationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("leave_application_not_exist"));
		LeaveType leaveType = leaveTypeResposity.findById(application.getLeaveType().getId())
				.orElseThrow(() -> new NotFoundException("leave_tpye_not_exist"));
		// Can only be cancel if the status is 'pending'.
		if (!application.getStatus().equals(ELeaveStatus.PENDING)) {
			throw new BadRequestException("can_only_be_cancel_or_response_if_pending");
		}
		if (leaveType.getValue() != null) {
			// count cancel days
			double cancelDays = calculateLeaveDays(leaveType, application.getStartDate(), application.getEndDate());
			// Find user leave and update usedDays
			List<UserLeaveResponseDTO> userLeaves = getUserLeaveForMember(leaveType, principal.getId());
			Collections.reverse(userLeaves);
			updateUsedDaysRemainedDays(userLeaves, cancelDays, true);
			
		}
		application.setStatus(ELeaveStatus.CANCELED);
		leaveApplicationRepository.save(application);
		return new MessageResponse("cancel_success");
	}

	@Override
	public MessageResponse updateLeaveApplication(Long id, LeaveApplicationRequestDTO request,
			UserDetailsImpl principal) {
		LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("leave_application_not_exist"));
		// Can only be update if the status is 'PENDING' or 'REQUESTED_CHANGE'
		if (!(leaveApplication.getStatus().equals(ELeaveStatus.PENDING)
				|| leaveApplication.getStatus().equals(ELeaveStatus.REQUESTED_CHANGE))) {
			throw new BadRequestException("can_only_be_update_if_pending_or_request_change");
		}
		LeaveType leaveType = leaveTypeResposity.findById(request.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("leave_type_not_exist"));
		// case not change leaveType
		if (leaveType.getValue() != null) {
			// check condition: remainDays > requestDays
			List<UserLeaveResponseDTO> userLeaves = getUserLeaveForMember(leaveType, principal.getId());
			double oldRequestDays = calculateLeaveDays(leaveType,leaveApplication.getStartDate(), leaveApplication.getEndDate());
			double remainingDays = calculateRemainingDays(userLeaves);
			double requestDays = calculateLeaveDays(leaveType, request.getStartDate(), request.getEndDate());
			if( requestDays == 0) {
				throw new BadRequestException("requested_day_unavailble");
			}
			if (requestDays > remainingDays + oldRequestDays) {
				throw new BadRequestException("requested_days_exceed");
			}
			// update usedDays to UserLeave
			if (requestDays < oldRequestDays) {
				Collections.reverse(userLeaves);
				updateUsedDaysRemainedDays(userLeaves, oldRequestDays - requestDays, true);
			}
			if (requestDays > oldRequestDays) {
				updateUsedDaysRemainedDays(userLeaves, requestDays - oldRequestDays, false);
			}
		}
		
		leaveApplication.setLeaveType(leaveType);
		leaveApplication.setStartDate(request.getStartDate());
		leaveApplication.setEndDate(request.getEndDate());
		leaveApplication.setReason(request.getReason());
		leaveApplication.setStatus(ELeaveStatus.PENDING);
		leaveApplicationRepository.save(leaveApplication);
		return new MessageResponse("update_success");
	}

	private void updateUsedDaysRemainedDays(List<UserLeaveResponseDTO> userLeaves, double requestDays, boolean isDecrease) {
		
		for (UserLeaveResponseDTO item : userLeaves) {
			double availableDays = isDecrease ? item.getUsedDays() : item.getTotalDays() - item.getUsedDays();
			if (availableDays >= requestDays) {
				double newUsedDays = isDecrease ? item.getUsedDays() - requestDays : item.getUsedDays() + requestDays;
				userLeaveService.updateUsedDaysRemainedDays(item.getId(), newUsedDays);
				break;
			} else {
				requestDays -= availableDays;
				userLeaveService.updateUsedDaysRemainedDays(item.getId(), isDecrease ? 0 : item.getTotalDays());
			}
		}
	}

	private long countDays(LocalDate startDate, LocalDate endDate) {
		return IntStream.rangeClosed(0, (int) ChronoUnit.DAYS.between(startDate, endDate)).mapToObj(startDate::plusDays)
				.filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY)
				.count();
	}

	private double calculateRemainingDays(List<UserLeaveResponseDTO> userLeaves) {
		return userLeaves.stream().mapToDouble(userLeave -> userLeave.getRemainedDays()).sum();
	}

	private double calculateLeaveDays(LeaveType leaveType, LocalDate startDate, LocalDate endDate) {
		String leaveValue = leaveType.getValue() != null ? leaveType.getValue() : "";
		return leaveValue.equals(ELeaveValue.HALF_DAY.name()) ? 0.5 : countDays(startDate, endDate);
	}

	private List<UserLeaveResponseDTO> getUserLeaveForMember(LeaveType leaveType, Long userId) {
		String leaveValue = leaveType.getValue() != null ? leaveType.getValue() : "";
		return (leaveValue.equals(ELeaveValue.HALF_DAY.name())
				|| leaveValue.equals(ELeaveValue.FULL_DAY.name()))
						? userLeaveService.getUserLeaveForMember(leaveType.getParent().getId(), userId)
						: userLeaveService.getUserLeaveForMember(leaveType.getId(), userId);
	}

	@Override
	public List<LeaveApplicationResponseDTO> getApprovedLeaveApplications() {
		List<LeaveApplication> leaveApplications =  leaveApplicationRepository.getApprovedLeaveApplications();

		    return leaveApplications.stream()
		        .map(this::convertToDTO)
		        .collect(Collectors.toList());
	}
}
