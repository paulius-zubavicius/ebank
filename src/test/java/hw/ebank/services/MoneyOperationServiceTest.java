package hw.ebank.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import hw.ebank.dao.ClientDAO;
import hw.ebank.dao.OperationDAO;
import hw.ebank.dao.SessionDAO;
import hw.ebank.exceptions.EBankException;
import hw.ebank.model.api.response.Balance;
import hw.ebank.model.api.response.StatementPage;
import hw.ebank.model.entites.Client;
import hw.ebank.model.entites.Operation;
import hw.ebank.model.entites.Session;

@RunWith(SpringRunner.class)
public class MoneyOperationServiceTest {

	private static final String VALID_TOKEN_1 = "valid-random-token-111";
	private static final String VALID_TOKEN_2 = "valid-random-token-222";
	private static final String NOT_VALID_TOKEN = "not-valid-random-token";

	private static List<Operation> operations = new ArrayList<>();

	@TestConfiguration
	static class TestContextConfiguration {

		@Bean
		public OperationDAO operationDAO() {
			return new OperationDAO() {

				@Override
				public Page<Operation> findAllByClient(Client client, Pageable pageable) {
					return new PageImpl<>(operations, pageable, operations.size());
				}

				@Override
				public Operation save(Operation op) {
					operations.add(op);
					return op;
				}
			};
		}

		@Bean
		public MoneyOperationService moneyOpService() {
			return new MoneyOperationServiceImpl();
		}
	}

	@MockBean
	private ClientDAO clientDao;

	@MockBean
	private SessionDAO sessionDao;

	@MockBean
	private MsgResourceService messageSource;

	@Autowired
	private MoneyOperationService moneyOpService;

	@Before
	public void init() {
		operations.clear();

		Client client1 = new Client();
		client1.setId(1L);
		client1.setBalance(new BigDecimal(11));
		client1.setEmail("111@111.111");
		client1.setPasswordHash("111");

		Client client2 = new Client();
		client2.setId(2L);
		client2.setBalance(new BigDecimal(22));
		client2.setEmail("222@222.222");
		client2.setPasswordHash("222");

		when(messageSource.getMessage(Mockito.anyString())).thenReturn("msg");

		when(clientDao.findByEmail("111@111.111")).thenReturn(Optional.of(client1));
		when(clientDao.findByEmail("222@222.222")).thenReturn(Optional.of(client2));

		when(sessionDao.findByValidToken(VALID_TOKEN_1, 1))
				.thenReturn(Optional.of(new Session(1L, LocalDateTime.now(), VALID_TOKEN_1, client1)));
		when(sessionDao.findByValidToken(VALID_TOKEN_2, 1))
				.thenReturn(Optional.of(new Session(2L, LocalDateTime.now(), VALID_TOKEN_2, client2)));

	}

	@Test(expected = EBankException.class)
	public void balanceNotValidToken() {
		moneyOpService.balance(NOT_VALID_TOKEN);
	}

	@Test(expected = EBankException.class)
	public void depositNotValidToken() {
		moneyOpService.deposit(NOT_VALID_TOKEN, new BigDecimal(1));
	}

	@Test(expected = EBankException.class)
	public void withdrawNotValidToken() {
		moneyOpService.withdraw(NOT_VALID_TOKEN, new BigDecimal(1));
	}

	@Test(expected = EBankException.class)
	public void statementNotValidToken() {
		moneyOpService.statement(NOT_VALID_TOKEN, 0, 1);
	}

	@Test
	public void balance1() {
		Balance balance = moneyOpService.balance(VALID_TOKEN_1);
		assertNotNull(balance);
		assertTrue(new BigDecimal(11).compareTo(balance.getBalance()) == 0);
	}

	@Test
	public void balance2() {
		Balance balance = moneyOpService.balance(VALID_TOKEN_2);
		assertNotNull(balance);
		assertTrue(new BigDecimal(22).compareTo(balance.getBalance()) == 0);
	}

	@Test
	public void deposit() {
		Balance balance1 = moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(12.5));
		assertNotNull(balance1);
		assertTrue(new BigDecimal(23.5).compareTo(balance1.getBalance()) == 0);

		Balance balance2 = moneyOpService.balance(VALID_TOKEN_1);
		assertNotNull(balance2);
		assertTrue(new BigDecimal(23.5).compareTo(balance2.getBalance()) == 0);
	}

	@Test
	public void withdraw() {
		Balance balance1 = moneyOpService.withdraw(VALID_TOKEN_1, new BigDecimal(11));
		assertNotNull(balance1);
		assertTrue(new BigDecimal(0).compareTo(balance1.getBalance()) == 0);

		Balance balance2 = moneyOpService.balance(VALID_TOKEN_1);
		assertNotNull(balance2);
		assertTrue(new BigDecimal(0).compareTo(balance2.getBalance()) == 0);
	}

	@Test(expected = EBankException.class)
	public void withdrawNotEnoughMoney() {
		moneyOpService.withdraw(VALID_TOKEN_1, new BigDecimal(11.01));
	}

	@Test
	public void accountStatement() {
		moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(1));
		moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(2.99));
		moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(3));
		moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(4));
		moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(5));
		moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(6));
		moneyOpService.withdraw(VALID_TOKEN_1, new BigDecimal(9));
		moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(7));
		moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(8));
		moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(9));
		moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(10.01));

		StatementPage page = moneyOpService.statement(VALID_TOKEN_1, 0, 3);
		assertNotNull(page);
		assertNotNull(page.getEntries());
		assertEquals(0, page.getPage());
		assertEquals(4, page.getPagesCount());
		assertEquals(11, page.getEntries().size());
		assertTrue(new BigDecimal(58).compareTo(page.getBalanceCurrent()) == 0);
	}

	@Test
	public void accountStatementEntry() {
		moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(1));
		moneyOpService.deposit(VALID_TOKEN_1, new BigDecimal(2.99));

		StatementPage page = moneyOpService.statement(VALID_TOKEN_1, 1, 1);
		assertNotNull(page);
		assertNotNull(page.getEntries());
		assertEquals(1, page.getPage());
		assertEquals(2, page.getPagesCount());
		assertEquals(2, page.getEntries().size());
		assertTrue(new BigDecimal(14.99).compareTo(page.getBalanceCurrent()) == 0);
		assertTrue(new BigDecimal(2.99).compareTo(page.getEntries().get(1).getAmount()) == 0);
		assertTrue(new BigDecimal(12.00).compareTo(page.getEntries().get(1).getBalanceBeforeOp()) == 0);
		assertTrue(page.getEntries().get(1).getDeposit());
		assertNotNull(page.getEntries().get(1).getTimestamp());
	}

	@Test
	public void accountStatementEmpty() {
		StatementPage page = moneyOpService.statement(VALID_TOKEN_2, 15, 3);
		assertNotNull(page);
		assertNotNull(page.getEntries());
		assertEquals(15, page.getPage());
		assertEquals(0, page.getPagesCount());
		assertEquals(0, page.getEntries().size());
		assertTrue(new BigDecimal(22).compareTo(page.getBalanceCurrent()) == 0);
	}
}
