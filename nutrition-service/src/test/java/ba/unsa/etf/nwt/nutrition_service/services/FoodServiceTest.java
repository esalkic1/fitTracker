package ba.unsa.etf.nwt.nutrition_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.nutrition_service.domain.Food;
import ba.unsa.etf.nwt.nutrition_service.domain.Meal;
import ba.unsa.etf.nwt.nutrition_service.domain.User;
import ba.unsa.etf.nwt.nutrition_service.dto.FoodDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.FoodServiceException;
import ba.unsa.etf.nwt.nutrition_service.exceptions.MealServiceException;
import ba.unsa.etf.nwt.nutrition_service.repositories.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private MealService mealService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FoodService foodService;

    private User user;
    private Meal meal;
    private Food food;
    private FoodDTO foodDTO;

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

        meal.setFoods(Arrays.asList(food));
        user.setMeals(Arrays.asList(meal));
        food.setMeal(meal);

        foodDTO = new FoodDTO();
        foodDTO.setId(1L);
        foodDTO.setName("Pancakes");
        foodDTO.setCalories(200.0);
        foodDTO.setMealId(1L);
    }

    @Test
    void getAllFoods_ShouldReturnAllFoods() {
        List<Food> foodList = Arrays.asList(food);
        when(foodRepository.findAll()).thenReturn(foodList);

        List<Food> result = foodService.getAllFoods();

        assertEquals(1, result.size());
        verify(foodRepository, times(1)).findAll();
    }

    @Test
    void getFood_WithValidId_ShouldReturnFood() throws FoodServiceException {
        Long foodId = 1L;
        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));

        Food result = foodService.getFood(foodId);

        assertEquals(food, result);
        verify(foodRepository, times(1)).findById(foodId);
    }

    @Test
    void getFood_WithInvalidId_ShouldThrowException() {
        Long invalidId = 999L;
        when(foodRepository.findById(invalidId)).thenReturn(Optional.empty());

        FoodServiceException exception = assertThrows(FoodServiceException.class,
                () -> foodService.getFood(invalidId));

        assertEquals("Food with ID 999 not found", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(foodRepository, times(1)).findById(invalidId);
    }

    @Test
    void createFood_WithValidData_ShouldReturnCreatedFood() throws FoodServiceException, MealServiceException {
        when(modelMapper.map(foodDTO, Food.class)).thenReturn(food);
        when(mealService.getMeal(1L)).thenReturn(meal);
        when(foodRepository.save(food)).thenReturn(food);
        when(modelMapper.map(food, FoodDTO.class)).thenReturn(foodDTO);

        FoodDTO result = foodService.createFood(foodDTO);

        assertEquals(foodDTO, result);
        verify(mealService, times(1)).getMeal(1L);
        verify(foodRepository, times(1)).save(food);
    }

    @Test
    void createFood_WithInvalidMealId_ShouldThrowException() throws MealServiceException {
        FoodDTO invalidFoodDTO = new FoodDTO();
        invalidFoodDTO.setId(2L);
        invalidFoodDTO.setName("Apples");
        invalidFoodDTO.setCalories(100.0);
        invalidFoodDTO.setMealId(999L);

        when(mealService.getMeal(999L)).thenThrow(new MealServiceException("Meal with ID 999 not found", ErrorType.ENTITY_NOT_FOUND));

        FoodServiceException exception = assertThrows(FoodServiceException.class,
                () -> foodService.createFood(invalidFoodDTO));

        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        verify(mealService, times(1)).getMeal(999L);
        verify(foodRepository, never()).save(any());
    }

    @Test
    void updateFood_WithValidData_ShouldReturnUpdatedFood() throws MealServiceException, FoodServiceException {
        Long foodId = 1L;
        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));
        when(mealService.getMeal(1L)).thenReturn(meal);
        when(foodRepository.save(food)).thenReturn(food);
        when(modelMapper.map(food, FoodDTO.class)).thenReturn(foodDTO);

        FoodDTO result = foodService.updateFood(foodId, foodDTO);

        assertEquals(foodDTO, result);
        assertEquals("Pancakes", food.getName());
        assertEquals(200.0, food.getCalories());
        assertEquals(meal, food.getMeal());
        verify(foodRepository, times(1)).findById(foodId);
        verify(foodRepository, times(1)).save(food);
    }

    @Test
    void updateFood_WithInvalidMealId_ShouldThrowException() throws MealServiceException {
        Long foodId = 1L;
        FoodDTO invalidFoodDTO = new FoodDTO();
        invalidFoodDTO.setId(2L);
        invalidFoodDTO.setName("Apples");
        invalidFoodDTO.setCalories(100.0);
        invalidFoodDTO.setMealId(999L);

        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));
        when(mealService.getMeal(999L)).thenThrow(new MealServiceException("Meal with ID 999 not found", ErrorType.ENTITY_NOT_FOUND));

        FoodServiceException exception = assertThrows(FoodServiceException.class,
                () -> foodService.updateFood(foodId, invalidFoodDTO));

        assertEquals("Failed to update food: Meal with ID 999 not found", exception.getMessage());
        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        verify(mealService, times(1)).getMeal(999L);
        verify(foodRepository, never()).save(any());
    }

    @Test
    void patchFood_WithValidUpdates_ShouldReturnPatchedFood() throws MealServiceException, FoodServiceException {
        Long foodId = 1L;
        Map<String, String> updates = Map.of(
                "name", "Organic Apple",
                "calories", "60",
                "mealId", "2"
        );

        Meal newMeal = new Meal();
        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));
        when(mealService.getMeal(2L)).thenReturn(newMeal);
        when(foodRepository.save(food)).thenReturn(food);
        when(modelMapper.map(food, FoodDTO.class)).thenReturn(new FoodDTO());

        FoodDTO result = foodService.patchFood(foodId, updates);

        assertNotNull(result);
        assertEquals("Organic Apple", food.getName());
        assertEquals(60.0, food.getCalories());
        assertEquals(newMeal, food.getMeal());
        verify(foodRepository, times(1)).findById(foodId);
        verify(foodRepository, times(1)).save(food);
    }

    @Test
    void patchFood_WithInvalidField_ShouldThrowException() {
        Long invalidId = 999L;
        when(foodRepository.findById(invalidId)).thenReturn(Optional.empty());

        FoodServiceException exception = assertThrows(FoodServiceException.class,
                () -> foodService.deleteFood(invalidId));

        assertEquals("Food with ID 999 not found", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(foodRepository, times(1)).findById(invalidId);
        verify(foodRepository, never()).save(any());
    }

    @Test
    void deleteFood_WithValidId_ShouldDeleteFood() throws FoodServiceException {
        Long foodId = 1L;
        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));
        doNothing().when(foodRepository).delete(food);

        foodService.deleteFood(foodId);

        verify(foodRepository, times(1)).findById(foodId);
        verify(foodRepository, times(1)).delete(food);
    }

    @Test
    void deleteFood_WithInvalidId_ShouldThrowException() {
        Long invalidId = 999L;
        when(foodRepository.findById(invalidId)).thenReturn(Optional.empty());

        FoodServiceException exception = assertThrows(FoodServiceException.class,
                () -> foodService.deleteFood(invalidId));

        assertEquals("Food with ID 999 not found", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(foodRepository, times(1)).findById(invalidId);
        verify(foodRepository, never()).delete(any());
    }

    @Test
    void findFoodsByCalorieRange_WithValidRange_ShouldReturnMatchingFoods() {
        int min = 50;
        int max = 250;
        when(foodRepository.findByCaloriesBetween(min, max)).thenReturn(List.of(food));

        List<Food> result = foodService.findFoodsByCalorieRange(min, max);

        assertEquals(1, result.size());
        verify(foodRepository, times(1)).findByCaloriesBetween(min, max);
    }

    @Test
    void searchFoodsByCalorieRange_WithInvalidRange_ShouldReturnEmptyList() throws FoodServiceException {
        int min = 100;
        int max = 50;
        when(foodRepository.findByCaloriesBetween(min, max)).thenReturn(List.of());

        List<Food> result = foodService.searchFoodsByCalorieRange(min, max);

        assertEquals(0, result.size());
        verify(foodRepository, times(1)).findByCaloriesBetween(min, max);
    }

    @Test
    void searchFoodsByName_WithValidName_ShouldReturnMatchingFoods() throws FoodServiceException {
        String name = "cake";
        when(foodRepository.findByNameContainingIgnoreCase(name)).thenReturn(List.of(food));

        List<Food> result = foodService.searchFoodsByName(name);

        assertEquals(1, result.size());
        verify(foodRepository, times(1)).findByNameContainingIgnoreCase(name);
    }

    @Test
    void searchFoodsByName_WithNonExistingName_ShouldReturnEmptyList() throws FoodServiceException {
        String name = "nonexistent";

        when(foodRepository.findByNameContainingIgnoreCase(name)).thenReturn(List.of());

        List<Food> result = foodService.searchFoodsByName(name);

        assertEquals(0, result.size());
        verify(foodRepository, times(1)).findByNameContainingIgnoreCase(name);
    }
}
