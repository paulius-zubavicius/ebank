package hw.ebank.db.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hw.ebank.model.entites.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

	Optional<Client> findByEmail(String email);

	@Query("FROM Client c WHERE LOWER(c.email) = LOWER(:email) AND LOWER(c.passwordHash) = LOWER(:passwHash) ")
	Optional<Client> findByCredentials(String email, String passwHash);

}
