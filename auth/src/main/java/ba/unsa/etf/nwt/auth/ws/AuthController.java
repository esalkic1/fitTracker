package ba.unsa.etf.nwt.auth.ws;

import ba.unsa.etf.nwt.auth.domain.User;
import ba.unsa.etf.nwt.auth.exceptions.UserServiceException;
import ba.unsa.etf.nwt.auth.services.AuthService;
import ba.unsa.etf.nwt.auth.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
	private final AuthService authService;
	private final UserService userService;

	public AuthController(final AuthService authService, final UserService userService) {
		this.authService = authService;
		this.userService = userService;
	}

	@PostMapping("register")
	public ResponseEntity<?> register(@RequestBody final AuthRequest authRequest) throws UserServiceException {
		return ResponseEntity.ok(authService.register(new User(authRequest.email(), authRequest.password())));
	}

	@PostMapping("login")
	public ResponseEntity<?> login(@RequestBody final AuthRequest authRequest) throws UserServiceException {
		return ResponseEntity.ok(authService.login(authRequest.email(), authRequest.password()));
	}

	@GetMapping("validate")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> validateToken() throws UserServiceException {
		// If the filter does not reject it's valid by default
		final String username = SecurityContextHolder.getContext().getAuthentication().getName();
		final User user = userService.findByUsername(username);
		return ResponseEntity.ok().body(new ValidationResponse(
				user.getEmail(),
				user.getRole().name(),
				user.getHandle()
		));
	}

	public record AuthRequest(String email, String password) {}

	public record ValidationResponse(String username, String role, UUID handle) {}
}
