package musai.app;

import musai.app.models.ERole;
import musai.app.models.Role;
import musai.app.models.User;
import musai.app.repositories.RoleRepository;
import musai.app.repositories.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;

	// Inject RoleRepository to save Role in DB
	public DataInitializer(RoleRepository roleRepository, UserRepository userRepository) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
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
					"$2a$12$m3qeFYXkwyWhggLeiAAKd.l2yBfEkZhNkUuv/bCloaD/Pp5xyaDiS", null, null, null, null, null);

			Role adminRole = roleRepository.findByName(ERole.ADMIN)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			user.getRoles().add(adminRole);
			userRepository.save(user);
		}
	}
}