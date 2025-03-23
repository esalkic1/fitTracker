package ba.unsa.etf.nwt.auth.exceptions;

import ba.unsa.etf.nwt.error_logging.exceptions.BaseServiceException;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;

import java.io.Serial;

public class JwtException extends BaseServiceException {

	@Serial
	private static final long serialVersionUID = -875557501917043848L;

	public JwtException(String message) {
		super(message, ErrorType.INTERNAL_ERROR);
	}
}
