package ba.unsa.etf.nwt.nutrition_service.validators;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.nutrition_service.dto.FoodDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.FoodServiceException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FoodValidator {
    public void validateFoodDTO(FoodDTO foodDTO, String operation) throws FoodServiceException {
        List<String> errors = new ArrayList<>();

        if (foodDTO.getName() == null) {
            errors.add("'name' is required");
        } else if (foodDTO.getName().length() < 3 || foodDTO.getName().length() > 100) {
            errors.add("'name' must be between 3 and 100 characters");
        }

        if (foodDTO.getCalories() == null) {
            errors.add("'calories' is required");
        } else if (foodDTO.getCalories() < 0) {
            errors.add("'calories' must be a non-negative number");
        }

        if (foodDTO.getMealId() == null) {
            errors.add("'mealId' is required");
        } else if (foodDTO.getMealId() <= 0) {
            errors.add("'mealId' must be a positive number");
        }

        throwError(errors, operation);
    }

    public void validateCaloriesRange(Integer minCalories, Integer maxCalories) throws FoodServiceException {
        List<String> errors = new ArrayList<>();

        if (minCalories == null) {
            errors.add("Minimum calories must be provided");
        } else if (minCalories < 0) {
            errors.add("Minimum calories must be non-negative");
        }

        if (maxCalories == null) {
            errors.add("Maximum calories must be provided");
        } else if (maxCalories < 0) {
            errors.add("Maximum calories must be non-negative");
        }

        if (minCalories != null && maxCalories != null && maxCalories < minCalories) {
            errors.add("Maximum calories must be greater than or equal to minimum calories");
        }

        throwError(errors, "search");
    }

    public void validatePatch(Map<String, String> updates) throws FoodServiceException {
        List<String> errors = new ArrayList<>();

        for (Map.Entry<String, String> entry : updates.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();

            try {
                switch (field) {
                    case "name":
                        if (value == null || value.length() < 3 || value.length() > 100) {
                            errors.add("'name' must be between 3 and 100 characters");
                        }
                        break;

                    case "calories":
                        double calories = Double.parseDouble(value);
                        if (calories < 0) {
                            errors.add("'calories' must be a non-negative number");
                        }
                        break;

                    case "mealId":
                        long mealId = Long.parseLong(value);
                        if (mealId <= 0) {
                            errors.add("'mealId' must be a positive number");
                        }
                        break;

                    default:
                        errors.add("'" + field + "' is invalid field name");
                        break;
                }
            } catch (NumberFormatException e) {
                errors.add("'" + field + "' must be a valid number");
            }
        }

        throwError(errors, "update");
    }

    private void throwError(List<String> errors, String operation) throws FoodServiceException {
        if (!errors.isEmpty()) {
            throw new FoodServiceException("Failed to " + operation + " food: " + String.join(", ", errors),
                    ErrorType.VALIDATION_FAILED);
        }
    }
}
