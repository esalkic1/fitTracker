package ba.unsa.etf.nwt.nutrition_service.exceptions;

import ba.unsa.etf.nwt.error_logging.exceptions.BaseServiceException;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;

import java.io.Serial;

public class FoodServiceException extends BaseServiceException {
    @Serial
    private static final long serialVersionUID = -7936927196680067159L;

    public FoodServiceException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}
