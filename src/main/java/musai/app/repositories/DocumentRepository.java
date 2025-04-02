package musai.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import musai.app.models.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

	List<Document> findByUploadById(Long id);
	
	@Query("SELECT d FROM Document d WHERE d.uploadBy.deletedAt IS NULL ORDER BY d.uploadAt DESC")
	List<Document> findAllActive();
}
