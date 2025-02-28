package musai.app.services.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.UserLeaveRequestDTO;
import musai.app.DTO.response.UserLeaveResponseDTO;
import musai.app.DTO.response.UserLeaveResponseDTO2;
import musai.app.exception.NotFoundException;
import musai.app.models.LeaveType;
import musai.app.models.User;
import musai.app.models.UserLeave;
import musai.app.repositories.LeaveTypeResposity;
import musai.app.repositories.UserLeaveRepository;
import musai.app.repositories.UserRepository;
import musai.app.security.services.UserDetailsImpl;
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

	/**
	 * Service get list user leave for member
	 */
	@Override
	public List<UserLeaveResponseDTO> getUserLeaveForMember(Long leaveTypeId, UserDetailsImpl principal) {

		LocalDate today = LocalDate.now();

		// get user leave of user logging in + filter user leave is valid today
		// (validFrom < today < validTo)
		List<UserLeave> userLeaves = userLeaveRepository.findByUserId(principal.getId()).stream()
				.filter(userLeave -> !userLeave.getValidFrom().isAfter(today) 
						&& !userLeave.getValidTo().isBefore(today)
//						&& (userLeave.getTotalDays() - userLeave.getUsedDays() > 0)
						&& (leaveTypeId == null || userLeave.getLeaveType().getId().equals(leaveTypeId)))
				.sorted(Comparator.comparing(UserLeave::getValidTo))
				.collect(Collectors.toList());

		List<UserLeaveResponseDTO> responseDTO = userLeaves.stream().map(this::convertToDTO)
				.collect(Collectors.toList());
		System.err.println(responseDTO);
		return responseDTO;
	}

	private UserLeaveResponseDTO convertToDTO(UserLeave userLeave) {
		return new UserLeaveResponseDTO(userLeave.getId(), userLeave.getUser().getFullName(),
				userLeave.getLeaveType().getName(), userLeave.getTotalDays(), userLeave.getUsedDays(),
				userLeave.getValidFrom(), userLeave.getValidTo());
	}
	

	@Override
	public MessageResponse updateUsedDays(Long id, int usedDay) {
		UserLeave userLeave = userLeaveRepository.findById(id).orElseThrow(() 
				-> new NotFoundException("User Leave not found"));
		userLeave.setUsedDays(usedDay);
		return new MessageResponse("User Leave update usedDays successful.");
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

	// update user_leaves
	public UserLeave editUserLeave(UserLeaveRequestDTO userLeaveRequestDTO) {
		// Fetch exiting UserLeave
		UserLeave existingUserLeave = userLeaveRepository.findById(userLeaveRequestDTO.getId())
	            .orElseThrow(() -> new NotFoundException("UserLeave not found"));
		// userId
		User existingUser = userRepository.findByIdAndDeletedAtIsNull(userLeaveRequestDTO.getUserId())
				.orElseThrow(() -> new NotFoundException("User not exist."));
		// leaveTypeId
		LeaveType leaveType = leaveTypeResposity.findByIdAndDeletedAtIsNull(userLeaveRequestDTO.getLeaveTypeId())
				.orElseThrow(() -> new NotFoundException("Error: LeaveType not found"));
		
		// update information of user
		existingUserLeave.setUser(existingUser);
		existingUserLeave.setLeaveType(leaveType);
		existingUserLeave.setTotalDays(userLeaveRequestDTO.getTotalDays());
		existingUserLeave.setUsedDays(userLeaveRequestDTO.getUsedDays());
		existingUserLeave.setValidFrom(userLeaveRequestDTO.getValidFrom());
		existingUserLeave.setValidTo(userLeaveRequestDTO.getValidTo());

		return userLeaveRepository.save(existingUserLeave);
	}

	//List All
	@Override
	
    public List<UserLeaveResponseDTO2> getAllUserLeaves() {
        LocalDate today = LocalDate.now();
        
        List<UserLeave> userLeaves = userLeaveRepository.findAll()
            .stream()
            .filter(userLeave -> !userLeave.getValidFrom().isAfter(today)
                && !userLeave.getValidTo().isBefore(today))
            .sorted(Comparator.comparing(UserLeave::getValidTo))
            .collect(Collectors.toList());

        return userLeaves.stream()
            .map(this::convertToDTOAll)
            .collect(Collectors.toList());
    }

    private UserLeaveResponseDTO2 convertToDTOAll (UserLeave userLeave) {
        return UserLeaveResponseDTO2.builder()
                .id(userLeave.getId())
                .userId(userLeave.getUser().getId())
                .userName(userLeave.getUser().getUsername())
                .leaveTypeId(userLeave.getLeaveType().getId())
                .leaveTypeName(userLeave.getLeaveType().getName())
                .totalDays(userLeave.getTotalDays())
                .usedDays(userLeave.getUsedDays())
                .validFrom(userLeave.getValidFrom())
                .validTo(userLeave.getValidTo())
                .build();
	}
    
  //Search
    @Override
    public List<UserLeaveResponseDTO2> searchUserLeaves(String keyword) {
        LocalDate today = LocalDate.now();
        
        // First, find users matching the keyword
        List<Long> matchingUserIds = userRepository.findAllByDeletedAtIsNull().stream()
                .filter(user -> user.getUsername().toLowerCase().contains(keyword.toLowerCase()) 
                        || (user.getFullName() != null && user.getFullName().toLowerCase().contains(keyword.toLowerCase())))
                .map(User::getId)
                .collect(Collectors.toList());
        
        // Then find all user leaves for these users
        List<UserLeave> userLeaves = userLeaveRepository.findAll().stream()
                .filter(userLeave -> 
                        matchingUserIds.contains(userLeave.getUser().getId()) &&
                        !userLeave.getValidFrom().isAfter(today) && 
                        !userLeave.getValidTo().isBefore(today))
                .sorted(Comparator.comparing(UserLeave::getValidTo))
                .collect(Collectors.toList());
        
        // Convert to DTOs
        return userLeaves.stream()
                .map(this::convertToDTOAll)
                .collect(Collectors.toList());
    }
}


