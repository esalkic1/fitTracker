package ba.unsa.etf.nwt.workout_service.exceptions;

import ba.unsa.etf.nwt.error_logging.exceptions.BaseServiceException;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;

import java.io.Serial;

public class ExerciseDetailsServiceException extends BaseServiceException {

    @Serial
    private static final long serialVersionUID = -8019517102390202287L;

    public ExerciseDetailsServiceException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}
