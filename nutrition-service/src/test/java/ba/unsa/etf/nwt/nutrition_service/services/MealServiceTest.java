package ba.unsa.etf.nwt.nutrition_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.nutrition_service.domain.Food;
import ba.unsa.etf.nwt.nutrition_service.domain.Meal;
import ba.unsa.etf.nwt.nutrition_service.domain.User;
import ba.unsa.etf.nwt.nutrition_service.dto.MealDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.MealServiceException;
import ba.unsa.etf.nwt.nutrition_service.exceptions.UserServiceException;
import ba.unsa.etf.nwt.nutrition_service.repositories.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MealService mealService;

    private User user;
    private Food food;
    private Meal meal;
    private MealDTO mealDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        meal = new Meal();
        meal.setId(1L);
        meal.setName("Breakfast");

        food = new Food();
        food.setId(1L);
        food.setName("Pancakes");
        food.setCalories(200.0);
        food.setMeal(meal);

        meal.setUser(user);
        meal.setFoods(Arrays.asList(food));
        user.setMeals(Arrays.asList(meal));

        mealDTO = new MealDTO();
        mealDTO.setId(1L);
        mealDTO.setName("Pancakes");
        mealDTO.setUserId(1L);
    }

    @Test
    void getAllMeals_ShouldReturnAllMeals() {
        when(mealRepository.findAll()).thenReturn(List.of(meal));

        List<Meal> result = mealService.getAllMeals();

        assertEquals(1, result.size());
        verify(mealRepository, times(1)).findAll();
    }

    @Test
    void getMeal_WithValidId_ShouldReturnMeal() throws MealServiceException {
        Long mealId = 1L;
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));

        Meal result = mealService.getMeal(mealId);

        assertEquals(meal, result);
        verify(mealRepository, times(1)).findById(mealId);
    }

    @Test
    void getMeal_WithInvalidId_ShouldThrowException() {
        Long invalidId = 999L;
        when(mealRepository.findById(invalidId)).thenReturn(Optional.empty());

        MealServiceException exception = assertThrows(MealServiceException.class,
                () -> mealService.getMeal(invalidId));

        assertEquals("Meal with ID 999 not found", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(mealRepository, times(1)).findById(invalidId);
    }

    @Test
    void createMeal_WithValidData_ShouldReturnCreatedMeal() throws UserServiceException, MealServiceException {
        when(modelMapper.map(mealDTO, Meal.class)).thenReturn(meal);
        when(userService.getUser(1L)).thenReturn(user);
        when(mealRepository.save(meal)).thenReturn(meal);
        when(modelMapper.map(meal, MealDTO.class)).thenReturn(mealDTO);

        MealDTO result = mealService.createMeal(mealDTO);

        assertEquals(mealDTO, result);
        assertEquals(user, meal.getUser());
        verify(userService, times(1)).getUser(1L);
        verify(mealRepository, times(1)).save(meal);
    }

    @Test
    void createMeal_WithInvalidUserId_ShouldThrowException() throws UserServiceException {
        Long mealId = 1L;
        MealDTO invalidMealDTO = new MealDTO();
        invalidMealDTO.setId(2L);
        invalidMealDTO.setName("Pancakes");
        invalidMealDTO.setUserId(999L);

        when(userService.getUser(999L)).thenThrow(new UserServiceException("User with ID 999 not found", ErrorType.ENTITY_NOT_FOUND));

        MealServiceException exception = assertThrows(MealServiceException.class,
                () -> mealService.createMeal(invalidMealDTO));

        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        verify(userService, times(1)).getUser(999L);
        verify(mealRepository, never()).save(any());
    }

    @Test
    void updateMeal_WithValidData_ShouldReturnUpdatedMeal() throws UserServiceException, MealServiceException {
        Long mealId = 1L;
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));
        when(userService.getUser(1L)).thenReturn(user);
        when(mealRepository.save(meal)).thenReturn(meal);
        when(modelMapper.map(meal, MealDTO.class)).thenReturn(mealDTO);

        MealDTO result = mealService.updateMeal(mealId, mealDTO);

        assertEquals(mealDTO, result);
        assertEquals("Pancakes", meal.getName());
        assertEquals(1, meal.getUser().getId());
        verify(mealRepository, times(1)).findById(mealId);
        verify(mealRepository, times(1)).save(meal);
    }

    @Test
    void updateMeal_WithInvalidUserId_ShouldThrowException() throws UserServiceException {
        Long mealId = 1L;
        MealDTO invalidMealDTO = new MealDTO();
        invalidMealDTO.setId(2L);
        invalidMealDTO.setName("Pancakes");
        invalidMealDTO.setUserId(999L);

        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));
        when(userService.getUser(999L)).thenThrow(new UserServiceException("User with ID 999 not found", ErrorType.ENTITY_NOT_FOUND));

        MealServiceException exception = assertThrows(MealServiceException.class,
                () -> mealService.updateMeal(mealId, invalidMealDTO));

        assertEquals("Failed to update meal: User with ID 999 not found", exception.getMessage());
        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        verify(userService, times(1)).getUser(999L);
        verify(mealRepository, never()).save(any());
    }

    @Test
    void deleteMeal_WithValidId_ShouldDeleteMeal() throws MealServiceException {
        Long mealId = 1L;
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));
        doNothing().when(mealRepository).delete(meal);

        mealService.deleteMeal(mealId);

        verify(mealRepository, times(1)).findById(mealId);
        verify(mealRepository, times(1)).delete(meal);
    }

    @Test
    void deleteMeal_WithInvalidId_ShouldThrowException() {
        Long invalidId = 999L;
        when(mealRepository.findById(invalidId)).thenReturn(Optional.empty());

        MealServiceException exception = assertThrows(MealServiceException.class,
                () -> mealService.deleteMeal(invalidId));

        assertEquals("Meal with ID 999 not found", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(mealRepository, times(1)).findById(invalidId);
        verify(mealRepository, never()).delete(any());
    }

    @Test
    void searchMealsByName_WithValidName_ShouldReturnMatchingMeals() throws MealServiceException {
        String name = "cake";
        when(mealRepository.findByNameContainingIgnoreCase(name)).thenReturn(List.of(meal));

        List<Meal> result = mealService.searchMealsByName(name);

        assertEquals(1, result.size());
        verify(mealRepository, times(1)).findByNameContainingIgnoreCase(name);
    }

    @Test
    void searchMealsByName_WithNonExistingName_ShouldReturnEmptyList() throws MealServiceException {
        String name = "nonexistent";

        when(mealRepository.findByNameContainingIgnoreCase(name)).thenReturn(List.of());

        List<Meal> result = mealService.searchMealsByName(name);

        assertEquals(0, result.size());
        verify(mealRepository, times(1)).findByNameContainingIgnoreCase(name);
    }
}
