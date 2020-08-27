package hw.ebank.model.api.request;

import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class MoneyOperation {

	@NotNull
	@Positive
	@Digits(integer = 8, fraction = 2)
	private BigDecimal amount;

	public MoneyOperation() {
	}

	public MoneyOperation(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

}
