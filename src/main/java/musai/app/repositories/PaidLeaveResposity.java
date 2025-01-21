package musai.app.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import musai.app.models.PaidLeave;

@Repository
public interface PaidLeaveResposity extends JpaRepository<PaidLeave, Long> {

	Boolean existsByName(String name);

	Optional<PaidLeave> findById(Long id);

}
