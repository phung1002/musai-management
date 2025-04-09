package musai.app.DTO.response;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {

	private Long id;

	private String employeeId;

	private String email;

	private Set<String> roles;

	private String fullName;

	private String fullNameFurigana;

	private LocalDate birthday;

	private String department;

	private String workPlace;
	
	private String mobile;

	private LocalDate joinDate;

	private String gender;

}