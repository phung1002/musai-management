package musai.app;

import musai.app.models.ELeaveValue;
import musai.app.models.ERole;
import musai.app.models.LeaveType;
import musai.app.models.Role;
import musai.app.models.Employee;
import musai.app.repositories.LeaveTypeResposity;
import musai.app.repositories.RoleRepository;
import musai.app.repositories.EmployeeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

	private final RoleRepository roleRepository;
	private final EmployeeRepository employeeRepository;
	private final LeaveTypeResposity leaveTypeResposity;

	// Inject RoleRepository to save Role in DB
	public DataInitializer(RoleRepository roleRepository, EmployeeRepository employeeRepository,
			LeaveTypeResposity leaveTypeResposity) {
		this.roleRepository = roleRepository;
		this.employeeRepository = employeeRepository;
		this.leaveTypeResposity = leaveTypeResposity;
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
			String encodedPassword = encoder.encode("admin");

			Employee admin = new Employee("0000", "admin@gmail.com", encodedPassword, "Admin", "アドミン",
					LocalDate.of(1990, 1, 1), "管理", "本社", "080000000", LocalDate.of(2023, 1, 1), "male");

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

			// management
			Employee user1 = new Employee("0002", "nguyen@gmail.com", encoder.encode("nguyen"), "Nguyen Khanh Phung",
					"グエンカンプン", LocalDate.of(1997, 2, 10), "IT", "本社","080000222", LocalDate.of(2024, 5, 1), "female");
			Employee user2 = new Employee("0003", "chamith@gmail.com", encoder.encode("chamith"), "Chamith", "チャミット",
					LocalDate.of(1994, 1, 1), "IT", "本社","080000333", LocalDate.of(2024, 12, 1), "male");
			Employee user3 = new Employee("0004", "hoang@gmail.com", encoder.encode("hoang"), "Tran Kim Hoang", "チャミット",
					LocalDate.of(1991, 1, 1), "IT", "本社", "080000444", LocalDate.of(2024, 12, 1), "female");
			roles.remove(roleAdmin);
			user1.getRoles().add(roleManagement);
			user2.getRoles().add(roleManagement);
			user3.getRoles().add(roleManagement);
			employeeRepository.save(user1);
			employeeRepository.save(user2);
			employeeRepository.save(user3);

			// member
			for (int i = 1; i <= 9; i++) {
				Employee employee = createUser(i, encoder);
				employee.getRoles().add(roleMember);
				employeeRepository.save(employee);
			}
		}

		// Add leave type if empty
		if (leaveTypeResposity.count() == 0) {
			leaveTypeResposity.save(new LeaveType("有休", null, ELeaveValue.PAID_LEAVE.name()));
			leaveTypeResposity.save(new LeaveType("公休", null, ELeaveValue.PUBLIC_LEAVE.toString()));

			LeaveType paidLeave = leaveTypeResposity.findByName("有休");
			leaveTypeResposity.save(new LeaveType("半休", paidLeave, ELeaveValue.HALF_DAY.toString()));
			leaveTypeResposity.save(new LeaveType("全休", paidLeave, ELeaveValue.FULL_DAY.name()));

			LeaveType publicLeave = leaveTypeResposity.findByName("公休");
			leaveTypeResposity.save(new LeaveType("特別休暇", publicLeave, ELeaveValue.SPECIAL_LEAVE.name()));
			leaveTypeResposity.save(new LeaveType("年末年始", publicLeave, null));
			leaveTypeResposity.save(new LeaveType("慶弔休暇", publicLeave, null));

			LeaveType specialLeave = leaveTypeResposity.findByName("特別休暇");
			leaveTypeResposity.save(new LeaveType("BBQ", specialLeave, null));
			leaveTypeResposity.save(new LeaveType("健康診断", specialLeave, null));
			leaveTypeResposity.save(new LeaveType("夏季休暇", specialLeave, "SUMMER_DAY"));
			leaveTypeResposity.save(new LeaveType("社員旅行", specialLeave, null));

			LeaveType familyLeave = leaveTypeResposity.findByName("慶弔休暇");
			leaveTypeResposity.save(new LeaveType("本人が結婚する", familyLeave, null));
			leaveTypeResposity.save(new LeaveType("子が結婚する", familyLeave, null));
			leaveTypeResposity.save(new LeaveType("妻が出産する", familyLeave, null));
			leaveTypeResposity.save(new LeaveType("父母、配偶者また子が死亡した", familyLeave, null));
			leaveTypeResposity.save(new LeaveType("祖父母、配偶者父母、兄弟姉妹が死亡した", familyLeave, null));
		}
	}

	public static Employee createUser(int i, BCryptPasswordEncoder encoder) {
		String employeeId = "001" + i;
		String email = "user" + i + "@gmail.com";
		String password = encoder.encode("user" + i);

		return new Employee(employeeId, email, password, "User " + i, generateRandomHiragana(5),
				LocalDate.of(1994, 1, 1), "役職 " + i, "支店 " + i, "070" + String.valueOf(i).repeat(6),
				LocalDate.of(2025, 1, 1), "male");
	}

	private static final int HIRAGANA_START = 0x3040;
	private static final int HIRAGANA_END = 0x309F;

	public static String generateRandomHiragana(int length) {
		Random random = new Random();
		StringBuilder hiraganaString = new StringBuilder();

		for (int i = 0; i < length; i++) {
			int randomCodePoint = HIRAGANA_START + random.nextInt(HIRAGANA_END - HIRAGANA_START + 1);
			hiraganaString.append((char) randomCodePoint);
		}

		return hiraganaString.toString();
	}
}