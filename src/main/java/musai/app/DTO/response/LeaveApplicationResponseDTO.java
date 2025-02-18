package musai.app.DTO.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LeaveApplicationResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long leaveTypeId;
    private String leaveTypeName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reason;
    private String status;
    private LocalDateTime respondedAt;
    private Long respondedById;
    private String respondedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

