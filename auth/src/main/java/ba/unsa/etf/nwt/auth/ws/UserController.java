package ba.unsa.etf.nwt.auth.ws;

import ba.unsa.etf.nwt.auth.domain.User;
import ba.unsa.etf.nwt.auth.dto.UserUpdateRequest;
import ba.unsa.etf.nwt.auth.exceptions.UserServiceException;
import ba.unsa.etf.nwt.auth.services.UserService;
import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
	private final UserService userService;

	public UserController(final UserService userService) {
		this.userService = userService;
	}

	@GetMapping("{handle}")
	public ResponseEntity<?> getUser(@PathVariable final UUID handle) throws UserServiceException {
		return ResponseEntity.ok(userService.get(handle));
	}

	@DeleteMapping("{handle}")
	public ResponseEntity<?> deleteUser(@PathVariable final UUID handle) throws UserServiceException{
        userService.delete(handle);
        return ResponseEntity.noContent().build();
    }

	@GetMapping("")
	public ResponseEntity<?> getAllUsers() throws UserServiceException {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@PutMapping("{handle}")
	public ResponseEntity<?> updateUser(
			@PathVariable final UUID handle,
			@RequestBody final UserUpdateRequest request) throws UserServiceException {
		User updatedUser = userService.updateUser(handle, request);
		return ResponseEntity.ok(updatedUser);
	}
}
