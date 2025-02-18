package musai.app.models;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "leave_applications")
public class LeaveApplication {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "leave_type_id", nullable = false)
	@JsonIgnore
	private LeaveType leaveType;

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;

	@Column(name = "reason", length = 500)
	private String reason;

	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "responded_at")
	private LocalDateTime respondedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responded_by")
	@JsonIgnore
	private User respondedBy;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@SoftDelete
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
