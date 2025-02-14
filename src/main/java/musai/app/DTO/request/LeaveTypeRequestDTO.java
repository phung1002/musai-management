package musai.app.DTO.request;

import lombok.Data;

@Data
public class LeaveTypeRequestDTO {
	private String name;
	private Long parent_id; // Store only parent ID

	public LeaveTypeRequestDTO(String name, Long parentId) {
		this.name = name;
		this.parent_id = parentId;
	}
	
	public Long getParentId() {
		return parent_id;
	}
}
