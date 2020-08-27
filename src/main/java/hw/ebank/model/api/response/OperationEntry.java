package hw.ebank.model.api.response;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class OperationEntry {

	private BigDecimal amount;

	private Boolean deposit;

	private BigDecimal balanceBeforeOp;

	private ZonedDateTime timestamp;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Boolean getDeposit() {
		return deposit;
	}

	public void setDeposit(Boolean deposit) {
		this.deposit = deposit;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public BigDecimal getBalanceBeforeOp() {
		return balanceBeforeOp;
	}

	public void setBalanceBeforeOp(BigDecimal balanceBeforeOp) {
		this.balanceBeforeOp = balanceBeforeOp;
	}

}
