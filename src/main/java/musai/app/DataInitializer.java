package musai.app;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import musai.app.models.ELeaveValue;
import musai.app.models.ERole;
import musai.app.models.Employee;
import musai.app.models.LeaveType;
import musai.app.models.Role;
import musai.app.repositories.EmployeeRepository;
import musai.app.repositories.LeaveTypeRepository;
import musai.app.repositories.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

	private final RoleRepository roleRepository;
	private final EmployeeRepository employeeRepository;
	private final LeaveTypeRepository leaveTypeRepository;

	// Inject RoleRepository to save Role in DB
	public DataInitializer(RoleRepository roleRepository, EmployeeRepository employeeRepository,
			LeaveTypeRepository leaveTypeRepository) {
		this.roleRepository = roleRepository;
		this.employeeRepository = employeeRepository;
		this.leaveTypeRepository = leaveTypeRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		// Add roles if empty
		if (roleRepository.count() == 0) {
			roleRepository.save(new Role(ERole.ADMIN));
			roleRepository.save(new Role(ERole.MANAGER));
			roleRepository.save(new Role(ERole.MEMBER));
		}

		// Add users if empty
		if (employeeRepository.count() == 0) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String encodedPassword = encoder.encode("123456a@");

			Employee admin = new Employee("0000", "admin@gmail.com", encodedPassword, "Admin", "アドミン",
					LocalDate.of(1970, 1, 1), "管理", "本社", "08000000000", LocalDate.of(2023, 1, 1), "male");

			Role roleAdmin = roleRepository.findByName(ERole.ADMIN)
					.orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
			Role roleManagement = roleRepository.findByName(ERole.MANAGER)
					.orElseThrow(() -> new RuntimeException("Error: Role MANAGEMENT is not found."));
			Role roleMember = roleRepository.findByName(ERole.MEMBER)
					.orElseThrow(() -> new RuntimeException("Error: Role MEMBER is not found."));

			Set<Role> roles = new HashSet<>();
			roles.add(roleAdmin);
			roles.add(roleManagement);
			roles.add(roleMember);
			admin.setRoles(roles);
			employeeRepository.save(admin);
		}

		// Add leave type if empty
		if (leaveTypeRepository.count() == 0) {
			leaveTypeRepository.save(new LeaveType("有休", null, ELeaveValue.PAID_LEAVE.name()));
			leaveTypeRepository.save(new LeaveType("公休", null, ELeaveValue.PUBLIC_LEAVE.toString()));

			LeaveType paidLeave = leaveTypeRepository.findByName("有休");
			leaveTypeRepository.save(new LeaveType("半休", paidLeave, ELeaveValue.HALF_DAY.toString()));
			leaveTypeRepository.save(new LeaveType("全休", paidLeave, ELeaveValue.FULL_DAY.name()));

			LeaveType publicLeave = leaveTypeRepository.findByName("公休");
			leaveTypeRepository.save(new LeaveType("特別休暇", publicLeave, ELeaveValue.SPECIAL_LEAVE.name()));
			leaveTypeRepository.save(new LeaveType("年末年始", publicLeave, null));
			leaveTypeRepository.save(new LeaveType("慶弔休暇", publicLeave, null));

			LeaveType specialLeave = leaveTypeRepository.findByName("特別休暇");
			leaveTypeRepository.save(new LeaveType("BBQ", specialLeave, null));
			leaveTypeRepository.save(new LeaveType("健康診断", specialLeave, null));
			leaveTypeRepository.save(new LeaveType("夏季休暇", specialLeave, "SUMMER_DAY"));
			leaveTypeRepository.save(new LeaveType("社員旅行", specialLeave, null));

			LeaveType familyLeave = leaveTypeRepository.findByName("慶弔休暇");
			leaveTypeRepository.save(new LeaveType("本人が結婚", familyLeave, null));
			leaveTypeRepository.save(new LeaveType("子が結婚", familyLeave, null));
			leaveTypeRepository.save(new LeaveType("妻が出産", familyLeave, null));
			leaveTypeRepository.save(new LeaveType("父母、配偶者また子が死亡", familyLeave, null));
			leaveTypeRepository.save(new LeaveType("祖父母、配偶者父母、兄弟姉妹が死亡", familyLeave, null));
		}
	}
}