package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.workout_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository){
        this.userRepository = userRepository;
    }
}
