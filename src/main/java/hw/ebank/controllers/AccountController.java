package hw.ebank.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import hw.ebank.model.api.request.Credentials;
import hw.ebank.model.api.response.Token;
import hw.ebank.services.AccountService;

@RestController
public class AccountController {

	@Autowired
	private AccountService accountService;

	@PostMapping("/signup")
	private ResponseEntity<Token> signup(@Valid @RequestBody Credentials credentials) {
		return ResponseEntity.ok(accountService.signupNew(credentials));
	}
	
	@PostMapping("/login")
	private ResponseEntity<Token> login(@Valid @RequestBody Credentials credentials) {
		return ResponseEntity.ok(accountService.login(credentials));
	}

}
