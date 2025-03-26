package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.workout_service.services.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(final UserService userService){
        this.userService = userService;
    }
}
