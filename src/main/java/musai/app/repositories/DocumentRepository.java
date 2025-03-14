package musai.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import musai.app.models.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
	 Optional<Document> findByTitleAndDeletedAtIsNull(String title);
}
