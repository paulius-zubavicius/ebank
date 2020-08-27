package hw.ebank.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import hw.ebank.db.repositories.SessionRepository;
import hw.ebank.model.entites.Session;

@Repository
public class SessionDAOImpl implements SessionDAO {

	@Autowired
	private SessionRepository repo;

	@Override
	public Optional<Session> findByValidToken(String token, Integer maxSessInMin) {
		return repo.findByValidToken(token, maxSessInMin);
	}

	@Override
	public Session save(Session session) {
		return repo.save(session);
	}

}
