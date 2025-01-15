package musai.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import musai.app.DTO.MessageResponse;
import musai.app.DTO.UserDTO;
import musai.app.services.UserService;
import musai.app.validation.ValidationGroups;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
		}
	
	/**
	 * API add user
	 * Only ADMIN can add user
	 * 
	 * @paramater userDTO
	 * @return MessageResponse add success/ fail
	 */
	@PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
	public ResponseEntity<?> addUser(@Validated(ValidationGroups.CreateUser.class) @RequestBody UserDTO userDTO) {
		 MessageResponse response = userService.addUser(userDTO);
	        if (response.getMessage().startsWith("Error")) {
	            return ResponseEntity.badRequest().body(response);
	        }
	        return ResponseEntity.ok(response);
	}
	
	/**
	 * API update user
	 * Only ADMIN can update user
	 * 
	 * @paramater userDTO
	 * @return MessageResponse update success/ fail
	 */
	@PutMapping("/edit/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
	public ResponseEntity<?> editUser(@PathVariable Long userId, @Validated @RequestBody UserDTO userDTO) {
	    MessageResponse response = userService.editUser(userId, userDTO);
	    return ResponseEntity.ok(response);
	}
	
}
