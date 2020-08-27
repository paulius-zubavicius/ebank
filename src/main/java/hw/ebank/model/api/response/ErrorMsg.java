package hw.ebank.model.api.response;

public class ErrorMsg {

	private String msg;

	private String exception;

	public ErrorMsg(String msg, String exception) {
		this.msg = msg;
		this.exception = exception;
	}

	public String getMsg() {
		return msg;
	}

	public String getException() {
		return exception;
	}

}
