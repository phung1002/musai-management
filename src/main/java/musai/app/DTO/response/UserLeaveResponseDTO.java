package musai.app.DTO.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLeaveResponseDTO {
	private Long id;
    private String userFullName;
    private String leaveTypeName;
    private Integer totalDays;
    private Integer usedDays;
    private LocalDate validFrom;
    private LocalDate validTo;
}
