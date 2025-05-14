package ba.unsa.etf.nwt.nutrition_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.nutrition_service.clients.WorkoutClient;
import ba.unsa.etf.nwt.nutrition_service.domain.Food;
import ba.unsa.etf.nwt.nutrition_service.domain.Meal;
import ba.unsa.etf.nwt.nutrition_service.domain.User;
import ba.unsa.etf.nwt.nutrition_service.dto.FoodDTO;
import ba.unsa.etf.nwt.nutrition_service.dto.MealDTO;
import ba.unsa.etf.nwt.nutrition_service.dto.MealWithFoodDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.MealServiceException;
import ba.unsa.etf.nwt.nutrition_service.exceptions.UserServiceException;
import ba.unsa.etf.nwt.nutrition_service.repositories.FoodRepository;
import ba.unsa.etf.nwt.nutrition_service.repositories.MealRepository;
import ba.unsa.etf.nwt.nutrition_service.validators.MealValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private MealValidator mealValidator;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private WorkoutClient workoutClient;

    @InjectMocks
    private MealService mealService;

    private User user;
    private Instant date;
    private Food food;
    private Meal meal;
    private MealDTO mealDTO;
    private FoodDTO foodDTO;
    private MealWithFoodDTO mealWithFoodDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        date = Instant.now();

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

        foodDTO = new FoodDTO();
        foodDTO.setId(1L);
        foodDTO.setName("Pancakes");
        foodDTO.setCalories(200.0);
        foodDTO.setMealId(1L);

        mealWithFoodDTO = new MealWithFoodDTO();
        mealWithFoodDTO.setMeal(mealDTO);
        mealWithFoodDTO.setFoods(Arrays.asList(foodDTO));
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
    void createMealWithFoods_WithValidData_ShouldReturnCreatedMeal() throws UserServiceException, MealServiceException {
        when(userService.getUser(1L)).thenReturn(user);
        when(modelMapper.map(mealDTO, Meal.class)).thenReturn(meal);
        when(modelMapper.map(foodDTO, Food.class)).thenReturn(food);
        when(mealRepository.save(any(Meal.class))).thenReturn(meal);
        when(modelMapper.map(meal, MealDTO.class)).thenReturn(mealDTO);

        MealDTO result = mealService.createMealWithFoods(mealWithFoodDTO);

        assertEquals(mealDTO, result);
        verify(userService, times(1)).getUser(1L);
        verify(mealRepository, times(1)).save(any(Meal.class));
        verify(modelMapper, times(1)).map(mealDTO, Meal.class);
        verify(modelMapper, times(1)).map(meal, MealDTO.class);
    }

    @Test
    void createMealWithFoods_WithInvalidUserId_ShouldThrowException() throws UserServiceException {
        MealDTO invalidMealDTO = new MealDTO();
        invalidMealDTO.setId(1L);
        invalidMealDTO.setName("Pancakes");
        invalidMealDTO.setUserId(999L);

        MealWithFoodDTO invalidMealWithFoodDTO = new MealWithFoodDTO();
        invalidMealWithFoodDTO.setMeal(invalidMealDTO);
        invalidMealWithFoodDTO.setFoods(Arrays.asList());

        when(userService.getUser(999L)).thenThrow(
                new UserServiceException("User with ID 999 not found", ErrorType.ENTITY_NOT_FOUND)
        );

        MealServiceException exception = assertThrows(MealServiceException.class,
                () -> mealService.createMealWithFoods(invalidMealWithFoodDTO));

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

    @Test
    void suggestMealBasedOnWorkout_LightIntensity_ReturnsLighterMeal() throws UserServiceException, MealServiceException {
        when(workoutClient.getWorkoutIntensityLevel(anyLong(), anyString())).thenReturn("LIGHT");
        when(userService.getUser(1L)).thenReturn(user);
        when(foodRepository.findByName("Salad")).thenReturn(Optional.empty());
        when(foodRepository.findByName("Chicken Breast")).thenReturn(Optional.empty());

        Food salad = new Food("Salad", 150.0, null);
        Food chicken = new Food("Chicken Breast", 200.0, null);
        Meal expectedMeal = new Meal("Lighter Meal", user, List.of(salad, chicken), date);

        when(foodRepository.save(any(Food.class))).thenReturn(salad).thenReturn(chicken);
        when(mealRepository.save(any(Meal.class))).thenReturn(expectedMeal);

        Meal result = mealService.suggestMealBasedOnWorkout(1L, date);

        assertNotNull(result);
        assertEquals("Lighter Meal", result.getName());
        assertEquals(2, result.getFoods().size());
        assertEquals("Salad", result.getFoods().get(0).getName());
        assertEquals("Chicken Breast", result.getFoods().get(1).getName());
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    void suggestMealBasedOnWorkout_ModerateIntensity_ReturnsModerateMeal() throws UserServiceException, MealServiceException {
        when(workoutClient.getWorkoutIntensityLevel(anyLong(), anyString())).thenReturn("MODERATE");
        when(userService.getUser(1L)).thenReturn(user);
        when(foodRepository.findByName("Rice")).thenReturn(Optional.empty());
        when(foodRepository.findByName("Tuna")).thenReturn(Optional.empty());

        Food rice = new Food("Rice", 400.0, null);
        Food tuna = new Food("Tuna", 300.0, null);
        Meal expectedMeal = new Meal("Moderate Calorie Meal", user, List.of(rice, tuna), date);

        when(foodRepository.save(any(Food.class))).thenReturn(rice).thenReturn(tuna);
        when(mealRepository.save(any(Meal.class))).thenReturn(expectedMeal);

        Meal result = mealService.suggestMealBasedOnWorkout(1L, date);

        assertNotNull(result);
        assertEquals("Moderate Calorie Meal", result.getName());
        assertEquals(2, result.getFoods().size());
        assertEquals("Rice", result.getFoods().get(0).getName());
        assertEquals("Tuna", result.getFoods().get(1).getName());
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    void suggestMealBasedOnWorkout_IntenseIntensity_ReturnsHigherCalorieMeal() throws UserServiceException, MealServiceException {
        when(workoutClient.getWorkoutIntensityLevel(anyLong(), anyString())).thenReturn("INTENSE");
        when(userService.getUser(1L)).thenReturn(user);
        when(foodRepository.findByName("Pasta")).thenReturn(Optional.empty());
        when(foodRepository.findByName("Beef Steak")).thenReturn(Optional.empty());

        Food pasta = new Food("Pasta", 800.0, null);
        Food beef = new Food("Beef Steak", 600.0, null);
        Meal expectedMeal = new Meal("Higher Calorie Meal", user, List.of(pasta, beef), date);

        when(foodRepository.save(any(Food.class))).thenReturn(pasta).thenReturn(beef);
        when(mealRepository.save(any(Meal.class))).thenReturn(expectedMeal);

        Meal result = mealService.suggestMealBasedOnWorkout(1L, date);

        assertNotNull(result);
        assertEquals("Higher Calorie Meal", result.getName());
        assertEquals(2, result.getFoods().size());
        assertEquals("Pasta", result.getFoods().get(0).getName());
        assertEquals("Beef Steak", result.getFoods().get(1).getName());
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    void suggestMealBasedOnWorkout_UnknownIntensity_ThrowsException() throws UserServiceException {
        when(workoutClient.getWorkoutIntensityLevel(anyLong(), anyString())).thenReturn("INVALID");
        when(userService.getUser(1L)).thenReturn(user);

        MealServiceException exception = assertThrows(MealServiceException.class,
                () -> mealService.suggestMealBasedOnWorkout(1L, date));

        assertEquals("Failed to suggest meal: Unknown workout intensity level", exception.getMessage());
        assertEquals(ErrorType.INTERNAL_ERROR, exception.getErrorType());
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void suggestMealBasedOnWorkout_UserNotFound_ThrowsException() throws UserServiceException {
        when(workoutClient.getWorkoutIntensityLevel(anyLong(), anyString())).thenReturn("LIGHT");
        when(userService.getUser(1L)).thenThrow(new UserServiceException("User with ID " + user.getId() + " not found", ErrorType.ENTITY_NOT_FOUND));

        MealServiceException exception = assertThrows(MealServiceException.class,
                () -> mealService.suggestMealBasedOnWorkout(1L, date));

        assertEquals("Failed to suggest meal: User with ID " + user.getId() + " not found", exception.getMessage());
        assertEquals(ErrorType.INTERNAL_ERROR, exception.getErrorType());
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void suggestMealBasedOnWorkout_ExistingFood_ReturnsMealWithExistingFood() throws UserServiceException, MealServiceException {
        when(workoutClient.getWorkoutIntensityLevel(anyLong(), anyString())).thenReturn("LIGHT");
        when(userService.getUser(1L)).thenReturn(user);

        Food existingSalad = new Food("Salad", 150.0, null);
        when(foodRepository.findByName("Salad")).thenReturn(Optional.of(existingSalad));
        when(foodRepository.findByName("Chicken Breast")).thenReturn(Optional.empty());

        Food chicken = new Food("Chicken Breast", 200.0, null);
        Meal expectedMeal = new Meal("Lighter Meal", user, List.of(existingSalad, chicken), date);

        when(foodRepository.save(any(Food.class))).thenReturn(chicken);
        when(mealRepository.save(any(Meal.class))).thenReturn(expectedMeal);

        Meal result = mealService.suggestMealBasedOnWorkout(1L, date);

        assertNotNull(result);
        assertEquals("Lighter Meal", result.getName());
        assertEquals(2, result.getFoods().size());
        assertEquals("Salad", result.getFoods().get(0).getName());
        assertEquals("Chicken Breast", result.getFoods().get(1).getName());
        verify(foodRepository, times(1)).save(any(Food.class));
    }
}
