package hw.ebank.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import hw.ebank.db.repositories.ClientRepository;
import hw.ebank.model.entites.Client;

@Repository
public class ClientDAOImpl implements ClientDAO {

	@Autowired
	private ClientRepository repo;

	@Override
	public Optional<Client> findByEmail(String email) {
		return repo.findByEmail(email);
	}

	@Override
	public Optional<Client> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public Client save(Client client) {
		return repo.save(client);
	}

	@Override
	public Optional<Client> findByCredentials(String email, String passwHash) {
		return repo.findByCredentials(email, passwHash);
	}
}
