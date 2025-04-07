package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.exceptions.UserServiceException;
import ba.unsa.etf.nwt.workout_service.exceptions.WorkoutServiceException;
import ba.unsa.etf.nwt.workout_service.services.UserService;
import org.springframework.http.HttpStatus;
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

    @GetMapping("")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUser(@PathVariable final String id) {
        try {
            return ResponseEntity.ok(userService.getUserById(Long.parseLong(id)));
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createUser(@RequestBody final User user) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userService.createUser(user));
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@PathVariable final String id) {
        try {
            userService.deleteUser(Long.parseLong(id));
            return ResponseEntity.noContent().build();
        } catch (UserServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }
}