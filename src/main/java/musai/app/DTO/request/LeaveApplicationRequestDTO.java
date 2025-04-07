package musai.app.DTO.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveApplicationRequestDTO {
	
	@NotNull(message = "Employee ID is required")
    private Long employeeId; 
	
	@NotNull(message = "Leave type ID is required")
    private Long leaveTypeId; 
	
    @NotNull(message = "Start date is required")
    private LocalDate startDate; 

    @NotNull(message = "End date is required")
    private LocalDate endDate; 

    @NotBlank(message = "Reason is required")
    private String reason;
}
