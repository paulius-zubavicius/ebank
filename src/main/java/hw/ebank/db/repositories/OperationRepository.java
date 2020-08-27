package hw.ebank.db.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hw.ebank.model.entites.Client;
import hw.ebank.model.entites.Operation;

public interface OperationRepository extends JpaRepository<Operation, Long> {

	@Query(value = "FROM Operation o WHERE o.client = :client ORDER BY o.timestamp ", countQuery = "SELECT count(*) FROM Operation o WHERE o.client = :client")
	Page<Operation> findAllByClient(Client client, Pageable pageable);

}
