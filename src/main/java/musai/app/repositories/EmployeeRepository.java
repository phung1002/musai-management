package musai.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musai.app.models.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	Optional<Employee> findByEmployeeId(String employeeId);

	@Query(value = "SELECT COUNT(*) FROM employees WHERE employee_id = :empId", nativeQuery = true)
	Long countByEmployeeIdIgnoreSoftDelete(@Param("empId") String empId);
	
	@Query(value = "SELECT COUNT(*) FROM employees WHERE email = :email", nativeQuery = true)
	Long countByEmailIgnoreSoftDelete(@Param("email") String email);

	@Query("SELECT e FROM Employee e WHERE e.deletedAt IS NULL "
			+ "AND LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :keyword, '%'))"
			+ "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))"
			+ "OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))"
			+ "OR LOWER(e.fullNameFurigana) LIKE LOWER(CONCAT('%', :keyword, '%'))"
			+ "OR LOWER(e.department) LIKE LOWER(CONCAT('%', :keyword, '%'))"
			+ "OR LOWER(e.workPlace) LIKE LOWER(CONCAT('%', :keyword, '%'))"
			+ "OR LOWER(e.mobile) LIKE CONCAT('%', :keyword, '%')")
	List<Employee> findActiveByKeyContaining(@Param("keyword") String keyword);

	@Query("SELECT e FROM Employee e JOIN e.roles r WHERE e.deletedAt IS NULL "
			+ "AND r.name = 'MEMBER' "
			+ "AND (LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :keyword, '%'))"
			+ "OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	List<Employee> findMemberActiveByKeyContaining(@Param("keyword") String keyword);
	
	@Query("SELECT e FROM Employee e JOIN e.roles r WHERE e.deletedAt IS NULL AND r.name = 'MEMBER'")
	List<Employee> findByRoleMember();

}