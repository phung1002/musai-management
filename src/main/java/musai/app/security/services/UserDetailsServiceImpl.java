package musai.app.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import musai.app.models.Employee;
import musai.app.repositories.EmployeeRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	EmployeeRepository employeeRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
		Employee employee = employeeRepository.findByEmployeeId(input)
				.orElseThrow(() -> new UsernameNotFoundException("Employee not found"));
		return UserDetailsImpl.build(employee);
	}
}