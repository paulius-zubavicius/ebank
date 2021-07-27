package hw.ebank.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.DigestUtils;

import hw.ebank.dao.ClientDAO;
import hw.ebank.dao.SessionDAO;
import hw.ebank.exceptions.EBankException;
import hw.ebank.model.api.request.Credentials;
import hw.ebank.model.api.response.Token;
import hw.ebank.model.entites.Client;

@SpringBootTest
public class AccountServiceTest {

	@MockBean
	private ClientDAO clientDao;

	@MockBean
	private SessionDAO sessionDao;

	@MockBean
	private MsgResourceService messageSource;

	@Autowired
	private AccountService accountService;

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

		when(messageSource.getMessage(AccountServiceImpl.ERR_MSG_EMAIL_IN_USE))
				.thenReturn(AccountServiceImpl.ERR_MSG_EMAIL_IN_USE);
		when(messageSource.getMessage(AccountServiceImpl.ERR_MSG_INCORRECT_EMAIL_OR_PASSWORD))
				.thenReturn(AccountServiceImpl.ERR_MSG_INCORRECT_EMAIL_OR_PASSWORD);

		when(clientDao.findByEmail("111@111.111")).thenReturn(Optional.of(client1));
		when(clientDao.findByEmail("222@222.222")).thenReturn(Optional.of(client2));
		when(clientDao.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
	}

	@Test
	public void noCredentials1() {
		assertThrows(EBankException.class, () -> {
			accountService.login(null);
		});
	}

	@Test
	public void noCredentials2() {
		assertThrows(EBankException.class, () -> {
			accountService.login(new Credentials());
		});
	}

	@Test
	public void noEmail() {
		assertThrows(EBankException.class, () -> {
			accountService.login(new Credentials(null, "password"));
		});
	}

	@Test
	public void noPassw() {
		assertThrows(EBankException.class, () -> {
			accountService.login(new Credentials("email", null));
		});
	}

	@Test
	public void wrongCredentials() {
		assertThrows(EBankException.class, () -> {
			accountService.login(new Credentials("not@existing.email", "password"));
		});
	}

	@Test
	public void login() {
		Client client1 = new Client();
		client1.setId(1L);
		client1.setBalance(new BigDecimal(11));
		client1.setEmail("111@111.111");
		client1.setPasswordHash("111");

		when(clientDao.findByCredentials("111@111.111", DigestUtils.md5DigestAsHex("111".getBytes())))
				.thenReturn(Optional.of(client1));

		Token token = accountService.login(new Credentials("111@111.111", "111"));
		assertNotNull(token);
		assertNotNull(token.getToken());
		assertEquals(36, token.getToken().length());
	}

	@Test
	public void loginTwice() {
		Client client1 = new Client();
		client1.setId(1L);
		client1.setBalance(new BigDecimal(11));
		client1.setEmail("111@111.111");
		client1.setPasswordHash("111");
		when(clientDao.findByCredentials("111@111.111", DigestUtils.md5DigestAsHex("111".getBytes())))
				.thenReturn(Optional.of(client1));

		Token token1 = accountService.login(new Credentials("111@111.111", "111"));
		assertNotNull(token1);

		Token token2 = accountService.login(new Credentials("111@111.111", "111"));
		assertNotNull(token2);
		assertNotEquals(token1.getToken(), token2.getToken());
	}

	@Test
	public void signupNew() {
		Client client1 = new Client();
		client1.setId(1L);
		client1.setBalance(new BigDecimal(11));
		client1.setEmail("444@444.444");
		client1.setPasswordHash("444");

		when(clientDao.findByEmail("444@444.444")).thenReturn(Optional.empty());

		when(clientDao.findByCredentials("444@444.444", DigestUtils.md5DigestAsHex("444".getBytes())))
				.thenReturn(Optional.of(client1));

		Token token = accountService.signupNew(new Credentials("444@444.444", "444"));
		assertNotNull(token);
		assertNotNull(token.getToken());
		assertEquals(36, token.getToken().length());
	}

	@Test
	public void signupAsExisting() {
		Client client1 = new Client();
		client1.setId(1L);
		client1.setBalance(new BigDecimal(11));
		client1.setEmail("444@444.444");
		client1.setPasswordHash("444");

		when(clientDao.findByEmail("444@444.444")).thenReturn(Optional.empty());

		when(clientDao.findByCredentials("444@444.444", DigestUtils.md5DigestAsHex("444".getBytes())))
				.thenReturn(Optional.of(client1));

		Token token = accountService.signupNew(new Credentials("444@444.444", "444"));
		assertNotNull(token);
		assertNotNull(token.getToken());
		assertEquals(36, token.getToken().length());

		when(clientDao.findByEmail("444@444.444")).thenReturn(Optional.of(client1));

		assertThrows(EBankException.class, () -> {
			accountService.signupNew(new Credentials("444@444.444", "444"));
		});
	}

}
