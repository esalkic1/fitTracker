package ba.unsa.etf.nwt.workout_service.exceptions;

import ba.unsa.etf.nwt.error_logging.exceptions.BaseServiceException;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;

import java.io.Serial;

public class ExerciseTemplateServiceException extends BaseServiceException {

    @Serial
    private static final long serialVersionUID = 7053084532415432991L;

    public ExerciseTemplateServiceException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}
