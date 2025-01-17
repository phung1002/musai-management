package musai.app.controller.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UpdatePaidLeaveRequest {
	
	@NotBlank(message = "Id cannot be blank")
	private Long id;
	
	@NotBlank(message = "Name cannot be blank")
	private String name;

}
