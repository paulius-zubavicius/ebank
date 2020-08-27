package hw.ebank.exceptions;

import org.springframework.http.HttpStatus;

public class EBankException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private HttpStatus code;

	public EBankException(HttpStatus code, String msgCode) {
		super(msgCode);
		this.code = code;
	}

	public HttpStatus getCode() {
		return code;
	}

}
