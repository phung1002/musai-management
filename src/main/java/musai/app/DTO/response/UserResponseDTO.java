package musai.app.DTO.response;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {

	private Long id;

	private String username;

	private String email;

	private Set<String> roles;

	private String fullName;

	private String fullNameFurigana;

	private LocalDateTime birthday;

	private String department;

	private String workPlace;

	private LocalDateTime joinDate;

	private String gender;

}