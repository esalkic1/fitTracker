package ba.unsa.etf.nwt.nutrition_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.nutrition_service.domain.Meal;
import ba.unsa.etf.nwt.nutrition_service.domain.User;
import ba.unsa.etf.nwt.nutrition_service.dto.MealDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.MealServiceException;
import ba.unsa.etf.nwt.nutrition_service.repositories.MealRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealService {
    private final MealRepository mealRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public MealService(MealRepository mealRepository, UserService userService, ModelMapper modelMapper) {
        this.mealRepository = mealRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
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
}
