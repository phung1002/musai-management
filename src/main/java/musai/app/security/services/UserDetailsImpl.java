package musai.app.security.services;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import musai.app.models.Employee;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String employeeId;
	private String email;
	@JsonIgnore
	private String password;
	private String fullName;
	private String gender;
	private LocalDateTime deletedAt;
	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Long id, String employeeId, String email, String password, String fullName, String gender,
			LocalDateTime deletedAt, Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.employeeId = employeeId;
		this.email = email;
		this.password = password;
		this.deletedAt = deletedAt;
		this.authorities = authorities;
		this.fullName = fullName;
		this.gender = gender;
	}

	public static UserDetailsImpl build(Employee employee) {
		List<GrantedAuthority> authorities = employee.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

		return new UserDetailsImpl(employee.getId(), employee.getEmployeeId(), employee.getEmail(), employee.getPassword(), employee.getFullName(), employee.getGender(),
				employee.getDeletedAt(), authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public String getFullName() {
		return fullName;
	}

	public String getGender() {
		return gender;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public String getUsername() {
		return employeeId;
	}
}