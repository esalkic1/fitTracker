package ba.unsa.etf.nwt.nutrition_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.nutrition_service.domain.User;
import ba.unsa.etf.nwt.nutrition_service.exceptions.UserServiceException;
import ba.unsa.etf.nwt.nutrition_service.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getAllUsers());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userService.getUser(id));
        } catch (UserServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(userService.createUser(user));
        } catch (UserServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity
                    .noContent()
                    .build();
        } catch (UserServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }
}
