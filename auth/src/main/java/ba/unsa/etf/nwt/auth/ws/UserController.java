package ba.unsa.etf.nwt.auth.ws;

import ba.unsa.etf.nwt.auth.exceptions.UserServiceException;
import ba.unsa.etf.nwt.auth.services.UserService;
import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
	private final UserService userService;

	public UserController(final UserService userService) {
		this.userService = userService;
	}

	@GetMapping("{id}")
	public ResponseEntity<?> getUser(@PathVariable final UUID id) {
		try {
			return ResponseEntity.ok(userService.get(id));
		} catch (UserServiceException e) {
			return ResponseEntity.badRequest().body(
					ErrorResponse.from(e.getErrorType(), e.getMessage())
			);
		}
	}
}
