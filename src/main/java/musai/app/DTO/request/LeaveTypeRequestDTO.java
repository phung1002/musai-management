package musai.app.DTO.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import musai.app.models.LeaveType;

@Data
public class LeaveTypeRequestDTO {
	private String name;
	private Long parentId; // Store only parent ID

	public LeaveTypeRequestDTO(String name, Long parentId) {
		this.name = name;
		this.parentId = parentId;
	}
}
