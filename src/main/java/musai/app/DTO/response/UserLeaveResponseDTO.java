package musai.app.DTO.response;

import lombok.Data;

@Data
public class UserLeaveResponseDTO {
	private Long id;
	private UserLeave user;

@Data
	public static class UserLeave {
		private Long id;
		private String username;
	}

}
