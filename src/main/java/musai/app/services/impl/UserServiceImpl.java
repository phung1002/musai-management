package musai.app.services.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.UserRequestDTO;
import musai.app.DTO.response.UserResponseDTO;
import musai.app.exception.BadRequestException;
import musai.app.exception.NotFoundException;
import musai.app.models.ERole;
import musai.app.models.Role;
import musai.app.models.User;
import musai.app.repositories.RoleRepository;
import musai.app.repositories.UserRepository;
import musai.app.services.UserService;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder encoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.encoder = encoder;
	}

	/**
	 * Service get all user
	 * 
	 * @return MessageResponse
	 */
	@Override
	public List<UserResponseDTO> getAllUsers() {
		List<UserResponseDTO> lstUser = userRepository.findAll().stream()
				.map(userResponseDTO -> new UserResponseDTO(userResponseDTO.getId(), userResponseDTO.getUsername(),
						userResponseDTO.getEmail(), userResponseDTO.getRoles().stream() // change Set<Role> ->
																						// Set<String>
								.map(role -> role.getName().name()) // get name of role (ERole)
								.collect(Collectors.toSet()),
						userResponseDTO.getFullName(), userResponseDTO.getFullNameFufigana(),
						userResponseDTO.getBirthday(), userResponseDTO.getDepartment(), userResponseDTO.getWorkPlace(),
						userResponseDTO.getJoinDate(), userResponseDTO.getGender()))
				.collect(Collectors.toList());
		return lstUser;
	}

	/**
	 * Service add user
	 * 
	 * @paramater UserRequestDTO
	 * @return MessageResponse add success/fail
	 */
	public MessageResponse addUser(UserRequestDTO userRequestDTO) {
		if (userRepository.existsByUsername(userRequestDTO.getUsername())) {
			throw new BadRequestException("Error: Username is already taken!");
		}

		if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
			throw new BadRequestException("Error: Email is already in use!");
		}

		// Create new user's account
		User user = new User(userRequestDTO.getUsername(), userRequestDTO.getEmail(),
				encoder.encode(userRequestDTO.getPassword()), userRequestDTO.getFullName(),
				userRequestDTO.getFullNameFufigana(), userRequestDTO.getBirthday(), userRequestDTO.getDepartment(),
				userRequestDTO.getWorkPlace(), userRequestDTO.getJoinDate(), userRequestDTO.getGender());

		Set<String> strRoles = userRequestDTO.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role memberRole = roleRepository.findByName(ERole.MEMBER)
					.orElseThrow(() -> new BadRequestException("Error: Role is not found."));
			roles.add(memberRole);
		} else {
			strRoles.forEach(role -> {
				ERole enumRole;
				try {
					enumRole = ERole.valueOf(role); // String to ERole
				} catch (IllegalArgumentException e) {
					throw new BadRequestException("Error: Invalid role specified.");
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
		return new MessageResponse("User registered successfully!");
	}

	/**
	 * Service update user
	 * 
	 * @paramater userId, UserRequestDTO
	 * @return MessageResponse update success/ fail
	 */
	@Override
	public MessageResponse editUser(Long userId, UserRequestDTO userRequestDTO) {
		// Find User by ID
		User existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Error: User not exist."));
		// Check user deleted
		if (existingUser.getDeletedAt() != null) {
			throw new NotFoundException("Error: User not exist.");
		}

		// Check user name or email exist (unless belong to current user)
		if (!existingUser.getUsername().equals(userRequestDTO.getUsername())
				&& userRepository.existsByUsername(userRequestDTO.getUsername())) {
			throw new BadRequestException("Error: Username is already taken!");
		}

		if (!existingUser.getEmail().equals(userRequestDTO.getEmail())
				&& userRepository.existsByEmail(userRequestDTO.getEmail())) {
			throw new BadRequestException("Error: Email is already in use!");
		}

		// update information of user
		existingUser.setUsername(userRequestDTO.getUsername());
		existingUser.setEmail(userRequestDTO.getEmail());
		existingUser.setFullName(userRequestDTO.getFullName());
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
					throw new BadRequestException("Error: Invalid role specified.");
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
			existingUser.setRoles(roles);
		}
		// Save change
		userRepository.save(existingUser);

		return new MessageResponse("User updated successfully!");
	}

	/**
	 * Service delete user
	 * 
	 * @paramater userId, UserRequestDTO
	 * @return MessageResponse update success/ fail
	 */
	@Override
	public MessageResponse deleteUser(Long userId) {
		User existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Error: User not exist."));

		if (existingUser.getDeletedAt() != null) {
			throw new NotFoundException("Error: User not exist.");
		}
		existingUser.setDeletedAt(LocalDateTime.now());
		userRepository.save(existingUser); // Update deleted_at
		return new MessageResponse("User just deleted.");
	}

	/**
	 * Get infor of role by name
	 */
	private Role getRoleByName(ERole roleName) {
		return roleRepository.findByName(roleName)
				.orElseThrow(() -> new BadRequestException("Error: Role " + roleName + " is not found."));
	}
}
