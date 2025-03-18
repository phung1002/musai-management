package musai.app.DTO.request;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import musai.app.validation.ValidationGroups;

@Data
public class UserRequestDTO {

	@NotBlank(message = "Username cannot be blank")
	@Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
	private String username;

	@NotBlank(message = "Email cannot be blank")
	@Size(max = 50, message = "Email must be less than 50 characters")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(groups = ValidationGroups.CreateUser.class, message = "Password cannot be blank")
//	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,40}$", message = "Password must be between 6 and 40 characters and contain at least one uppercase letter, one lowercase letter, one number, and one special character")
	private String password;

	@NotEmpty(message = "Roles cannot be empty")
	private Set<String> roles;

	private String fullName;

	private String fullNameFurigana;

	private LocalDate birthday;

	private String department;

	private String workPlace;

	private LocalDate joinDate;

	private String gender;

}