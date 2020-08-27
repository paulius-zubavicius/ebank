package hw.ebank.model.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

public class PasswordPolicyValidator implements ConstraintValidator<PasswordPolicy, String> {

	private static final int MIN_SIZE = 8;
	private static final char[] ONE_OF_SPEC_CAHRS = new char[] { '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '\'',
			'\\', '/', '"', '?' };

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {

		if (StringUtils.isBlank(password)) {
			return false;
		}

		if (password.length() < MIN_SIZE) {
			return false;
		}

		if (!StringUtils.containsAny(password, ONE_OF_SPEC_CAHRS)) {
			return false;
		}

		return true;
	}

}
