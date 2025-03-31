package ba.unsa.etf.nwt.auth.ws;

import ba.unsa.etf.nwt.auth.domain.User;
import ba.unsa.etf.nwt.auth.exceptions.UserServiceException;
import ba.unsa.etf.nwt.auth.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(final AuthService authService) {
		this.authService = authService;
	}

	@PostMapping
	public ResponseEntity<?> register(@RequestBody final AuthRequest authRequest) throws UserServiceException {
		return ResponseEntity.ok(authService.register(new User(authRequest.email(), authRequest.password())));
	}

	@GetMapping
	public ResponseEntity<?> login(@RequestBody final AuthRequest authRequest) throws UserServiceException {
		return ResponseEntity.ok(authService.login(authRequest.email(), authRequest.password()));
	}

	public record AuthRequest(String email, String password) {}
}
