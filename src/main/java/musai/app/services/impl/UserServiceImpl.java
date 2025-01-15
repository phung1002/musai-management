package musai.app.services.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import musai.app.DTO.MessageResponse;
import musai.app.DTO.UserDTO;
import musai.app.exception.BadRequestException;
import musai.app.exception.UserNotFoundException;
import musai.app.models.ERole;
import musai.app.models.Role;
import musai.app.models.User;
import musai.app.repositories.RoleRepository;
import musai.app.repositories.UserRepository;
import musai.app.services.UserService;

@Service
public class UserServiceImpl implements UserService{
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
	 * Service add user
	 * 
	 * @paramater userDTO
	 * @return MessageResponse add success/fail
	 */
	public MessageResponse addUser(UserDTO userDTO) {
		if (userRepository.existsByUsername(userDTO.getUsername())) {
			throw new BadRequestException("Error: Username is already taken!");
		}

		if (userRepository.existsByEmail(userDTO.getEmail())) {
			throw new BadRequestException("Error: Email is already in use!");
		}

		// Create new user's account
		User user = new User(
				userDTO.getUsername(),
				userDTO.getEmail(),
				encoder.encode(userDTO.getPassword()),
				userDTO.getFullname(),
				userDTO.getDepartment(),
				userDTO.getPosition()
		);

		Set<String> strRoles = userDTO.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role memberRole = roleRepository.findByName(ERole.ROLE_MEMBER)
					.orElseThrow(() -> new BadRequestException("Error: Role is not found."));
			roles.add(memberRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
					case "admin":
						Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
								.orElseThrow(() -> new BadRequestException("Error: Role is not found."));
						roles.add(adminRole);
						break;

					case "manager":
						Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
								.orElseThrow(() -> new BadRequestException("Error: Role is not found."));
						roles.add(managerRole);
						break;

					default:
						Role userRole = roleRepository.findByName(ERole.ROLE_MEMBER)
								.orElseThrow(() -> new BadRequestException("Error: Role is not found."));
						roles.add(userRole);
				}
			});
		}
		user.setRoles(roles);
		userRepository.save(user);
		return new MessageResponse("User registered successfully!");
	}

	/**
	 * Service update user
	 * 
	 * @paramater userId, userDTO
	 * @return MessageResponse update success/ fail
	 */
	@Override
	public MessageResponse editUser(Long userId, UserDTO userDTO) {
	    // Find User by ID
	    User existingUser = userRepository.findById(userId)
	            .orElseThrow(() -> new UserNotFoundException("Error: User not exist."));
	    //Check user deleted
	    if (existingUser.getDeletedAt() != null) {
            throw new UserNotFoundException("Error: User not exist.");
        }
	    
	    // Check user name or email exist (unless belong to current user)
	    if (!existingUser.getUsername().equals(userDTO.getUsername()) &&
	        userRepository.existsByUsername(userDTO.getUsername())) {
	        throw new BadRequestException("Error: Username is already taken!");
	    }

	    if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
	        userRepository.existsByEmail(userDTO.getEmail())) {
	        throw new BadRequestException("Error: Email is already in use!");
	    }

	    // update information of user
	    existingUser.setUsername(userDTO.getUsername());
	    existingUser.setEmail(userDTO.getEmail());
	    existingUser.setFullname(userDTO.getFullname());
	    existingUser.setDepartment(userDTO.getDepartment());
	    existingUser.setPosition(userDTO.getPosition());

	    // update pass if request
	    if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
	        existingUser.setPassword(encoder.encode(userDTO.getPassword()));
	    }

	    // update roles
	    Set<String> strRoles = userDTO.getRoles();
	    Set<Role> roles = new HashSet<>();

	    if (strRoles != null) {
	        strRoles.forEach(role -> {
	            switch (role) {
	                case "admin":
	                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
	                            .orElseThrow(() -> new BadRequestException("Error: Role not found."));
	                    roles.add(adminRole);
	                    break;

	                case "manager":
	                    Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
	                            .orElseThrow(() -> new BadRequestException("Error: Role not found."));
	                    roles.add(managerRole);
	                    break;

	                default:
	                    Role userRole = roleRepository.findByName(ERole.ROLE_MEMBER)
	                            .orElseThrow(() -> new BadRequestException("Error: Role not found."));
	                    roles.add(userRole);
	            }
	        });
	        existingUser.setRoles(roles);
	    }

	    // Save change
	    userRepository.save(existingUser);

	    return new MessageResponse("User updated successfully!");
	}

	@Override
	public MessageResponse deleteUser(Long userId) {
	    User existingUser = userRepository.findById(userId)
	            .orElseThrow(() -> new UserNotFoundException("Error: User not exist."));
	    
        if (existingUser.getDeletedAt() != null) {
            throw new UserNotFoundException("Error: User already deleted.");
        }
        existingUser.setDeletedAt(LocalDateTime.now());
        userRepository.save(existingUser); // Update deleted_at
        return new MessageResponse("User just deleted.");
	}
}
