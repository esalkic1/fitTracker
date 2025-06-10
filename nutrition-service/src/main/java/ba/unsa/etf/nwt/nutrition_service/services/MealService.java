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
import ba.unsa.etf.nwt.nutrition_service.repositories.FoodRepository;
import ba.unsa.etf.nwt.nutrition_service.repositories.MealRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class MealService {
    private final MealRepository mealRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final FoodRepository foodRepository;
    private final WorkoutClient workoutClient;

    public MealService(MealRepository mealRepository, UserService userService, ModelMapper modelMapper, FoodRepository foodRepository, WorkoutClient workoutClient) {
        this.mealRepository = mealRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.foodRepository = foodRepository;
        this.workoutClient = workoutClient;
    }

    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    public Meal getMeal(Long id) throws MealServiceException {
        return mealRepository.findById(id)
                .orElseThrow(() -> new MealServiceException("Meal with ID " + id + " not found", ErrorType.ENTITY_NOT_FOUND));
    }

    public MealDTO createMeal(MealDTO mealDTO) throws MealServiceException {
        try {
            User user = userService.getUser(mealDTO.getUserId());
            Meal meal = modelMapper.map(mealDTO, Meal.class);
            meal.setUser(user);
            meal.setDate(mealDTO.getDate());

            Meal savedMeal = mealRepository.save(meal);
            return modelMapper.map(savedMeal, MealDTO.class);
        } catch (Exception e) {
            throw new MealServiceException("Failed to create meal: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public MealDTO updateMeal(Long id, MealDTO mealDTO) throws MealServiceException {
        Meal existingMeal = mealRepository.findById(id)
                .orElseThrow(() -> new MealServiceException("Meal with ID " + id + " not found", ErrorType.ENTITY_NOT_FOUND));

        try {
            if (mealDTO.getName() != null) {
                existingMeal.setName(mealDTO.getName());
            }

            if (mealDTO.getUserId() != null) {
                User user = userService.getUser(mealDTO.getUserId());
                existingMeal.setUser(user);
            }

            if (mealDTO.getDate() != null) {
                existingMeal.setDate(mealDTO.getDate());
            }

            Meal updatedMeal = mealRepository.save(existingMeal);
            MealDTO result = modelMapper.map(updatedMeal, MealDTO.class);
            result.setUserId(updatedMeal.getUser().getId());

            return result;
        } catch (Exception e) {
            throw new MealServiceException("Failed to update meal: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public void deleteMeal(Long id) throws MealServiceException {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new MealServiceException("Meal with ID " + id + " not found", ErrorType.ENTITY_NOT_FOUND));

        mealRepository.delete(meal);
    }

    public List<Meal> searchMealsByName(String name) throws MealServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new MealServiceException("Search name cannot be empty", ErrorType.VALIDATION_FAILED);
        }

        return mealRepository.findByNameContainingIgnoreCase(name.trim());
    }

    @Transactional
    public MealDTO createMealWithFoods(MealWithFoodDTO mealWithFoodDTO) throws MealServiceException {
        try {
            User user = userService.getUser(mealWithFoodDTO.getMeal().getUserId());

            Meal meal = modelMapper.map(mealWithFoodDTO.getMeal(), Meal.class);
            meal.setUser(user);
            meal.setDate(mealWithFoodDTO.getMeal().getDate());
            Meal savedMeal = mealRepository.save(meal);

            for (FoodDTO foodDTO : mealWithFoodDTO.getFoods()) {
                Food food = modelMapper.map(foodDTO, Food.class);
                food.setMeal(savedMeal);
                foodRepository.save(food);
            }

            return modelMapper.map(savedMeal, MealDTO.class);
        } catch (Exception e) {
            throw new MealServiceException("Failed to create meal with foods: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public boolean hasMealBeforeWorkout(UUID userUuid, Instant workoutTime) {
        Instant threeHoursBefore = workoutTime.minus(Duration.ofHours(3));
        return !mealRepository.findByUserUuidAndDateBetween(userUuid, threeHoursBefore, workoutTime).isEmpty();
    }

    private Food findOrCreateFood(String name, Double calories) {
        return foodRepository.findByName(name).orElseGet(() -> {
            Food food = new Food(name, calories, null);
            return foodRepository.save(food);
        });
    }

    private Meal suggestLighterMeal(User user, Instant date) {
        Food salad = findOrCreateFood("Salad", 150.0);
        Food chickenBreast = findOrCreateFood("Chicken Breast", 200.0);

        Meal meal = new Meal("Lighter Meal", user, List.of(salad, chickenBreast), date);
        salad.setMeal(meal);
        chickenBreast.setMeal(meal);

        return mealRepository.save(meal);
    }

    private Meal suggestModerateMeal(User user, Instant date) {
        Food rice = findOrCreateFood("Rice", 400.0);
        Food tuna = findOrCreateFood("Tuna", 300.0);

        Meal meal = new Meal("Moderate Calorie Meal", user, List.of(rice, tuna), date);
        rice.setMeal(meal);
        tuna.setMeal(meal);

        return mealRepository.save(meal);
    }

    private Meal suggestHigherCalorieMeal(User user, Instant date) {
        Food pasta = findOrCreateFood("Pasta", 800.0);
        Food beef = findOrCreateFood("Beef Steak", 600.0);

        Meal meal = new Meal("Higher Calorie Meal", user, List.of(pasta, beef), date);
        pasta.setMeal(meal);
        beef.setMeal(meal);

        return mealRepository.save(meal);
    }

    public Meal suggestMealBasedOnWorkout(Long userId, Instant date) throws MealServiceException {
        try {
            String intensity = workoutClient.getWorkoutIntensityLevel(userId, date.toString());
            User user = userService.getUser(userId);

            return switch (intensity) {
                case "LIGHT" -> suggestLighterMeal(user, date);
                case "MODERATE" -> suggestModerateMeal(user, date);
                case "INTENSE" -> suggestHigherCalorieMeal(user, date);
                default -> throw new MealServiceException("Unknown workout intensity level", ErrorType.INTERNAL_ERROR);
            };
        } catch (Exception e) {
            throw new MealServiceException("Failed to suggest meal: " + e.getMessage(), ErrorType.INTERNAL_ERROR);
        }
    }

    public List<Meal> getByUserAndRange(final Long userId, final Instant from, final Instant to) {
        return mealRepository.findByUserIdAndDateBetween(userId, from, to);
    }
}
