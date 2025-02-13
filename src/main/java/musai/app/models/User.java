package musai.app.models;

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
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email") })
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

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "full_name_furigana")
	private String fullNameFurigana;

	@Column(name = "birthday")
	private LocalDateTime birthday;

	@Column(name = "department")
	private String department;

	@Column(name = "work_place")
	private String workPlace;

	@Column(name = "join_date")
	private LocalDateTime joinDate;

	@Column(name = "gender")
	private String gender;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@SoftDelete
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	public User(String username, String email, String encode, String fullName, String fullNameFurigana,
			LocalDateTime birthday, String department, String workPlace, LocalDateTime joinDate, String gender) {
		this.username = username;
		this.email = email;
		this.password = encode;
		this.fullName = fullName;
		this.fullNameFurigana = fullNameFurigana;
		this.birthday = birthday;
		this.department = department;
		this.workPlace = workPlace;
		this.joinDate = joinDate;
		this.gender = gender;
	}
}