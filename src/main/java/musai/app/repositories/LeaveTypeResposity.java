package musai.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musai.app.models.LeaveType;

@Repository
public interface LeaveTypeResposity extends JpaRepository<LeaveType, Long> {

	Boolean existsByName(String name);

	Optional<LeaveType> findById(Long id);
	
	// Fetch by ID where deleted_at is NULL
	Optional<LeaveType> findByIdAndDeletedAtIsNull(Long id); 
	
	//Excluding Soft-Deleted Records
	 // List<LeaveType> findByNameContainingIgnoreCase(String name);
	
	
}
 