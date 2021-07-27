package hw.ebank.units.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hw.ebank.model.validators.PasswordPolicyValidator;

public class PasswordPolicyValidatorTest {

	private PasswordPolicyValidator validator;

	@BeforeEach
	public void init() {
		validator = new PasswordPolicyValidator();
	}

	@Test
	public void pwdNull() {
		assertFalse(validator.isValid(null, null));
	}

	@Test
	public void pwdWhiteSpace() {
		assertFalse(validator.isValid("", null));
		assertFalse(validator.isValid(" ", null));
		assertFalse(validator.isValid("\t", null));
		assertFalse(validator.isValid("\n", null));
		assertFalse(validator.isValid("   ", null));
		assertFalse(validator.isValid(" \t  ", null));
	}

	@Test
	public void pwdToShort() {
		assertFalse(validator.isValid("seven77", null));
	}

	@Test
	public void pwdOnlyLetters() {
		assertFalse(validator.isValid("password", null));
	}

	@Test
	public void pwdOnlyNumbers() {
		assertFalse(validator.isValid("12345678", null));
	}

	@Test
	public void pwdOk() {
		assertTrue(validator.isValid("seven77!", null));
		assertTrue(validator.isValid("       @", null));
		assertTrue(validator.isValid("!@#$%^&*()", null));
	}
}
