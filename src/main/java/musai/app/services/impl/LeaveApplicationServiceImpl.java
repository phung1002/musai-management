package musai.app.services.impl;

import org.springframework.stereotype.Service;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.LeaveApplicationRequestDTO;
import musai.app.exception.NotFoundException;
import musai.app.models.LeaveApplication;
import musai.app.models.LeaveType;
import musai.app.models.User;
import musai.app.repositories.LeaveApplicationRepository;
import musai.app.repositories.LeaveTypeResposity;
import musai.app.repositories.UserRepository;
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
	public MessageResponse apply(LeaveApplicationRequestDTO request) {
		User user = userRepository.findByIdAndDeletedAtIsNull(request.getUserId())
				.orElseThrow(() -> new NotFoundException("User not exist."));
		LeaveType leaveType = leaveTypeResposity.findByIdAndDeletedAtIsNull(request.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("Leave type not exist"));
		
		//check điều kiện: số ngày không vượt quá
		
		LeaveApplication leaveApplication = new LeaveApplication();
		leaveApplication.setUser(user);
		leaveApplication.setLeaveType(leaveType);
		leaveApplication.setStartDate(request.getStartDate());
		leaveApplication.setEndDate(request.getEndDate());
		leaveApplication.setReason(request.getReason());
		leaveApplication.setStatus("pending");
		leaveApplicationRepository.save(leaveApplication);
		return new MessageResponse("Apply success");
	}
}
