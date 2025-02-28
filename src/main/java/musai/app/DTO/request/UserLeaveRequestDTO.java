package musai.app.DTO.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserLeaveRequestDTO {
	private Long Id;
    private Long userId;
    private Long leaveTypeId;
    private Double totalDays;
    private Double usedDays;
    private LocalDate validFrom;
    private LocalDate validTo;
    
}