package ba.unsa.etf.nwt.workout_service.exceptions;

import ba.unsa.etf.nwt.error_logging.exceptions.BaseServiceException;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;

import java.io.Serial;

public class UserServiceException extends BaseServiceException {

    @Serial
    private static final long serialVersionUID = -7404179256298882902L;

    public UserServiceException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}
