package musai.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musai.app.models.LeaveApplication;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

}
