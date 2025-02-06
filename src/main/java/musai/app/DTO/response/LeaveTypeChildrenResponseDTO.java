package musai.app.DTO.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import musai.app.models.LeaveType;

@Data
@AllArgsConstructor
public class LeaveTypeChildrenResponseDTO {

	private Long id;

	private String name;

	private List<LeaveType> children;

}
