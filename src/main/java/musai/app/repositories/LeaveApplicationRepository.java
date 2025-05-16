package musai.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musai.app.models.LeaveApplication;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
	 
	// 削除されていない全データを取得
	@Query("SELECT la FROM LeaveApplication la WHERE la.employee.deletedAt IS NULL "
			+ "AND la.leaveType.deletedAt IS NULL "
			+ "ORDER BY la.createdAt DESC")
	List<LeaveApplication> findAllActive();

	// keyword で employee.fullName または leaveType.name を検索
	@Query("SELECT la FROM LeaveApplication la WHERE la.employee.deletedAt IS NULL "
			+ "AND la.leaveType.deletedAt IS NULL "
			+ "AND (LOWER(la.employee.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(la.leaveType.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " + "ORDER BY la.createdAt DESC")
	List<LeaveApplication> findActiveByKeywordContaining(@Param("keyword") String keyword);

	// 指定した EmployeeId のデータを取得
	@Query("SELECT la FROM LeaveApplication la WHERE la.employee.deletedAt IS NULL "
			+ "AND la.leaveType.deletedAt IS NULL "
			+ "AND la.employee.id = :id ORDER BY la.createdAt DESC")
	List<LeaveApplication> findActiveByEmployeeId(@Param("id") Long id);

	// EmployeeId で検索しつつ、理由 (reason) または 休暇タイプ (leaveType.name) で検索
	@Query("SELECT la FROM LeaveApplication la WHERE la.employee.deletedAt IS NULL AND la.employee.id = :id "
			+ "AND la.leaveType.deletedAt IS NULL "
			+ "AND (LOWER(la.reason) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(la.leaveType.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " + "ORDER BY la.createdAt DESC")
	List<LeaveApplication> findActiveByEmployeeIdByKeywordContaining(@Param("id") Long id,
			@Param("keyword") String keyword);
	
	@Query("SELECT la FROM LeaveApplication la WHERE la.employee.deletedAt IS NULL "
			+ "AND la.leaveType.deletedAt IS NULL AND la.status = 'APPROVED'")
	public List<LeaveApplication> getApprovedLeaveApplications();

	List<LeaveApplication> findByEmployeeId(Long employeeId);

	List<LeaveApplication> findByLeaveTypeId(Long id);
}