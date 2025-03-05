package musai.app.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@SQLRestriction("deleted_at IS NULL")
@Data
@NoArgsConstructor
@Entity
@Table(name = "user_leaves")
public class UserLeave {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id")
    private LeaveType leaveType;
    
    @Min(value = 0, message="Total days cannot be negative")
    @Column(name = "total_days")
    private Double totalDays;
    
    @Min(value = 0, message="Use days cannot be negative")
    @Column(name = "used_days")
    private Double usedDays;
    
    @Min(value = 0, message="Remaining days cannot be negative")
    @Column(name = "remained_days")
    private Double remainedDays;
    
    @JsonBackReference
    @Column(name = "valid_from")
    private LocalDate validFrom;
    
    @JsonBackReference
    @Column(name = "valid_to")
    private LocalDate validTo;
    
    @CreationTimestamp
    @JsonBackReference
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @JsonBackReference
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @SoftDelete
    @JsonBackReference
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Constructor
    public UserLeave(User userId, LeaveType leaveType, Double totalDays, Double usedDays ) {
        this.user = userId;
        this.leaveType = leaveType;
        this.totalDays = totalDays;
        this.usedDays = usedDays; 
    }
    
    public void recalculateRemainedDays() {
    	this.remainedDays = this.totalDays - this.usedDays;
    }
}