package musai.app.DTO.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserLeaveRequestDTO {
    private Long userId;
    private Long leaveTypeId;
    private Integer totalDays;
    private Integer usedDays;
    private LocalDate validFrom;
    private LocalDate validTo;
    
}