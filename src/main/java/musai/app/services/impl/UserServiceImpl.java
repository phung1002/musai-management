package musai.app.services.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import musai.app.DTO.request.ChangePasswordRequestDTO;
import musai.app.DTO.request.UserRequestDTO;
import musai.app.DTO.response.MessageResponse;
import musai.app.DTO.response.UserResponseDTO;
import musai.app.exception.BadRequestException;
import musai.app.exception.ForbiddenException;
import musai.app.exception.NotFoundException;
import musai.app.models.ERole;
import musai.app.models.Role;
import musai.app.models.User;
import musai.app.repositories.RoleRepository;
import musai.app.repositories.UserRepository;
import musai.app.security.services.UserDetailsImpl;
import musai.app.services.UserService;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder encoder;
	
	/**
	 * Service get all user
	 * 
	 * @return MessageResponse
	 */
	@Override
	public List<UserResponseDTO> getAllUsers(String keyword) {
		List<User> users = StringUtils.hasText(keyword)
				? userRepository.findActiveByKeyContaining(keyword)
				: userRepository.findAll();

		// If the list is empty, return an empty list
		if (users.isEmpty()) {
			return Collections.emptyList();
		}

		// Map each User entity to UserResponseDTO using the convertToDTO method
		return users.stream().map(this::convertToDTO) // Convert each user to DTO
				.collect(Collectors.toList());
	}

	private UserResponseDTO convertToDTO(User user) {
		return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(),
				user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()),
				user.getFullName(), user.getFullNameFurigana(), user.getBirthday(), user.getDepartment(),
				user.getWorkPlace(), user.getJoinDate(), user.getGender());
	}

	/**
	 * Service add user
	 * 
	 * @paramater UserRequestDTO
	 * @return MessageResponse add success/fail
	 */
	public MessageResponse addUser(UserRequestDTO userRequestDTO) {
		if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
			throw new BadRequestException("username_already_exist");
		}

		if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
			throw new BadRequestException("email_already_exists");
		}

		// Create new user's account
		User user = new User(userRequestDTO.getUsername(), userRequestDTO.getEmail(),
				encoder.encode(userRequestDTO.getPassword()), userRequestDTO.getFullName(),
				userRequestDTO.getFullNameFurigana(), userRequestDTO.getBirthday(), userRequestDTO.getDepartment(),
				userRequestDTO.getWorkPlace(), userRequestDTO.getJoinDate(), userRequestDTO.getGender());

		Set<String> strRoles = userRequestDTO.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role memberRole = getRoleByName(ERole.MEMBER);
			roles.add(memberRole);
		} else {
			strRoles.forEach(role -> {
				ERole enumRole;
				try {
					enumRole = ERole.valueOf(role); // String to ERole
				} catch (IllegalArgumentException e) {
					throw new BadRequestException("role_invalid");
				}

				switch (enumRole) {
				case ADMIN:
					roles.add(getRoleByName(ERole.ADMIN));
					break;

				case MANAGER:
					roles.add(getRoleByName(ERole.MANAGER));
					break;

				default:
					roles.add(getRoleByName(ERole.MEMBER));
					break;
				}
			});
		}
		user.setRoles(roles);
		userRepository.save(user);// save to DB
		return new MessageResponse("add_success");
	}

	/**
	 * Service update user
	 * 
	 * @paramater userId, UserRequestDTO
	 * @return MessageResponse update success/ fail
	 */
	@Override
	public MessageResponse editUser(Long userId, UserRequestDTO userRequestDTO, UserDetailsImpl principal) {
		// Find User by ID
		User existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("user_not_exist"));

		// Check user name or email exist (unless belong to current user)
		if (!existingUser.getUsername().equals(userRequestDTO.getUsername())
				&& userRepository.existsByUsername(userRequestDTO.getUsername())) {
			throw new BadRequestException("username_already_exist");
		}

		if (!existingUser.getEmail().equals(userRequestDTO.getEmail())
				&& userRepository.existsByEmail(userRequestDTO.getEmail())) {
			throw new BadRequestException("email_already_exists");
		}

		// update information of user
		existingUser.setUsername(userRequestDTO.getUsername());
		existingUser.setEmail(userRequestDTO.getEmail());
		existingUser.setFullName(userRequestDTO.getFullName());
		existingUser.setFullNameFurigana(userRequestDTO.getFullNameFurigana());
		existingUser.setBirthday(userRequestDTO.getBirthday());
		existingUser.setDepartment(userRequestDTO.getDepartment());
		existingUser.setWorkPlace(userRequestDTO.getWorkPlace());
		existingUser.setJoinDate(userRequestDTO.getJoinDate());
		existingUser.setGender(userRequestDTO.getGender());

		// update pass if request
		if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
			existingUser.setPassword(encoder.encode(userRequestDTO.getPassword()));
		}

		// update roles
		Set<String> strRoles = userRequestDTO.getRoles();
		Set<Role> roles = new HashSet<>();
		if (strRoles != null) {
			strRoles.forEach(role -> {
				ERole enumRole;
				try {
					enumRole = ERole.valueOf(role); // String to ERole
				} catch (IllegalArgumentException e) {
					throw new BadRequestException("role_invalid");
				}
				switch (enumRole) {
				case ADMIN:
					roles.add(getRoleByName(ERole.ADMIN));
					break;

				case MANAGER:
					roles.add(getRoleByName(ERole.MANAGER));
					break;

				default:
					roles.add(getRoleByName(ERole.MEMBER));
					break;
				}
			});
			if (isUserModifyingSelf(userId, principal) && !roles.contains(getRoleByName(ERole.ADMIN))) {
				throw new ForbiddenException("cannot_remove_own_admin_role");
			}
			existingUser.setRoles(roles);
		}
		// Save change
		userRepository.save(existingUser);

		return new MessageResponse("update_success");
	}

	/**
	 * Service delete user
	 * 
	 * @paramater userId, UserRequestDTO
	 * @return MessageResponse update success/ fail
	 */
	@Override
	public MessageResponse deleteUser(Long userId, UserDetailsImpl principal) {
		if (isUserModifyingSelf(userId, principal)) {
			throw new ForbiddenException("cannot_delete_your_self");
		}
		User existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("user_not_exist"));

		existingUser.setDeletedAt(LocalDateTime.now());
		userRepository.save(existingUser); // Update deleted_at
		return new MessageResponse("delete_success");
	}

	private boolean isUserModifyingSelf(Long id, UserDetailsImpl principal) {
		return id.equals(Long.valueOf(principal.getId()));
	}

	/**
	 * Get infor of role by name
	 */
	private Role getRoleByName(ERole roleName) {
		return roleRepository.findByName(roleName).orElseThrow(() -> new BadRequestException("role_not_found"));
	}

	// get detail
	@Override
	public UserResponseDTO detailUser(Long userId) {
		User existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("user_not_exist"));

		return new UserResponseDTO(existingUser.getId(), existingUser.getUsername(), existingUser.getEmail(),
				existingUser.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()),
				existingUser.getFullName(), existingUser.getFullNameFurigana(), existingUser.getBirthday(),
				existingUser.getDepartment(), existingUser.getWorkPlace(), existingUser.getJoinDate(),
				existingUser.getGender());

	}

	@Override
	public MessageResponse changePassword(ChangePasswordRequestDTO changePasswordRequestDTO,
			UserDetailsImpl principal) {
		User user = userRepository.findById(principal.getId())
				.orElseThrow(() -> new NotFoundException("user_not_exist"));

		if (!encoder.matches(changePasswordRequestDTO.getPassword(), user.getPassword())) {

			throw new BadRequestException("current_password_incorect");
		}
		user.setPassword(encoder.encode(changePasswordRequestDTO.getNewPassword()));
		userRepository.save(user);

		return null;
	}

}
