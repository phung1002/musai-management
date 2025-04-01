package musai.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musai.app.models.UserLeave;

@Repository
public interface UserLeaveRepository extends JpaRepository<UserLeave, Long> {

	List<UserLeave> findByLeaveTypeId(Long leaveTypeId);

	List<UserLeave> findByUserId(Long id);

	@Query("SELECT ul FROM UserLeave ul WHERE ul.deletedAt IS NULL AND ul.user.deletedAt IS NULL "
			+ "AND ul.leaveType.deletedAt IS NULL ORDER BY ul.validTo DESC")
	List<UserLeave> findAllActive();

	@Query("SELECT ul FROM UserLeave ul WHERE ul.deletedAt IS NULL AND ul.user.deletedAt IS NULL "
			+ "AND ul.leaveType.deletedAt IS NULL "
			+ "AND LOWER(ul.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " + "ORDER BY ul.validTo DESC")
	List<UserLeave> findActiveByKeyContaining(@Param("keyword") String keyword);

}