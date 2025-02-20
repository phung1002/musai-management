package musai.app.repositories;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musai.app.models.UserLeave;

@Repository
public interface UserLeaveRepository extends JpaRepository<UserLeave, Long> {
    
    List<UserLeave> findByLeaveTypeId(Long leaveTypeId);
    
}