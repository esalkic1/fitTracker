package ba.unsa.etf.nwt.error_logging.error_advice;

import ba.unsa.etf.nwt.error_logging.exceptions.BaseServiceException;
import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseErrorAdvice {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseErrorAdvice.class);

	@ExceptionHandler(BaseServiceException.class)
	public ResponseEntity<ErrorResponse> handleBaseServiceException(final BaseServiceException e) {
		LOGGER.error(e.getMessage(), e);
		return ResponseEntity.badRequest().body(ErrorResponse.from(e));
	}
}
