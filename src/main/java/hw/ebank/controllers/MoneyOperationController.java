package hw.ebank.controllers;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import hw.ebank.model.api.request.MoneyOperation;
import hw.ebank.model.api.response.Balance;
import hw.ebank.model.api.response.StatementPage;
import hw.ebank.services.MoneyOperationService;

@RestController
public class MoneyOperationController {

	private static final String HEADER_TOKEN = "token";

	@Value("${ebank.statement.page-size:10}")
	private Integer statementPageSize;

	@Autowired
	private MoneyOperationService moneyOperationService;

	@PostMapping("/deposit")
	private ResponseEntity<Balance> deposit(@RequestHeader(HEADER_TOKEN) String token,
			@Valid @RequestBody MoneyOperation op) {
		return ResponseEntity.ok(moneyOperationService.deposit(token, op.getAmount()));
	}

	@PostMapping("/withdraw")
	private ResponseEntity<Balance> withdraw(@RequestHeader(HEADER_TOKEN) String token,
			@Valid @RequestBody MoneyOperation op) {
		return ResponseEntity.ok(moneyOperationService.withdraw(token, op.getAmount()));
	}

	@GetMapping("/balance")
	private ResponseEntity<Balance> balance(@RequestHeader(HEADER_TOKEN) String token) {
		return ResponseEntity.ok(moneyOperationService.balance(token));
	}

	@GetMapping("/statement")
	private ResponseEntity<StatementPage> statement1stPage(@RequestHeader(HEADER_TOKEN) String token) {
		return ResponseEntity.ok(moneyOperationService.statement(token, 0, statementPageSize));
	}

	@GetMapping("/statement/page/{page}")
	private ResponseEntity<StatementPage> statement(@RequestHeader(HEADER_TOKEN) String token,
			@PositiveOrZero @PathVariable("page") Integer pageNumber) {
		return ResponseEntity.ok(moneyOperationService.statement(token, pageNumber, statementPageSize));
	}
}
