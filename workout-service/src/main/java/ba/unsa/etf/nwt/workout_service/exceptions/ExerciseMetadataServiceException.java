package ba.unsa.etf.nwt.workout_service.exceptions;

import ba.unsa.etf.nwt.error_logging.exceptions.BaseServiceException;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;

import java.io.Serial;

public class ExerciseMetadataServiceException extends BaseServiceException {

    @Serial
    private static final long serialVersionUID = 7871027404322023303L;

    public ExerciseMetadataServiceException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}
