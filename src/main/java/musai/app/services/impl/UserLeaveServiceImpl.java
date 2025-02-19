package musai.app.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import musai.app.DTO.request.UserLeaveRequestDTO;
import musai.app.exception.NotFoundException;
import musai.app.models.LeaveType;
import musai.app.models.User;
import musai.app.models.UserLeave;
import musai.app.repositories.LeaveTypeResposity;
import musai.app.repositories.UserLeaveRepository;
import musai.app.repositories.UserRepository;
import musai.app.services.UserLeaveService;

@Service
public class UserLeaveServiceImpl implements UserLeaveService {

	@Autowired
	private UserLeaveRepository userLeaveRepository;
	private UserRepository userRepository;
	private LeaveTypeResposity leaveTypeResposity;

	public UserLeaveServiceImpl(UserLeaveRepository userLeaveRepository, UserRepository userRepository,
			LeaveTypeResposity leaveTypeResposity) {
		super();
		this.userLeaveRepository = userLeaveRepository;
		this.userRepository = userRepository;
		this.leaveTypeResposity = leaveTypeResposity;
	}

	// Create new leave user_leaves
	public UserLeave createUserLeave(UserLeaveRequestDTO userLeaveRequestDTO) {
		// userId
		User existingUser = userRepository.findByIdAndDeletedAtIsNull(userLeaveRequestDTO.getUserId())
				.orElseThrow(() -> new NotFoundException("User not exist."));

		// leaveTypeId
		LeaveType leaveType = leaveTypeResposity.findByIdAndDeletedAtIsNull(userLeaveRequestDTO.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("Error: LeaveType not found"));

		UserLeave userLeave = new UserLeave();

		userLeave.setUser(existingUser);
		userLeave.setLeaveType(leaveType);
		userLeave.setTotalDays(userLeaveRequestDTO.getTotalDays());

		if (userLeaveRequestDTO.getUsedDays() == null) {
			userLeave.setUsedDays(0);
		} else {
			userLeave.setUsedDays(userLeaveRequestDTO.getUsedDays());
		}

		userLeave.setValidFrom(userLeaveRequestDTO.getValidFrom());
		userLeave.setValidTo(userLeaveRequestDTO.getValidTo());

		return userLeaveRepository.save(userLeave);
	}

}
