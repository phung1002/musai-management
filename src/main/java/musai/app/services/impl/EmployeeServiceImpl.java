package musai.app.services.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import musai.app.DTO.request.ChangePasswordRequestDTO;
import musai.app.DTO.request.EmployeeRequestDTO;
import musai.app.DTO.response.MessageResponse;
import musai.app.DTO.response.EmployeeResponseDTO;
import musai.app.exception.BadRequestException;
import musai.app.exception.ForbiddenException;
import musai.app.exception.NotFoundException;
import musai.app.models.ERole;
import musai.app.models.Role;
import musai.app.models.Employee;
import musai.app.repositories.LeaveApplicationRepository;
import musai.app.repositories.RoleRepository;
import musai.app.repositories.EmployeeLeaveRepository;
import musai.app.repositories.EmployeeRepository;
import musai.app.security.services.UserDetailsImpl;
import musai.app.services.EmployeeService;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
	private final EmployeeRepository employeeRepository;
	private final EmployeeLeaveRepository employeeLeaveRepository;
	private final LeaveApplicationRepository leaveApplicationRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder encoder;

	/**
	 * Service get all employee and search
	 * 
	 * @return MessageResponse
	 */
	@Override
	public List<EmployeeResponseDTO> getEmployees(String keyword) {
		List<Employee> employees = StringUtils.hasText(keyword) ? employeeRepository.findActiveByKeyContaining(keyword)
				: employeeRepository.findAll();

		// If the list is empty, return an empty list
		if (employees.isEmpty()) {
			return Collections.emptyList();
		}

		// Map each User entity to UserResponseDTO using the convertToDTO method
		return employees.stream().map(this::convertToDTO) // Convert each employee to DTO
				.collect(Collectors.toList());
	}

	private EmployeeResponseDTO convertToDTO(Employee employee) {
		return new EmployeeResponseDTO(employee.getId(), employee.getEmployeeId(), employee.getEmail(),
				employee.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()),
				employee.getFullName(), employee.getFullNameFurigana(), employee.getBirthday(),
				employee.getDepartment(), employee.getWorkPlace(), employee.getMobile(), employee.getJoinDate(),
				employee.getGender());
	}

	/**
	 * Service add employee
	 * 
	 * @paramater UserRequestDTO
	 * @return MessageResponse add success/fail
	 */
	public MessageResponse createEmployee(EmployeeRequestDTO employeeRequestDTO) {
		if (employeeRepository.existsByEmployeeId(employeeRequestDTO.getEmployeeId())) {
			throw new BadRequestException("employee_id_already_exist");
		}

		if (employeeRepository.existsByEmail(employeeRequestDTO.getEmail())) {
			throw new BadRequestException("email_already_exists");
		}

		// Create new employee's account
		Employee employee = new Employee(employeeRequestDTO.getEmployeeId(), employeeRequestDTO.getEmail(),
				encoder.encode(employeeRequestDTO.getPassword()), employeeRequestDTO.getFullName(),
				employeeRequestDTO.getFullNameFurigana(), employeeRequestDTO.getBirthday(),
				employeeRequestDTO.getDepartment(), employeeRequestDTO.getWorkPlace(), employeeRequestDTO.getMobile(),
				employeeRequestDTO.getJoinDate(), employeeRequestDTO.getGender());

		Set<String> strRoles = employeeRequestDTO.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role memberRole = getRoleByName(ERole.MEMBER);
			roles.add(memberRole);
		} else {
			strRoles.forEach(role -> {
				ERole enumRole;
				try {
					enumRole = ERole.valueOf(role);
				} catch (IllegalArgumentException e) {
					throw new BadRequestException("role_invalid");
				}

				switch (enumRole) {
				case ADMIN:
					roles.add(getRoleByName(ERole.ADMIN));
					break;

				case MANAGER:
					roles.add(getRoleByName(ERole.MANAGER));
					break;

				default:
					roles.add(getRoleByName(ERole.MEMBER));
					break;
				}
			});
		}
		employee.setRoles(roles);
		employeeRepository.save(employee);// save to DB
		return new MessageResponse("add_success");
	}

	/**
	 * Service update employee
	 * 
	 * @paramater employeeId, UserRequestDTO
	 * @return MessageResponse update success/ fail
	 */
	@Override
	public MessageResponse updateEmployee(Long employeeId, EmployeeRequestDTO employeeRequestDTO,
			UserDetailsImpl principal) {
		// Find Employee by ID
		Employee existingUser = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new NotFoundException("employee_not_exist"));

		// Check employee name or email exist (unless belong to current employee)
		if (!existingUser.getEmployeeId().equals(employeeRequestDTO.getEmployeeId())
				&& employeeRepository.existsByEmployeeId(employeeRequestDTO.getEmployeeId())) {
			throw new BadRequestException("employee_id_already_exist");
		}

		if (!existingUser.getEmail().equals(employeeRequestDTO.getEmail())
				&& employeeRepository.existsByEmail(employeeRequestDTO.getEmail())) {
			throw new BadRequestException("email_already_exists");
		}

		// update information of employee
		existingUser.setEmployeeId(employeeRequestDTO.getEmployeeId());
		existingUser.setEmail(employeeRequestDTO.getEmail());
		existingUser.setFullName(employeeRequestDTO.getFullName());
		existingUser.setFullNameFurigana(employeeRequestDTO.getFullNameFurigana());
		existingUser.setBirthday(employeeRequestDTO.getBirthday());
		existingUser.setDepartment(employeeRequestDTO.getDepartment());
		existingUser.setWorkPlace(employeeRequestDTO.getWorkPlace());
		existingUser.setJoinDate(employeeRequestDTO.getJoinDate());
		existingUser.setGender(employeeRequestDTO.getGender());

		// update pass if request
		if (employeeRequestDTO.getPassword() != null && !employeeRequestDTO.getPassword().isEmpty()) {
			existingUser.setPassword(encoder.encode(employeeRequestDTO.getPassword()));
		}

		// update roles
		Set<String> strRoles = employeeRequestDTO.getRoles();
		Set<Role> roles = new HashSet<>();
		if (strRoles != null) {
			strRoles.forEach(role -> {
				ERole enumRole;
				try {
					enumRole = ERole.valueOf(role); // String to ERole
				} catch (IllegalArgumentException e) {
					throw new BadRequestException("role_invalid");
				}
				switch (enumRole) {
				case ADMIN:
					roles.add(getRoleByName(ERole.ADMIN));
					break;

				case MANAGER:
					roles.add(getRoleByName(ERole.MANAGER));
					break;

				default:
					roles.add(getRoleByName(ERole.MEMBER));
					break;
				}
			});
			if (isModifyingSelf(employeeId, principal) && !roles.contains(getRoleByName(ERole.ADMIN))) {
				throw new ForbiddenException("cannot_remove_own_admin_role");
			}
			existingUser.setRoles(roles);
		}
		// Save change
		employeeRepository.save(existingUser);

		return new MessageResponse("update_success");
	}

	/**
	 * Service delete employee
	 * 
	 * @paramater employeeId, UserRequestDTO
	 * @return MessageResponse update success/ fail
	 */
	@Override
	public MessageResponse deleteEmployee(Long employeeId, UserDetailsImpl principal) {
		if (isModifyingSelf(employeeId, principal)) {
			throw new ForbiddenException("cannot_delete_your_self");
		}
		Employee existingUser = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new NotFoundException("employee_not_exist"));
		// Check relationship
		if (employeeLeaveRepository.existsByEmployeeId(employeeId)) {
			throw new BadRequestException("cannot_delete_employee_have_leave");
		}
		if (leaveApplicationRepository.existsByEmployeeId(employeeId)) {
			throw new BadRequestException("cannot_delete_employee_have_request");
		}
		existingUser.setDeletedAt(LocalDateTime.now());
		employeeRepository.save(existingUser); // Update deleted_at
		return new MessageResponse("delete_success");
	}

	private boolean isModifyingSelf(Long id, UserDetailsImpl principal) {
		return id.equals(Long.valueOf(principal.getId()));
	}

	// Get infor of role by name
	private Role getRoleByName(ERole roleName) {
		return roleRepository.findByName(roleName).orElseThrow(() -> new BadRequestException("role_not_found"));
	}

	// Change password
	@Override
	public MessageResponse changePassword(ChangePasswordRequestDTO changePasswordRequestDTO,
			UserDetailsImpl principal) {
		Employee employee = employeeRepository.findById(principal.getId())
				.orElseThrow(() -> new NotFoundException("employee_not_exist"));

		if (!encoder.matches(changePasswordRequestDTO.getPassword(), employee.getPassword())) {

			throw new BadRequestException("current_password_incorect");
		}
		employee.setPassword(encoder.encode(changePasswordRequestDTO.getNewPassword()));
		employeeRepository.save(employee);

		return null;
	}

}
