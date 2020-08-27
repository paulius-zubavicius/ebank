package hw.ebank.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import hw.ebank.model.api.response.ErrorMsg;

@ControllerAdvice
public class ErrorHandler {
	private static Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<ErrorMsg> onError(final Throwable throwable) {

		logByExceptionClass(throwable);

		return bodyBuilderByExceptionClass(throwable)
				.body(new ErrorMsg(throwable.getMessage(), throwable.getClass().getCanonicalName()));
	}

	private BodyBuilder bodyBuilderByExceptionClass(Throwable throwable) {

		if (throwable instanceof MethodArgumentNotValidException)
			return ResponseEntity.status(BAD_REQUEST);

		if (throwable instanceof EBankException)
			return ResponseEntity.status(((EBankException) throwable).getCode());

		return ResponseEntity.status(INTERNAL_SERVER_ERROR);
	}

	private void logByExceptionClass(Throwable throwable) {

		if (throwable instanceof MethodArgumentNotValidException)
			return;

		LOG.error("Error occurred: ", throwable);

	}

}
