package ba.unsa.etf.nwt.workout_service.exceptions;

import ba.unsa.etf.nwt.error_logging.exceptions.BaseServiceException;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;

import java.io.Serial;

public class WorkoutServiceException extends BaseServiceException {

    @Serial
    private static final long serialVersionUID = 2784694237222497263L;

    public WorkoutServiceException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}
