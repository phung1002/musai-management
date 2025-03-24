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
	@Query("SELECT la FROM LeaveApplication la WHERE la.user.deletedAt IS NULL ORDER BY la.createdAt DESC")
	List<LeaveApplication> findAllActive();

	// keyword で user.fullName または leaveType.name を検索
	@Query("SELECT la FROM LeaveApplication la WHERE la.user.deletedAt IS NULL "
			+ "AND (LOWER(la.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(la.leaveType.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " + "ORDER BY la.createdAt DESC")
	List<LeaveApplication> findActiveByKeywordContaining(@Param("keyword") String keyword);

	// 指定した userId のデータを取得
	@Query("SELECT la FROM LeaveApplication la WHERE la.user.deletedAt IS NULL AND la.user.id = :userId ORDER BY la.createdAt DESC")
	List<LeaveApplication> findActiveByUserId(@Param("userId") Long id);

	// userId で検索しつつ、理由 (reason) または 休暇タイプ (leaveType.name) で検索
	@Query("SELECT la FROM LeaveApplication la WHERE la.user.deletedAt IS NULL AND la.user.id = :userId "
			+ "AND (LOWER(la.reason) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "OR LOWER(la.leaveType.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " + "ORDER BY la.createdAt DESC")
	List<LeaveApplication> findActiveByUserIdByKeywordContaining(@Param("userId") Long id,
			@Param("keyword") String keyword);
	
	@Query("SELECT la FROM LeaveApplication la WHERE la.user.deletedAt IS NULL AND la.status = 'APPROVED'")
	public List<LeaveApplication> getApprovedLeaveApplications();
}