package ba.unsa.etf.nwt.auth.ws;

import ba.unsa.etf.nwt.auth.domain.User;
import ba.unsa.etf.nwt.auth.exceptions.UserServiceException;
import ba.unsa.etf.nwt.auth.services.AuthService;
import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(final AuthService authService) {
		this.authService = authService;
	}

	@PostMapping
	public ResponseEntity<?> register(@RequestBody final AuthRequest authRequest) {
		try {
			return ResponseEntity.ok(authService.register(new User(authRequest.email(), authRequest.password())));
		} catch (final UserServiceException e) {
			return ResponseEntity.badRequest().body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
		}
	}

	@GetMapping
	public ResponseEntity<?> login(@RequestBody final AuthRequest authRequest) {
		try {
			return ResponseEntity.ok(authService.login(authRequest.email(), authRequest.password()));
		} catch (final UserServiceException e) {
			return ResponseEntity.badRequest().body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
		}
	}

	public record AuthRequest(String email, String password) {}
}
