package hw.ebank.services;

import static hw.ebank.model.entites.OperationType.DEPOSIT;
import static hw.ebank.model.entites.OperationType.WITHDRAW;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hw.ebank.dao.ClientDAO;
import hw.ebank.dao.OperationDAO;
import hw.ebank.dao.SessionDAO;
import hw.ebank.exceptions.EBankException;
import hw.ebank.model.api.response.Balance;
import hw.ebank.model.api.response.OperationEntry;
import hw.ebank.model.api.response.StatementPage;
import hw.ebank.model.entites.Client;
import hw.ebank.model.entites.Operation;
import hw.ebank.model.entites.OperationType;
import hw.ebank.model.entites.Session;

@Service
public class MoneyOperationServiceImpl implements MoneyOperationService {

	private static final String ERR_MSG_NOT_ENOUGH_MONEY = "operation.withdraw.enough.money";
	private static final String ERR_MSG_INVALID_TOKEN = "session.invalid.token";

	@Value("${ebank.session.max-sess-in-min:1}")
	private Integer maxSessInMin;

	@Autowired
	private ClientDAO clientDao;

	@Autowired
	private OperationDAO operationDao;

	@Autowired
	private SessionDAO sessionDao;

	@Autowired
	private MsgResourceService msgService;

	@Override
	public Balance deposit(String token, BigDecimal amount) {
		Client client = touchValidSession(token).getClient();
		return moneyOperation(DEPOSIT, client, amount);
	}

	@Override
	public Balance withdraw(String token, BigDecimal amount) {
		Client client = touchValidSession(token).getClient();
		return moneyOperation(WITHDRAW, client, amount);
	}

	@Override
	public Balance balance(String token) {
		return new Balance(touchValidSession(token).getClient().getBalance());
	}

	@Override
	public StatementPage statement(String token, Integer pageNb, Integer statementPageSize) {

		Client client = touchValidSession(token).getClient();

		Pageable pageable = PageRequest.of(pageNb, statementPageSize);
		Page<Operation> page = operationDao.findAllByClient(client, pageable);

		StatementPage sp = new StatementPage();
		sp.setPage(page.getNumber());
		sp.setPagesCount(page.getTotalPages());
		sp.setBalanceCurrent(client.getBalance());
		sp.setEntries(page.getContent().stream().map(this::convertToOpEntry).collect(toList()));

		return sp;
	}

	private OperationEntry convertToOpEntry(Operation op) {

		OperationEntry oe = new OperationEntry();
		oe.setAmount(OperationType.WITHDRAW.equals(op.getType()) ? op.getAmount().negate() : op.getAmount());
		oe.setDeposit(OperationType.DEPOSIT.equals(op.getType()));
		oe.setTimestamp(op.getTimestamp());
		oe.setBalanceBeforeOp(op.getBalanceBefore());

		return oe;
	}

	private Session touchValidSession(String token) {

		Optional<Session> esssOpt = sessionDao.findByValidToken(token, maxSessInMin);
		if (esssOpt.isEmpty()) {
			throw new EBankException(HttpStatus.UNAUTHORIZED, msgService.getMessage(ERR_MSG_INVALID_TOKEN));
		}

		Session sess = esssOpt.get();
		sess.setLastTouch(LocalDateTime.now());

		sessionDao.save(sess);
		
		return sess;
	}

	@Transactional
	private Balance moneyOperation(OperationType type, Client client, BigDecimal amount) {

		createOperationRecord(type, amount, client);
		updateBanalce(type, amount, client);

		return new Balance(client.getBalance());
	}

	private Client updateBanalce(OperationType type, BigDecimal amount, Client client) {

		if (DEPOSIT.equals(type)) {
			client.setBalance(client.getBalance().add(amount));
		} else {
			if (client.getBalance().compareTo(amount) >= 0) {
				client.setBalance(client.getBalance().subtract(amount));
			} else {
				throw new EBankException(HttpStatus.NOT_ACCEPTABLE, msgService.getMessage(ERR_MSG_NOT_ENOUGH_MONEY));
			}
		}
		clientDao.save(client);
		return client;
	}

	private void createOperationRecord(OperationType type, BigDecimal amount, Client client) {
		Operation op = new Operation();
		op.setClient(client);
		op.setAmount(amount);
		op.setTimestamp(ZonedDateTime.now());
		op.setType(type);
		op.setBalanceBefore(client.getBalance());

		operationDao.save(op);
	}

}
