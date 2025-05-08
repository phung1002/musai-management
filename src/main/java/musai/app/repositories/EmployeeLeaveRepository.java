package musai.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musai.app.models.Employee;
import musai.app.models.EmployeeLeave;
import musai.app.models.LeaveType;

@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, Long> {

	List<EmployeeLeave> findByLeaveTypeId(Long leaveTypeId);

	List<EmployeeLeave> findByEmployeeId(Long id);

	@Query("SELECT el FROM EmployeeLeave el WHERE el.deletedAt IS NULL AND el.employee.deletedAt IS NULL "
			+ "AND el.leaveType.deletedAt IS NULL ORDER BY el.validTo DESC")
	List<EmployeeLeave> findAllActive();

	@Query("SELECT el FROM EmployeeLeave el WHERE el.deletedAt IS NULL AND el.employee.deletedAt IS NULL "
			+ "AND el.leaveType.deletedAt IS NULL "
			+ "AND LOWER(el.employee.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " + "ORDER BY el.validTo DESC")
	List<EmployeeLeave> findActiveByKeyContaining(@Param("keyword") String keyword);

	List<EmployeeLeave> findByEmployeeAndLeaveType(Employee employee, LeaveType leaveType);
}