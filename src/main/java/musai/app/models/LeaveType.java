package musai.app.models;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@SQLRestriction("deleted_at IS NULL")
@Table(name = "leave_types")
public class LeaveType {

	public LeaveType(String name, LeaveType parent, String value) {
		this.name = name;
		this.parent = parent;
		this.value = value;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name") // Assuming name refers to the type of leave (e.g., Vacation, Sick Leave)
	private String name;
	
	@Column(name = "value")
    private String value; 
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
	@JsonIgnore
    private LeaveType parent;
	
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	@JsonIgnore
    private List<LeaveType> children;

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

}
