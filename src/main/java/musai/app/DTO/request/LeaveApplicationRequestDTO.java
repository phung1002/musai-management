package musai.app.DTO.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveApplicationRequestDTO {
	
//	@NotNull(message = "User ID is required")
    private Long userId; 
	
	@NotNull(message = "Leave type ID is required")
    private Long leaveTypeId; 
	
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDateTime startDate; 

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDateTime endDate; 

    @NotBlank(message = "Reason is required")
    private String reason;
}
