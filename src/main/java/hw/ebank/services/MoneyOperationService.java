package hw.ebank.services;

import java.math.BigDecimal;

import hw.ebank.model.api.response.Balance;
import hw.ebank.model.api.response.StatementPage;

public interface MoneyOperationService {

	Balance deposit(String token, BigDecimal amount);

	Balance withdraw(String token, BigDecimal amount);

	Balance balance(String token);

	StatementPage statement(String token, Integer i, Integer statementPageSize);

}
