package hw.ebank.model.api.response;

import java.math.BigDecimal;
import java.util.List;

public class StatementPage {

	private Integer page;
	private Integer pagesCount;
	private BigDecimal balanceCurrent;

	private List<OperationEntry> entries;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPagesCount() {
		return pagesCount;
	}

	public void setPagesCount(Integer pagesCount) {
		this.pagesCount = pagesCount;
	}

	public List<OperationEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<OperationEntry> entries) {
		this.entries = entries;
	}

	public BigDecimal getBalanceCurrent() {
		return balanceCurrent;
	}

	public void setBalanceCurrent(BigDecimal balanceCurrent) {
		this.balanceCurrent = balanceCurrent;
	}

}
