package musai.app.DTO.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import musai.app.models.PaidLeave;

@Data
@AllArgsConstructor
public class PaidLeaveRequestDTO {

	private Long id;
	
	private String name;
	
	private PaidLeave parent;
	
    private List<PaidLeave> children;

}
