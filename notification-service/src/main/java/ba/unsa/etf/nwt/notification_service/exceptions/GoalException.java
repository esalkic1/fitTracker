package ba.unsa.etf.nwt.notification_service.exceptions;

import ba.unsa.etf.nwt.error_logging.exceptions.BaseServiceException;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;

public class GoalException extends BaseServiceException {

	public GoalException(final String message, final ErrorType errorType) {
		super(message, errorType);
	}
}
