package musai.app.services.impl;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveApplicationRequestDTO;
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

@Service
public class LeaveApplicationServiceImpl implements LeaveApplicationService {

	private LeaveApplicationRepository leaveApplicationRepository;
	private UserRepository userRepository;
	private LeaveTypeResposity leaveTypeResposity;

	public LeaveApplicationServiceImpl(LeaveApplicationRepository leaveApplicationRepository,
			UserRepository userRepository, LeaveTypeResposity leaveTypeResposity) {
		super();
		this.leaveApplicationRepository = leaveApplicationRepository;
		this.userRepository = userRepository;
		this.leaveTypeResposity = leaveTypeResposity;
	}

	/**
	 * Service appply leave application
	 */
	@Override
	public MessageResponse applyLeave(LeaveApplicationRequestDTO request) {
		User user = userRepository.findByIdAndDeletedAtIsNull(request.getUserId())
				.orElseThrow(() -> new NotFoundException("User not exist."));
		LeaveType leaveType = leaveTypeResposity.findByIdAndDeletedAtIsNull(request.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("Leave type not exist"));

		// check điều kiện: số ngày không vượt quá

		LeaveApplication leaveApplication = new LeaveApplication();
		leaveApplication.setUser(user);
		leaveApplication.setLeaveType(leaveType);
		leaveApplication.setStartDate(request.getStartDate());
		leaveApplication.setEndDate(request.getEndDate());
		leaveApplication.setReason(request.getReason());
		leaveApplication.setStatus(ELeaveStatus.PENDING.name());
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
		if (status.equals(ELeaveStatus.REVOKED.name())
				&& !leaveApplication.getStatus().equals(ELeaveStatus.APPROVED.name())) {
			throw new BadRequestException("Can only be revoked if the status is 'approved'.");
		}

		// Can only be revoked if the status is 'pending'.
		EnumSet<ELeaveStatus> updatableStatuses = EnumSet.of(ELeaveStatus.APPROVED, ELeaveStatus.REJECTED,
				ELeaveStatus.REQUESTED_CHANGE);
		if (updatableStatuses.contains(eStatus) && !leaveApplication.getStatus().equals(ELeaveStatus.PENDING.name())) {
			throw new BadRequestException("Can only be responded to if the status is 'pending'.");
		}
		leaveApplication.setStatus(status);
		leaveApplication.setRespondedBy(userRepository.findById(principal.getId()).orElse(null));
		leaveApplication.setRespondedAt(LocalDateTime.now());

		leaveApplicationRepository.save(leaveApplication);

		return new MessageResponse("Leave application is " + status + " now.");
	}

	@Override
	public MessageResponse cancelLeave(Long id) {
		LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Leave application not found"));

		// Can only be cancel if the status is 'pending'.
		if (!leaveApplication.getStatus().equals(ELeaveStatus.PENDING.name())) {
			throw new BadRequestException(" Can only be cancel if the status is 'pending'.");
		}
		leaveApplication.setStatus(ELeaveStatus.CANCELED.name());
		leaveApplicationRepository.save(leaveApplication);
		return new MessageResponse("Leave application is canceled");
	}

	@Override
	public MessageResponse updateLeaveApplication(Long id, LeaveApplicationRequestDTO request) {
		LeaveApplication leaveApplication = leaveApplicationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Leave application not found"));

		// Can only be update if the status is 'pending'. ???????????????????????????????
		System.out.println(leaveApplication.getStatus().equals(ELeaveStatus.REQUESTED_CHANGE.name()));
		if (!leaveApplication.getStatus().equals(ELeaveStatus.PENDING.name()) ||
				!leaveApplication.getStatus().equals(ELeaveStatus.REQUESTED_CHANGE.name())) {
			throw new BadRequestException(" Can only be update if the status is PENDING or REQUESTED_CHANGE.");
		}
		LeaveType leaveType = leaveTypeResposity.findByIdAndDeletedAtIsNull(request.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("Leave type not exist"));

		// check điều kiện: số ngày không vượt quá

		leaveApplication.setLeaveType(leaveType);
		leaveApplication.setStartDate(request.getStartDate());
		leaveApplication.setEndDate(request.getEndDate());
		leaveApplication.setReason(request.getReason());
		leaveApplication.setStatus(ELeaveStatus.PENDING.name());
		leaveApplicationRepository.save(leaveApplication);

		return new MessageResponse("Leave application update success");
	}

}
