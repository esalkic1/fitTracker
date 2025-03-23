package ba.unsa.etf.nwt.error_logging.model;

public enum ErrorType {
	UNAUTHORIZED(401),
	VALIDATION_FAILED(400),
	ENTITY_NOT_FOUND(400),
	ALREADY_EXISTS(400),
	INTERNAL_ERROR(500);
	// add others as necessary

	private final Integer statusCode;

	ErrorType(final Integer statusCode) {
		this.statusCode = statusCode;
	}

	public Integer getStatusCode() {
		return statusCode;
	}
}
