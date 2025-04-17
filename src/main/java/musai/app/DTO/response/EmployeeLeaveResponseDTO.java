package musai.app.DTO.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EmployeeLeaveResponseDTO {
	private Long id;
	private Long employeeId;
    private String employeeFullName;
    private Long leaveTypeId;  
    private String leaveTypeName;
    private String leaveTypeValue;
    private Double totalDays;
    private Double usedDays;
    private Double remainedDays;
    private LocalDate validFrom;
    private LocalDate validTo;
}
