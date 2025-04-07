package musai.app.DTO.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EmployeeLeaveRequestDTO {
    private Long employeeId;
    private Long leaveTypeId;
    private Double totalDays;
    private Double usedDays;
    private Double remainedDays;
    private LocalDate validFrom;
    private LocalDate validTo;
    
}