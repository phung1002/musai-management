package musai.app.services.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import musai.app.DTO.request.UserLeaveRequestDTO;
import musai.app.DTO.response.UserLeaveResponseDTO;
import musai.app.exception.NotFoundException;
import musai.app.models.LeaveType;
import musai.app.models.User;
import musai.app.models.UserLeave;
import musai.app.repositories.LeaveTypeResposity;
import musai.app.repositories.UserLeaveRepository;
import musai.app.repositories.UserRepository;
import musai.app.services.UserLeaveService;

@Service
@AllArgsConstructor
public class UserLeaveServiceImpl implements UserLeaveService {
	private final UserLeaveRepository userLeaveRepository;
	@Autowired
	private final UserRepository userRepository;
	@Autowired
	private final LeaveTypeResposity leaveTypeResposity;
 
	// List All
	@Override
	public List<UserLeaveResponseDTO> getAllUserLeaves(String keyword) {
		List<UserLeave> userLeaves = StringUtils.hasText(keyword)
				? userLeaveRepository.findActiveByKeyContaining(keyword)
				: userLeaveRepository.findAllActive();
		return userLeaves.stream().map(this::convertToDTOAll).collect(Collectors.toList());
	}

	private UserLeaveResponseDTO convertToDTOAll(UserLeave userLeave) {
		return UserLeaveResponseDTO.builder().id(userLeave.getId()).userId(userLeave.getUser().getId())
				.userFullName(userLeave.getUser().getFullName()).leaveTypeId(userLeave.getLeaveType().getId())
				.leaveTypeName(userLeave.getLeaveType().getName()).leaveTypeValue(userLeave.getLeaveType().getValue())
				.totalDays(userLeave.getTotalDays()).usedDays(userLeave.getUsedDays())
				.remainedDays(userLeave.getRemainedDays()).validFrom(userLeave.getValidFrom())
				.validTo(userLeave.getValidTo()).build();
	}

	// Service get list user leave with leaveTypeId, userId
	@Override
	public List<UserLeaveResponseDTO> getUserLeaveForMember(Long leaveTypeId, Long userId) {
		LocalDate today = LocalDate.now();
		// get user leave of user logging in + filter user leave is valid today
		// (validFrom < today < validTo)
		List<UserLeave> userLeaves = userLeaveRepository.findByUserId(userId).stream()
				.filter(userLeave -> (userLeave.getRemainedDays() > 0) && !userLeave.getValidFrom().isAfter(today)
						&& !userLeave.getValidTo().isBefore(today)
						&& (leaveTypeId == null || userLeave.getLeaveType().getId().equals(leaveTypeId)))
				.sorted(Comparator.comparing(UserLeave::getValidTo)).collect(Collectors.toList());
		return userLeaves.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	// Service get list user leave for member to show in screen
	@Override
	public List<UserLeaveResponseDTO> getUserLeaveForMember(Long userId) {
		LocalDate today = LocalDate.now();
		List<UserLeave> userLeaves = userLeaveRepository.findByUserId(userId).stream().filter(
				userLeave -> !userLeave.getValidFrom().isAfter(today) && !userLeave.getValidTo().isBefore(today))
				.sorted(Comparator.comparing(UserLeave::getValidTo)).collect(Collectors.toList());
		List<UserLeaveResponseDTO> responseDTO = userLeaves.stream().map(this::convertToDTO)
				.collect(Collectors.toList());
		return responseDTO;
	}

	private UserLeaveResponseDTO convertToDTO(UserLeave userLeave) {
		return new UserLeaveResponseDTO(userLeave.getId(), userLeave.getUser().getId(),
				userLeave.getUser().getFullName(), userLeave.getLeaveType().getId(), userLeave.getLeaveType().getName(),
				userLeave.getLeaveType().getValue(), userLeave.getTotalDays(), userLeave.getUsedDays(),
				userLeave.getRemainedDays(), userLeave.getValidFrom(), userLeave.getValidTo());
	}

	@Override
	public void updateUsedDaysRemainedDays(Long id, double usedDay) {
		UserLeave userLeave = userLeaveRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("user_leave_not_found"));
		userLeave.setUsedDays(usedDay);
		userLeave.setRemainedDays(userLeave.getTotalDays() - usedDay);
	}

	// Create new leave user_leaves
	public UserLeave createUserLeave(UserLeaveRequestDTO userLeaveRequestDTO) {
		// userId
		User existingUser = userRepository.findByIdAndDeletedAtIsNull(userLeaveRequestDTO.getUserId())
				.orElseThrow(() -> new NotFoundException("user_not_exist"));
		// leaveTypeId
		LeaveType leaveType = leaveTypeResposity.findByIdAndDeletedAtIsNull(userLeaveRequestDTO.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("leave_type_not_found"));

		UserLeave userLeave = new UserLeave();

		userLeave.setUser(existingUser);
		userLeave.setLeaveType(leaveType);
		userLeave.setTotalDays(userLeaveRequestDTO.getTotalDays());
		userLeave.setUsedDays(0.0);

		// calculate remainedDays
		userLeave.setRemainedDays(userLeave.getTotalDays());

		userLeave.setValidFrom(userLeaveRequestDTO.getValidFrom());
		userLeave.setValidTo(userLeaveRequestDTO.getValidTo());

		return userLeaveRepository.save(userLeave);
	}

	// update user_leaves
	public UserLeave editUserLeave(UserLeaveRequestDTO userLeaveRequestDTO) {
		// Fetch exiting UserLeave
		UserLeave existingUserLeave = userLeaveRepository.findById(userLeaveRequestDTO.getId())
				.orElseThrow(() -> new NotFoundException("user_leave_not_found"));
		// userId
		User existingUser = userRepository.findByIdAndDeletedAtIsNull(userLeaveRequestDTO.getUserId())
				.orElseThrow(() -> new NotFoundException("user_not_exist"));
		// leaveTypeId
		LeaveType leaveType = leaveTypeResposity.findByIdAndDeletedAtIsNull(userLeaveRequestDTO.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("leave_type_not_found"));
		// update information of user
		existingUserLeave.setUser(existingUser);
		existingUserLeave.setLeaveType(leaveType);
		existingUserLeave.setTotalDays(userLeaveRequestDTO.getTotalDays());
		existingUserLeave.setUsedDays(userLeaveRequestDTO.getUsedDays());

		// recalculate remainedDays
		existingUserLeave.setRemainedDays(userLeaveRequestDTO.getTotalDays() - userLeaveRequestDTO.getUsedDays());
		existingUserLeave.setValidFrom(userLeaveRequestDTO.getValidFrom());
		existingUserLeave.setValidTo(userLeaveRequestDTO.getValidTo());

		return userLeaveRepository.save(existingUserLeave);
	}
}
