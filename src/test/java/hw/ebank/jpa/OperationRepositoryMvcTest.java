package hw.ebank.jpa;

//import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import hw.ebank.db.repositories.OperationRepository;
import hw.ebank.model.entites.Client;
import hw.ebank.model.entites.Operation;
import hw.ebank.model.entites.OperationType;

//@RunWith(SpringRunner.class)
//@DataJpaTest
//@Sql({ "/schema.sql", "/data.sql" })
public class OperationRepositoryMvcTest {

//	@Autowired
	private TestEntityManager em;

//	@Autowired
	private OperationRepository repo;

	private List<Operation> ops = null;

//	@BeforeEach
	public void init() {

		ops = new ArrayList<>();
		Client client1 = new Client();
		client1.setId(1L);
		client1.setBalance(new BigDecimal(0));
		client1.setEmail("111@111.111");
		client1.setPasswordHash("111");

		Client client2 = new Client();
		client2.setId(2L);
		client2.setBalance(new BigDecimal(100));
		client2.setEmail("222@222.222");
		client2.setPasswordHash("222");

		Stream<MoneyOp> opStream = Stream.of(new MoneyOp(1L, client1), new MoneyOp(2L, client1),
				new MoneyOp(8L, client2), new MoneyOp(5L, client1));

		opStream.map(this::toOperation).forEach(o -> {
			ops.add(em.persist(o));
			em.flush();
		});

	}

//	@Test
	public void findByClient() {

		Client client1 = new Client();
		client1.setId(1L);
		client1.setBalance(new BigDecimal(0));
		client1.setEmail("111@111.111");
		client1.setPasswordHash("111");

		Pageable pageable = PageRequest.of(0, 5);

		// when
		Page<Operation> page = repo.findAllByClient(client1, pageable);

		// then
		assertEquals(3, page.getTotalElements());
		assertEquals(Long.valueOf(1), page.toList().get(0).getId());
		assertEquals(Long.valueOf(2), page.toList().get(1).getId());
		assertEquals(Long.valueOf(5), page.toList().get(2).getId());

	}

	private Operation toOperation(MoneyOp mo) {
		Operation op = new Operation();
		op.setId(mo.id);
		op.setTimestamp(ZonedDateTime.now().minusDays(1L).plusMinutes(mo.id));
		op.setType(OperationType.DEPOSIT);
		op.setAmount(new BigDecimal(10));
		op.setBalanceBefore(new BigDecimal(0));
		op.setClient(mo.client);
		return op;
	}

	private class MoneyOp {
		private Long id;
		private Client client;

		public MoneyOp(Long id, Client client) {
			this.id = id;
			this.client = client;
		}

	}

}
