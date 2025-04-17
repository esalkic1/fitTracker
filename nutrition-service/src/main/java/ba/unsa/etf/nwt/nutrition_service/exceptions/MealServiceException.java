package ba.unsa.etf.nwt.nutrition_service.exceptions;

import ba.unsa.etf.nwt.error_logging.exceptions.BaseServiceException;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;

import java.io.Serial;

public class MealServiceException extends BaseServiceException {
    @Serial
    private static final long serialVersionUID = 8035791522364502819L;

    public MealServiceException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}
