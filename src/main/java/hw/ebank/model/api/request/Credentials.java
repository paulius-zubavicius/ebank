package hw.ebank.model.api.request;

import javax.validation.constraints.Email;

import hw.ebank.model.validators.PasswordPolicy;

public class Credentials {

	@Email
	private String email;

	@PasswordPolicy
	private String password;

	public Credentials() {
	}

	public Credentials(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

}
