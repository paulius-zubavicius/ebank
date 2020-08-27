package hw.ebank.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hw.ebank.model.entites.Client;
import hw.ebank.model.entites.Operation;

public interface OperationDAO {

	Page<Operation> findAllByClient(Client client, Pageable pageable);

	Operation save(Operation op);
}
