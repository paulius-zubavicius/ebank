package hw.ebank.dao;

import java.util.Optional;

import hw.ebank.model.entites.Client;

public interface ClientDAO {

	Optional<Client> findById(Long id);

	Optional<Client> findByEmail(String email);

	Client save(Client client);

	Optional<Client> findByCredentials(String email, String passwHash);

}
