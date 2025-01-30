package musai.app.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.UserRequestDTO;
import musai.app.DTO.response.UserResponseDTO;
import musai.app.services.UserService;
import musai.app.validation.ValidationGroups;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/user")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * API get all user Only ADMIN can get all user
	 * 
	 * @return ResponseEntity
	 */
	@GetMapping("/list")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> getAllUser() {
		List<UserResponseDTO> response = userService.getAllUsers();
		return ResponseEntity.ok(response);
	}

	/**
	 * API add user Only ADMIN can add user
	 * 
	 * @paramater UserRequestDTO
	 * @return ResponseEntity
	 */
	@PostMapping("/add")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> addUser(
			@Validated(ValidationGroups.CreateUser.class) @RequestBody UserRequestDTO userRequestDTO) {
		MessageResponse response = userService.addUser(userRequestDTO);
		return ResponseEntity.ok(response);
	}

	/**
	 * API update user Only ADMIN can update user
	 * 
	 * @paramater UserRequestDTO
	 * @return ResponseEntity
	 */
	@PutMapping("/edit/{userId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> editUser(@PathVariable Long userId,
			@Validated @RequestBody UserRequestDTO userRequestDTO) {
		MessageResponse response = userService.editUser(userId, userRequestDTO);
		return ResponseEntity.ok(response);
	}

	/**
	 * API delete user Only ADMIN can delete user and they can't delete themself
	 * 
	 * @paramater UserRequestDTO
	 * @return ResponseEntity
	 */
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN') and #id != principal.id")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		MessageResponse response = userService.deleteUser(id);
		return ResponseEntity.ok(response);
	}
}
