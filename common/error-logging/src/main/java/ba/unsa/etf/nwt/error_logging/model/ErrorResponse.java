package ba.unsa.etf.nwt.error_logging.model;

public record ErrorResponse(ErrorType type, String message, String stackTrace, Integer statusCode) {

	public static ErrorResponse from(final ErrorType errorType, final String message) {
		return new ErrorResponse(errorType, message, null, errorType.getStatusCode());
	}
}
