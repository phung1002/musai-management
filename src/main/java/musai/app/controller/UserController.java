package musai.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import musai.app.DTO.MessageResponse;
import musai.app.DTO.request.ChangePasswordRequestDTO;
import musai.app.DTO.request.UserRequestDTO;
import musai.app.DTO.response.UserResponseDTO;
import musai.app.security.services.UserDetailsImpl;
import musai.app.services.UserService;
import musai.app.validation.ValidationGroups;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class UserController {
	private final UserService userService;

	/**
	 * API get all user Only ADMIN can get all user
	 * 
	 * @return ResponseEntity
	 */
	@GetMapping
	public ResponseEntity<?> getAllUser(@RequestParam String keyword) {
		List<UserResponseDTO> response = userService.getAllUsers(keyword);
		return ResponseEntity.ok(response);
	}

	/**
	 * API add user Only ADMIN can add user
	 * 
	 * @paramater UserRequestDTO
	 * @return ResponseEntity
	 */
	@PostMapping
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
	@PutMapping("/{userId}")
	public ResponseEntity<?> editUser(@PathVariable Long userId, @Validated @RequestBody UserRequestDTO userRequestDTO,
			@AuthenticationPrincipal UserDetailsImpl principal) {
		MessageResponse response = userService.editUser(userId, userRequestDTO, principal);
		return ResponseEntity.ok(response);
	}

	/**
	 * API delete user Only ADMIN can delete user and they can't delete themself
	 * 
	 * parameter UserRequestDTO
	 * @return ResponseEntity
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl principal) {
		MessageResponse response = userService.deleteUser(id, principal);
		return ResponseEntity.ok(response);
	}

	// Create User detail
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserDetail(@PathVariable Long id) {
		UserResponseDTO response = userService.detailUser(id);

		return ResponseEntity.ok(response);

	}

	/**
	 * API Change password 
	 * 
	 * parameter UserRequestDTO
	 * @return ResponseEntity
	 */
	@PutMapping("/change-password")
	public ResponseEntity<?> changePassword( @Validated @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO,
			@AuthenticationPrincipal UserDetailsImpl principal) {
		MessageResponse response = userService.changePassword(changePasswordRequestDTO, principal);
		return ResponseEntity.ok(response);
	}
}
