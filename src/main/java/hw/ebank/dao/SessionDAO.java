package hw.ebank.dao;

import java.util.Optional;

import hw.ebank.model.entites.Session;

public interface SessionDAO {

	Optional<Session> findByValidToken(String token, Integer maxSessInMin);

	Session save(Session session);

}
