package musai.app.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaveTypeParentResponseDTO {

	private Long id;

	private String name;

	private Long parentId;

}
