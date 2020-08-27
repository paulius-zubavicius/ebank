package hw.ebank.services;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import hw.ebank.dao.ClientDAO;
import hw.ebank.dao.SessionDAO;
import hw.ebank.exceptions.EBankException;
import hw.ebank.model.api.request.Credentials;
import hw.ebank.model.api.response.Token;
import hw.ebank.model.entites.Client;
import hw.ebank.model.entites.Session;

@Service
public class AccountServiceImpl implements AccountService {

	static final String ERR_MSG_EMAIL_IN_USE = "client.email.already.registered";
	static final String ERR_MSG_INCORRECT_EMAIL_OR_PASSWORD = "client.incorrect.email.or.password";

	@Autowired
	private ClientDAO clientDao;

	@Autowired
	private SessionDAO sessionDao;

	@Autowired
	private MsgResourceService msgService;

	@Override
	public Token signupNew(Credentials credentials) {
		createClient(credentials);
		return login(credentials);
	}

	@Override
	public Token login(Credentials credentials) {

		Client client = findClientByCredentials(credentials);

		Session session = new Session();
		session.setClient(client);
		session.setToken(UUID.randomUUID().toString());
		session.setLastTouch(LocalDateTime.now());
		sessionDao.save(session);

		return new Token(session.getToken());
	}

	@Transactional
	private Client createClient(Credentials credentials) {
		checkForEmailUniq(credentials.getEmail());

		Client client = new Client();
		client.setEmail(credentials.getEmail());
		client.setPasswordHash(DigestUtils.md5DigestAsHex(credentials.getPassword().getBytes()));
		client.setBalance(new BigDecimal(0));
		return clientDao.save(client);
	}

	private Client findClientByCredentials(Credentials credentials) {

		if (credentials == null || credentials.getEmail() == null || credentials.getPassword() == null) {
			throw new EBankException(NOT_FOUND, msgService.getMessage(ERR_MSG_INCORRECT_EMAIL_OR_PASSWORD));
		}

		Optional<Client> clOpt = clientDao.findByCredentials(credentials.getEmail(),
				DigestUtils.md5DigestAsHex(credentials.getPassword().getBytes()));

		if (clOpt.isEmpty()) {
			throw new EBankException(NOT_FOUND, msgService.getMessage(ERR_MSG_INCORRECT_EMAIL_OR_PASSWORD));
		}

		return clOpt.get();
	}

	private void checkForEmailUniq(String email) {

		Optional<Client> clOpt = clientDao.findByEmail(email);

		if (clOpt.isPresent()) {
			throw new EBankException(BAD_REQUEST, msgService.getMessage(ERR_MSG_EMAIL_IN_USE));
		}

	}

}
