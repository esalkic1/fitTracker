package ba.unsa.etf.nwt.error_logging.exceptions;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;

import java.io.Serial;

public class BaseServiceException extends Exception {

	@Serial
	private static final long serialVersionUID = -392982241246825333L;

	private final ErrorType errorType;

	public BaseServiceException(String message, ErrorType errorType) {
		super(message);

		this.errorType = errorType;
	}

	public BaseServiceException(String message) {
		this(message, null);
	}

	public ErrorType getErrorType() {
		return errorType;
	}
}
