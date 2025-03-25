package musai.app.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
	private Long id;
	private String username;
	private String email;
	private List<String> roles;
	private String fullName;
	private String gender;

}