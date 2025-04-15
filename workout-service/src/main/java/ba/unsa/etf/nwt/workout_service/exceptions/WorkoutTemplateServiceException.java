package ba.unsa.etf.nwt.workout_service.exceptions;

import ba.unsa.etf.nwt.error_logging.exceptions.BaseServiceException;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;

import java.io.Serial;

public class WorkoutTemplateServiceException extends BaseServiceException {

    @Serial
    private static final long serialVersionUID = -7474408998023562474L;

    public WorkoutTemplateServiceException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}
