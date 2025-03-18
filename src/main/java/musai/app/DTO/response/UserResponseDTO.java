package musai.app.DTO.response;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

	private Long id;

	private String username;

	private String email;

	private Set<String> roles;

	private String fullName;

	private String fullNameFurigana;

	private LocalDate birthday;

	private String department;

	private String workPlace;

	private LocalDate joinDate;

	private String gender;

}