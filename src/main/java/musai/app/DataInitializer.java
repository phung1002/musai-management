package musai.app;

import musai.app.models.ERole;
import musai.app.models.LeaveType;
import musai.app.models.Role;
import musai.app.models.User;
import musai.app.repositories.LeaveTypeResposity;
import musai.app.repositories.RoleRepository;
import musai.app.repositories.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final LeaveTypeResposity leaveTypeResposity;

	// Inject RoleRepository to save Role in DB
	public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,LeaveTypeResposity leaveTypeResposity) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
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

		// Add admin if empty
		if (userRepository.count() == 0) {
			User user = new User("admin", "admin@gmail.com",
					"$2a$12$m3qeFYXkwyWhggLeiAAKd.l2yBfEkZhNkUuv/bCloaD/Pp5xyaDiS", null, null, null, null, null, null, null);

			Role adminRole = roleRepository.findByName(ERole.ADMIN)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			user.getRoles().add(adminRole);
			userRepository.save(user);
		}
		
		// Add leave type if empty
		if(leaveTypeResposity.count() == 0) {
			leaveTypeResposity.save(new LeaveType("有休",null));
			leaveTypeResposity.save(new LeaveType("公休",null));

			LeaveType paidLeave = leaveTypeResposity.findByName("有休");
			leaveTypeResposity.save(new LeaveType("半休", paidLeave));
			leaveTypeResposity.save(new LeaveType("全休", paidLeave));
			
			LeaveType publicLeave = leaveTypeResposity.findByName("公休");
			leaveTypeResposity.save(new LeaveType("特別休暇", publicLeave));
			leaveTypeResposity.save(new LeaveType("年末年始", publicLeave));
			leaveTypeResposity.save(new LeaveType("慶弔休暇", publicLeave));
			

			LeaveType specialLeave = leaveTypeResposity.findByName("特別休暇");
			leaveTypeResposity.save(new LeaveType("BBQ", specialLeave));
			leaveTypeResposity.save(new LeaveType("健康診断", specialLeave));
			leaveTypeResposity.save(new LeaveType("夏季休暇", specialLeave));
			leaveTypeResposity.save(new LeaveType("社員旅行", specialLeave));
			
			LeaveType familyLeave = leaveTypeResposity.findByName("慶弔休暇");
			leaveTypeResposity.save(new LeaveType("本人が結婚する", familyLeave));
			leaveTypeResposity.save(new LeaveType("子が結婚する", familyLeave));
			leaveTypeResposity.save(new LeaveType("妻が出産する", familyLeave));
			leaveTypeResposity.save(new LeaveType("父母、配偶者また子が死亡した", familyLeave));
			leaveTypeResposity.save(new LeaveType("祖父母、配偶者父母、兄弟姉妹が死亡した", familyLeave));
		}
	}
}