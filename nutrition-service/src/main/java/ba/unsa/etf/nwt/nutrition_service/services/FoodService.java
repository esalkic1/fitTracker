package ba.unsa.etf.nwt.nutrition_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.nutrition_service.domain.Food;
import ba.unsa.etf.nwt.nutrition_service.domain.Meal;
import ba.unsa.etf.nwt.nutrition_service.dto.FoodDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.FoodServiceException;
import ba.unsa.etf.nwt.nutrition_service.exceptions.MealServiceException;
import ba.unsa.etf.nwt.nutrition_service.repositories.FoodRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FoodService {
    private final FoodRepository foodRepository;
    private final MealService mealService;
    private final ModelMapper modelMapper;

    public FoodService(FoodRepository foodRepository, MealService mealService, ModelMapper modelMapper) {
        this.foodRepository = foodRepository;
        this.mealService = mealService;
        this.modelMapper = modelMapper;
    }

    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    public Food getFood(Long id) throws FoodServiceException {
        return foodRepository.findById(id)
                .orElseThrow(() -> new FoodServiceException("Food with ID " + id + " not found", ErrorType.ENTITY_NOT_FOUND));
    }

    public FoodDTO createFood(FoodDTO foodDTO) throws FoodServiceException {
        try {
            Meal meal = mealService.getMeal(foodDTO.getMealId());
            Food food = modelMapper.map(foodDTO, Food.class);
            food.setMeal(meal);

            Food savedFood = foodRepository.save(food);
            return modelMapper.map(savedFood, FoodDTO.class);
        } catch (Exception e) {
            throw new FoodServiceException("Failed to create food: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public FoodDTO updateFood(Long id, FoodDTO foodDTO) throws FoodServiceException {
        Food existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new FoodServiceException("Food with ID " + id + " not found", ErrorType.ENTITY_NOT_FOUND));

        try {
            if (foodDTO.getName() != null) {
                existingFood.setName(foodDTO.getName());
            }

            if (foodDTO.getCalories() != null) {
                existingFood.setCalories(foodDTO.getCalories());
            }

            if (foodDTO.getMealId() != null) {
                Meal meal = mealService.getMeal(foodDTO.getMealId());
                existingFood.setMeal(meal);
            }

            Food updatedFood = foodRepository.save(existingFood);
            FoodDTO result = modelMapper.map(updatedFood, FoodDTO.class);
            result.setMealId(updatedFood.getMeal().getId());

            return result;
        } catch (Exception e) {
            throw new FoodServiceException("Failed to update food: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public FoodDTO patchFood(Long id, Map<String, String> updates) throws FoodServiceException {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new FoodServiceException("Food with ID " + id + " not found", ErrorType.ENTITY_NOT_FOUND));

        for (Map.Entry<String, String> entry : updates.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            switch (key) {
                case "name" -> food.setName(value);
                case "calories" -> food.setCalories(Double.valueOf(value));
                case "mealId" -> {
                    try {
                        Meal meal = mealService.getMeal(Long.valueOf(value));
                        food.setMeal(meal);
                    } catch (MealServiceException e) {
                        throw new FoodServiceException("Meal with ID " + value + " not found", ErrorType.ENTITY_NOT_FOUND);
                    }
                }
                default -> throw new FoodServiceException("Field '" + key + "' is not pachable", ErrorType.VALIDATION_FAILED);
            }
        }

        foodRepository.save(food);
        return modelMapper.map(food, FoodDTO.class);
    }

    public void deleteFood(Long id) throws FoodServiceException {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new FoodServiceException("Food with ID " + id + " not found", ErrorType.ENTITY_NOT_FOUND));

        foodRepository.delete(food);
    }

    public List<Food> findFoodsByCalorieRange(int minCalories, int maxCalories) {
        return foodRepository.findByCaloriesBetween(minCalories, maxCalories);
    }

    public List<Food> searchFoodsByName(String name) throws FoodServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new FoodServiceException("Search name cannot be empty", ErrorType.VALIDATION_FAILED);
        }

        return foodRepository.findByNameContainingIgnoreCase(name.trim());
    }

    public List<Food> searchFoodsByCalorieRange(Integer minCalories, Integer maxCalories) throws FoodServiceException {
        return foodRepository.findByCaloriesBetween(minCalories, maxCalories);
    }
}
