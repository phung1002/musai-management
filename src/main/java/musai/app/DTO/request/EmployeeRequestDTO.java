package musai.app.DTO.request;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import musai.app.validation.ValidationGroups;

@Data
public class EmployeeRequestDTO {

	@NotBlank(message = "Employee ID cannot be blank")
	@Pattern(regexp = "^\\d{4}$", message = "Employee ID must be 4 digits (e.g., 0001)")
	private String employeeId;

	@NotBlank(message = "Email cannot be blank")
	@Size(min = 5, max = 30, message = "Email must be beetwen 5 and 50 characters")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(groups = ValidationGroups.CreateEmployee.class, message = "Password cannot be blank")
//	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,40}$", message = "Password must be between 6 and 40 characters and contain at least one uppercase letter, one lowercase letter, one number, and one special character")
	private String password;

	@NotEmpty(message = "Roles cannot be empty")
	private Set<String> roles;

	@NotBlank(message = "Full Name cannot be blank")
	private String fullName;

	@NotBlank(message = "Full Name Furigana cannot be blank")
	private String fullNameFurigana;

	private LocalDate birthday;

	@NotBlank(message = "Department cannot be blank")
	private String department;

	@NotBlank(message = "Work Place cannot be blank")
	private String workPlace;
	
	@NotBlank(message = "Mobile cannot be blank")
	private String mobile;

	private LocalDate joinDate;

	@NotBlank(message = "Gender cannot be blank")
	private String gender;

}