package ba.unsa.etf.nwt.nutrition_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MealWithFoodDTO {
    @NotNull(message = "Meal data is required")
    @Valid
    private MealDTO meal;

    @NotNull(message = "Food list is required")
    @Valid
    private List<FoodDTO> foods;
}
