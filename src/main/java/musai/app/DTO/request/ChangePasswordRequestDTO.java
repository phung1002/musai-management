package musai.app.DTO.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePasswordRequestDTO {
	
	@NotEmpty(message = "Password cannot be empty")
	private String password;
	
	@NotEmpty(message = "newPassword cannot be empty")
	private String newPassword;
}
