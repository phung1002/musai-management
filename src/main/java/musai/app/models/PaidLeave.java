package musai.app.models;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

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
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@NoArgsConstructor
@Entity
@Table(name = "pay_leaves")
public class PaidLeave {

	public PaidLeave(String name, PaidLeave parent, List<PaidLeave> children) {
		this.name = name;
		this.parent = parent;
		this.children = children;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name") // Assuming name refers to the type of leave (e.g., Vacation, Sick Leave)
	private String name;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
	@JsonBackReference
    private PaidLeave parent;
	
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	@JsonManagedReference
    private List<PaidLeave> children;

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
