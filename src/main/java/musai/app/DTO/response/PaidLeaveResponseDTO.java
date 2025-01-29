package musai.app.DTO.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import musai.app.models.PaidLeave;

@Data
@AllArgsConstructor
public class PaidLeaveResponseDTO {

	private Long id;

	private String name;

//	private PaidLeave parent;

	private List<PaidLeave> children;

}
