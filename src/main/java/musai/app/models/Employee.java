package musai.app.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@SQLRestriction("deleted_at IS NULL")
@Table(name = "employees", uniqueConstraints = {  @UniqueConstraint(columnNames = "employee_id"), @UniqueConstraint(columnNames = "email") })
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "employee_id", length = 20)
	private String employeeId;

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
	private LocalDate birthday;

	@Column(name = "department")
	private String department;

	@Column(name = "work_place")
	private String workPlace;
	
	@Column(name = "mobile")
	private String mobile;

	@Column(name = "join_date")
	private LocalDate joinDate;

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
	@JoinTable(name = "employee_roles", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	public Employee(String employeeId, String email, String encode, String fullName, String fullNameFurigana,
			LocalDate birthday, String department, String workPlace, String mobile, LocalDate joinDate, String gender) {
		this.employeeId = employeeId;
		this.email = email;
		this.password = encode;
		this.fullName = fullName;
		this.fullNameFurigana = fullNameFurigana;
		this.birthday = birthday;
		this.department = department;
		this.workPlace = workPlace;
		this.mobile = mobile;
		this.joinDate = joinDate;
		this.gender = gender;
	}
}