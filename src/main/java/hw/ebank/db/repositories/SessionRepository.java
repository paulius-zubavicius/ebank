package hw.ebank.db.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hw.ebank.model.entites.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {

	@Query("FROM Session sess WHERE sess.token = :token AND EXTRACT(EPOCH FROM now() - sess.lastTouch) < (60 * :maxSessInMin)")
	Optional<Session> findByValidToken(String token, Integer maxSessInMin);
}
