package musai.app.DTO.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

@AllArgsConstructor
public class UserLeaveResponseDTO {
	private Long id;
	//private Long userId;
    private String userFullName;
    private String leaveTypeName;
    private Double totalDays;
    private Double usedDays;
    private LocalDate validFrom;
    private LocalDate validTo;
}
