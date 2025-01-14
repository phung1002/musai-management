package musai.app.DTO;

import java.util.Set;
import jakarta.validation.constraints.*;
import lombok.Data;
import musai.app.validation.ValidationGroups;

@Data
public class UserDTO {
	@NotBlank(message = "Username cannot be blank")
	@Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
	private String username;

	@NotBlank(message = "Email cannot be blank")
	@Size(max = 50, message = "Email must be less than 50 characters")
	@Email(message = "Invalid email format")
	private String email;
	
	
	@NotBlank(groups = ValidationGroups.CreateUser.class, message = "Password cannot be blank")
	@Size(min = 6, max = 40, message = "Username must be between 6 and 40 characters")
	private String password;
	  
	private Set<String> roles;
	  
	private String fullname;

	private String department;
	
	private String position;
	
}