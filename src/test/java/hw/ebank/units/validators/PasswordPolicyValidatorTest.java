package hw.ebank.units.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import hw.ebank.model.validators.PasswordPolicyValidator;

public class PasswordPolicyValidatorTest {

	private PasswordPolicyValidator validator;

	@Before
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
