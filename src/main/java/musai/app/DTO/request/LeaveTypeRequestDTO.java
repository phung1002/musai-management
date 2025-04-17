package musai.app.DTO.request;

import lombok.Data;

@Data
public class LeaveTypeRequestDTO {
	private String name;
	private Long parentId; // Store only parent ID

	public LeaveTypeRequestDTO(String name, Long parentId) {
		this.name = name;
		this.parentId = parentId;
	}
	
	public Long getParentId() {
		return parentId;
	}
}
