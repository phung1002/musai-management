package musai.app.DTO.request;

import lombok.Data;

@Data
public class LoginRequest {
	private String employeeId;
	private String password;
}
