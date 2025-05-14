package ba.unsa.etf.nwt.nutrition_service.validators;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.nutrition_service.dto.FoodDTO;
import ba.unsa.etf.nwt.nutrition_service.dto.MealDTO;
import ba.unsa.etf.nwt.nutrition_service.dto.MealWithFoodDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.FoodServiceException;
import ba.unsa.etf.nwt.nutrition_service.exceptions.MealServiceException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class MealValidator {
    private FoodValidator foodValidator;

    public MealValidator(FoodValidator foodValidator) {
        this.foodValidator = foodValidator;
    }

    public void validateMealDTO(MealDTO mealDTO, String operation) throws MealServiceException {
        List<String> errors = new ArrayList<>();

        if (mealDTO.getName() == null) {
            errors.add("'name' is required");
        } else if (mealDTO.getName().length() < 3 || mealDTO.getName().length() > 100) {
            errors.add("'name' must be between 3 and 100 characters");
        }

        if (mealDTO.getUserId() == null) {
            errors.add("'userId' is required");
        } else if (mealDTO.getUserId() <= 0) {
            errors.add("'userId' must be a positive number");
        }

        if (mealDTO.getDate() == null) {
            errors.add("'date' is required");
        } else if (mealDTO.getDate().isAfter(Instant.now())) {
            errors.add("'date' cannot be in the future");
        }

        throwError(errors, operation);
    }

    public void validateMealWithFoodDTO(MealWithFoodDTO mealWithFoodDTO, String operation) throws MealServiceException, FoodServiceException {
        List<String> errors = new ArrayList<>();

        if (mealWithFoodDTO.getMeal() == null) {
            errors.add("'meal' is required");
        }
        if (mealWithFoodDTO.getFoods() == null) {
            errors.add("'foods' is required");
        }
        throwError(errors, operation);

        validateMealDTO(mealWithFoodDTO.getMeal(), operation);
        for (FoodDTO foodDTO : mealWithFoodDTO.getFoods()) {
            foodValidator.validateFoodDTO(foodDTO, operation);
        }
    }

    private void throwError(List<String> errors, String operation) throws MealServiceException {
        if (!errors.isEmpty()) {
            throw new MealServiceException("Failed to " + operation + " meal: " + String.join(", ", errors),
                    ErrorType.VALIDATION_FAILED);
        }
    }
}
