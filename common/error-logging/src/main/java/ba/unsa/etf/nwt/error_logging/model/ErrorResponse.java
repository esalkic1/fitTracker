package ba.unsa.etf.nwt.error_logging.model;

import ba.unsa.etf.nwt.error_logging.exceptions.BaseServiceException;

public record ErrorResponse(ErrorType type, String message, String stackTrace, Integer statusCode) {

	public static ErrorResponse from(final ErrorType errorType, final String message) {
		return new ErrorResponse(errorType, message, null, errorType.getStatusCode());
	}

	public static ErrorResponse from(final BaseServiceException exception) {
		return new ErrorResponse(exception.getErrorType(), exception.getMessage(), null, 400);
	}
}
