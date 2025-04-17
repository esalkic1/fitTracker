package ba.unsa.etf.nwt.nutrition_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FoodDTO {
    private Long id;

    @NotNull(message = "Food name is required")
    @Size(min = 3, max = 100, message = "Food name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Calories is required")
    @PositiveOrZero(message = "Calories must be a non-negative number")
    private Double calories;

    @NotNull(message = "Meal ID is required")
    @Positive(message = "Meal ID must be a positive number")
    private Long mealId;
}
