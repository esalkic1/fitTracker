package ba.unsa.etf.nwt.nutrition_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.nutrition_service.domain.User;
import ba.unsa.etf.nwt.nutrition_service.exceptions.UserServiceException;
import ba.unsa.etf.nwt.nutrition_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) throws UserServiceException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("User with ID " + id + " not found", ErrorType.ENTITY_NOT_FOUND));
    }

    public User createUser(User user) throws UserServiceException {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new UserServiceException("Failed to create user: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public void deleteUser(Long id) throws UserServiceException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("User with ID " + id + " not found", ErrorType.ENTITY_NOT_FOUND));

        userRepository.delete(user);
    }
}
