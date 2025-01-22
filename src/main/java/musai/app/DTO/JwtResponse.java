package musai.app.DTO;

import java.util.List;

import lombok.Data;

@Data
public class JwtResponse {
	private Long id;
	private String username;
	private String email;
	private List<String> roles;
	private boolean isAuthenticated;
	
	public JwtResponse(Long id, String username, String email, List<String> roles, boolean isAuthenticated) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
		this.isAuthenticated = isAuthenticated;
		}
}