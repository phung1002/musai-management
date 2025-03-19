package musai.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import musai.app.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	List<User> findAllByDeletedAtIsNull();

	Optional<User> findByIdAndDeletedAtIsNull(Long id);

	@Query("SELECT u FROM User u WHERE u.deletedAt IS NULL "
			+ "AND LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))"
			+ "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))"
			+ "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))"
			+ "OR LOWER(u.fullNameFurigana) LIKE LOWER(CONCAT('%', :keyword, '%'))"
			+ "OR LOWER(u.department) LIKE LOWER(CONCAT('%', :keyword, '%'))"
			+ "OR LOWER(u.workPlace) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<User> findActiveByKeyContaining(@Param("keyword") String keyword);

}