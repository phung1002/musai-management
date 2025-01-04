package musai.app.models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users", 
	uniqueConstraints = { 
		@UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email") 
	})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Column(name = "username", length = 20)
	private String username;
	
	@NotNull
	@Column(name = "email", length = 50)
	private String email;
	
	@NotNull
	@Column(name = "password", length = 120)
	private String password;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(	name = "user_roles", 
		joinColumns = @JoinColumn(name = "user_id"), 
		inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	@Column(name = "fullname")
	private String fullname;

	@Column(name = "department")
	private String department;

	@Column(name = "position")
	private String position;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	@SoftDelete
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public User(String username, String email, String encode, String fullname, String department, String position) {
		this.username = username;
		this.email = email;
		this.password = encode;
		this.fullname = fullname;
		this.department = department;
		this.position = position;
	}
}