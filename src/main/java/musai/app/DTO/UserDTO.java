package musai.app.DTO;

import java.util.Set;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDTO {
	@NotBlank
	@Size(min = 3, max = 20)
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;
	
	@NotBlank
	@Size(min = 6, max = 40)
	private String password;
	  
	private Set<String> roles;
	  
	private String fullname;

	private String department;
	
	private String position;
	
}