package musai.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musai.app.models.LeaveType;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

	Boolean existsByName(String name);

	LeaveType findByName(String name);

	LeaveType findByValue(String value);
	
	@Query("SELECT lt FROM LeaveType lt WHERE lt.name LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<LeaveType> findActiveByKeywordContaining(@Param("keyword") String keyword);

	@Query("SELECT lt FROM LeaveType lt WHERE lt.deletedAt IS NULL")
	List<LeaveType> findAllActive();

	List<LeaveType> findByParentId(Long parentId);

}
