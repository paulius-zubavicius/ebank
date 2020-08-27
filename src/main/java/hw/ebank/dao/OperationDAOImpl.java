package hw.ebank.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import hw.ebank.db.repositories.OperationRepository;
import hw.ebank.model.entites.Client;
import hw.ebank.model.entites.Operation;

@Repository
public class OperationDAOImpl implements OperationDAO {

	@Autowired
	private OperationRepository repo;

	@Override
	public Page<Operation> findAllByClient(Client client, Pageable pageable) {
		return repo.findAllByClient(client, pageable);
	}

	@Override
	public Operation save(Operation op) {
		return repo.save(op);
	}

}
