package musai.app.DTO.request;

import java.time.LocalDateTime;
import java.util.Set;
import jakarta.validation.constraints.*;
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
	@Size(min = 6, max = 40, message = "Username must be between 6 and 40 characters")
//	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,40}$", message = "Password must be between 6 and 40 characters and contain at least one uppercase letter, one lowercase letter, one number, and one special character")
	private String password;

	@NotEmpty(message = "Roles cannot be empty")
	private Set<String> roles;

	private String fullName;

	private String department;

	private String workPlace;

	private LocalDateTime joinDate;

	private String gender;

	public UserRequestDTO(String username, String email, Set<String> roles, String fullName, String department,
			String workPlace, LocalDateTime joinDate, String gender) {
		this.username = username;
		this.email = email;
		this.roles = roles;
		this.fullName = fullName;
		this.department = department;
		this.workPlace = workPlace;
		this.joinDate = joinDate;
		this.gender = gender;
	}
}