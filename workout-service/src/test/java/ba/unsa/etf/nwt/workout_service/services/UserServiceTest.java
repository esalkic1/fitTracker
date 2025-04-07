package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.exceptions.UserServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws UserServiceException {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getId());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_WhenExceptionOccurs_ShouldThrowUserServiceException() {
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        UserServiceException exception = assertThrows(
                UserServiceException.class,
                () -> userService.getAllUsers()
        );

        assertEquals("Failed to retrieve users: Database error", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() throws UserServiceException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserServiceException exception = assertThrows(
                UserServiceException.class,
                () -> userService.getUserById(99L)
        );

        assertEquals("Could not find user with id: 99", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void createUser_WithValidData_ShouldCreateAndReturnUser() throws UserServiceException {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_WhenExceptionOccurs_ShouldThrowUserServiceException() {
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        UserServiceException exception = assertThrows(
                UserServiceException.class,
                () -> userService.createUser(user)
        );

        assertEquals("Failed to create user: Database error", exception.getMessage());
        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() throws UserServiceException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserServiceException exception = assertThrows(
                UserServiceException.class,
                () -> userService.deleteUser(99L)
        );

        assertEquals("Could not find user with id: 99", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).delete(any(User.class));
    }
}