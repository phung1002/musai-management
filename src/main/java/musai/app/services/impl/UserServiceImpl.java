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
		List<UserResponseDTO> lstUser = userRepository.findAllByDeletedAtIsNull().stream()
				.map(userResponseDTO -> new UserResponseDTO(userResponseDTO.getId(), userResponseDTO.getUsername(),
						userResponseDTO.getEmail(),
						userResponseDTO.getRoles().stream().map(role -> role.getName().name())
								.collect(Collectors.toSet()),
						userResponseDTO.getFullName(), userResponseDTO.getFullNameFurigana(),
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
			throw new BadRequestException("Username is already taken!");
		}

		if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
			throw new BadRequestException("Email is already in use!");
		}

		// Create new user's account
		User user = new User(userRequestDTO.getUsername(), userRequestDTO.getEmail(),
				encoder.encode(userRequestDTO.getPassword()), userRequestDTO.getFullName(),
				userRequestDTO.getFullNameFurigana(), userRequestDTO.getBirthday(), userRequestDTO.getDepartment(),
				userRequestDTO.getWorkPlace(), userRequestDTO.getJoinDate(), userRequestDTO.getGender());

		System.out.println(userRequestDTO);
		System.out.println(user);
		Set<String> strRoles = userRequestDTO.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role memberRole = roleRepository.findByName(ERole.MEMBER)
					.orElseThrow(() -> new BadRequestException("Role is not found."));
			roles.add(memberRole);
		} else {
			strRoles.forEach(role -> {
				ERole enumRole;
				try {
					enumRole = ERole.valueOf(role); // String to ERole
				} catch (IllegalArgumentException e) {
					throw new BadRequestException("Invalid role specified.");
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
	public MessageResponse editUser(Long userId, UserRequestDTO userRequestDTO, UserDetailsImpl principal) {
		// Find User by ID
		User existingUser = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new NotFoundException("User not exist."));

		// Check user name or email exist (unless belong to current user)
		if (!existingUser.getUsername().equals(userRequestDTO.getUsername())
				&& userRepository.existsByUsername(userRequestDTO.getUsername())) {
			throw new BadRequestException("Username is already taken!");
		}

		if (!existingUser.getEmail().equals(userRequestDTO.getEmail())
				&& userRepository.existsByEmail(userRequestDTO.getEmail())) {
			throw new BadRequestException("Email is already in use!");
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
					throw new BadRequestException("Invalid role specified.");
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
				throw new ForbiddenException("You are not allowed to remove your own ADMIN permissions.");
			}
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
	public MessageResponse deleteUser(Long userId, UserDetailsImpl principal) {
		if (isUserModifyingSelf(userId, principal)) {
			throw new ForbiddenException("Can not delete your self");
		}
		User existingUser = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new NotFoundException("User not exist."));

		existingUser.setDeletedAt(LocalDateTime.now());
		userRepository.save(existingUser); // Update deleted_at
		return new MessageResponse("User just deleted.");
	}

	private boolean isUserModifyingSelf(Long id, UserDetailsImpl principal) {
		return id.equals(Long.valueOf(principal.getId()));
	}

	/**
	 * Get infor of role by name
	 */
	private Role getRoleByName(ERole roleName) {
		return roleRepository.findByName(roleName)
				.orElseThrow(() -> new BadRequestException("Role " + roleName + " is not found."));
	}

	// get detail
	@Override
	public UserResponseDTO detailUser(Long userId) {
		User existingUser = userRepository.findByIdAndDeletedAtIsNull(userId)
				.orElseThrow(() -> new NotFoundException("User not exist."));

		return new UserResponseDTO(existingUser.getId(), existingUser.getUsername(), existingUser.getEmail(),
				existingUser.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()),
				existingUser.getFullName(), existingUser.getFullNameFurigana(), existingUser.getBirthday(),
				existingUser.getDepartment(), existingUser.getWorkPlace(), existingUser.getJoinDate(),
				existingUser.getGender());

	}

	// get search
	@Override
	public List<UserResponseDTO> searchUser(String keyword) {

		List<UserResponseDTO> filteredUser = userRepository.findAllByDeletedAtIsNull().stream()
				.filter(user -> user.getUsername().toLowerCase().contains(keyword.toLowerCase()))
				.map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(),
						user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()),
						user.getFullName(), user.getFullNameFurigana(), user.getBirthday(), user.getDepartment(),
						user.getWorkPlace(), user.getJoinDate(), user.getGender()

				)).collect(Collectors.toList());

		return filteredUser;

	}

}
