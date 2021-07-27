package hw.ebank.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import hw.ebank.dao.ClientDAO;
import hw.ebank.dao.OperationDAO;
import hw.ebank.dao.SessionDAO;
import hw.ebank.exceptions.EBankException;
import hw.ebank.model.api.response.Balance;
import hw.ebank.model.api.response.StatementPage;
import hw.ebank.model.entites.Client;
import hw.ebank.model.entites.Operation;
import hw.ebank.model.entites.Session;

@SpringBootTest
public class MoneyOperationServiceTest {

	private static final String VALID_TOKEN_1 = "valid-random-token-111";
	private static final String VALID_TOKEN_2 = "valid-random-token-222";
	private static final String NOT_VALID_TOKEN = "not-valid-random-token";

	@MockBean
	private ClientDAO clientDao;

	@MockBean
	private SessionDAO sessionDao;

	@MockBean
	private OperationDAO operationDao;

	@MockBean
	private MsgResourceService messageSource;

	@Autowired
	private MoneyOperationService moneyOpService;

	@BeforeEach
	public void init() {

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

		when(sessionDao.findByValidToken(Mockito.eq(VALID_TOKEN_1), Mockito.any()))
				.thenReturn(Optional.of(new Session(1L, LocalDateTime.now(), VALID_TOKEN_1, client1)));
		when(sessionDao.findByValidToken(Mockito.eq(VALID_TOKEN_2), Mockito.any()))
				.thenReturn(Optional.of(new Session(2L, LocalDateTime.now(), VALID_TOKEN_2, client2)));
		when(sessionDao.findByValidToken(Mockito.eq(NOT_VALID_TOKEN), Mockito.any())).thenReturn(Optional.empty());

		List<Operation> operations = new ArrayList<>();

		when(operationDao.save(Mockito.any())).thenAnswer((invocation) -> {
			operations.add((Operation) invocation.getArguments()[0]);
			return (Operation) invocation.getArguments()[0];
		});

		when(operationDao.findAllByClient(Mockito.any(), Mockito.any())).thenAnswer((invocation) -> {
			Client client = (Client) invocation.getArguments()[0];
			Pageable pageable = (Pageable) invocation.getArguments()[1];
			List<Operation> ops = operations.stream()
					.filter(op -> op.getClient().getEmail().equalsIgnoreCase(client.getEmail()))
					.collect(Collectors.toList());
			return new PageImpl<>(ops, pageable, ops.size());
		});

	}

	@Test
	public void balanceNotValidToken() {
		assertThrows(EBankException.class, () -> {
			moneyOpService.balance(NOT_VALID_TOKEN);
		});
	}

	@Test
	public void depositNotValidToken() {
		assertThrows(EBankException.class, () -> {
			moneyOpService.deposit(NOT_VALID_TOKEN, new BigDecimal(1));
		});
	}

	@Test
	public void withdrawNotValidToken() {
		assertThrows(EBankException.class, () -> {
			moneyOpService.withdraw(NOT_VALID_TOKEN, new BigDecimal(1));
		});
	}

	@Test
	public void statementNotValidToken() {
		assertThrows(EBankException.class, () -> {
			moneyOpService.statement(NOT_VALID_TOKEN, 0, 1);
		});
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

	@Test
	public void withdrawNotEnoughMoney() {
		assertThrows(EBankException.class, () -> {
			moneyOpService.withdraw(VALID_TOKEN_1, new BigDecimal(11.01));
		});
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
