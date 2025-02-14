package musai.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musai.app.models.LeaveType;

@Repository
public interface LeaveTypeResposity extends JpaRepository<LeaveType, Long> {

	Boolean existsByName(String name);

	Optional<LeaveType> findById(Long id);

	Optional<LeaveType> findByIdAndDeletedAtIsNull(Long id);
	
	LeaveType findByName(String name);

	List<LeaveType> findAllByDeletedAtIsNull(); 
	
	List<LeaveType> findByParentIdAndDeletedAtIsNull(Long parentId); 
	
}
 