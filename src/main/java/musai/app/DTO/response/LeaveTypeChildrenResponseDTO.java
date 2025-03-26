package musai.app.DTO.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaveTypeChildrenResponseDTO {

	private Long id;

	private String name;

	private String value;

	private List<LeaveTypeChildrenResponseDTO> children;

	public LeaveTypeChildrenResponseDTO(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

}
