package ba.unsa.etf.nwt.nutrition_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.nutrition_service.domain.Food;
import ba.unsa.etf.nwt.nutrition_service.domain.Meal;
import ba.unsa.etf.nwt.nutrition_service.domain.User;
import ba.unsa.etf.nwt.nutrition_service.exceptions.UserServiceException;
import ba.unsa.etf.nwt.nutrition_service.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private Meal meal;
    private User user;
    private Food food;

    @BeforeEach
    void setUp() {
        food = new Food();
        food.setId(1L);
        food.setName("Pancakes");
        food.setCalories(200.0);

        meal = new Meal();
        meal.setId(1L);
        meal.setName("Breakfast");

        user = new User();
        user.setId(1L);

        user.setMeals(Arrays.asList(meal));
        meal.setFoods(Arrays.asList(food));
        food.setMeal(meal);
        meal.setUser(user);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getId());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUser_WithValidId_ShouldReturnUser() throws UserServiceException {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(meal, result.getMeals().getFirst());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUser_WithInvalidId_ShouldThrowException() {
        Long invalidId = 999L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userService.getUser(invalidId));

        assertEquals("User with ID 999 not found", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findById(invalidId);
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() throws UserServiceException {
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Arrays.asList(meal), result.getMeals());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_WithInvalidData_ShouldThrowException() {
        User invalidUser = new User();
        when(userRepository.save(invalidUser)).thenThrow(new RuntimeException("Validation failed"));

        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userService.createUser(invalidUser));

        assertEquals("Failed to create user: Validation failed", exception.getMessage());
        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        verify(userRepository, times(1)).save(invalidUser);
    }

    @Test
    void deleteUser_WithValidId_ShouldDeleteUser() throws UserServiceException {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldThrowException() {
        Long invalidId = 999L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        UserServiceException exception = assertThrows(UserServiceException.class,
                () -> userService.deleteUser(invalidId));

        assertEquals("User with ID 999 not found", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(userRepository, times(1)).findById(invalidId);
        verify(userRepository, never()).delete(any());
    }
}