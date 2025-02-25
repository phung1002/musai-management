package musai.app.DTO.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import musai.app.models.ELeaveStatus;

@Data
@AllArgsConstructor
public class LeaveApplicationResponseDTO {
    private Long id;
    private String userFullName;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private ELeaveStatus status;
    private LocalDateTime respondedAt;
    private String respondedByFullName;
    private LocalDateTime createdAt;
}

