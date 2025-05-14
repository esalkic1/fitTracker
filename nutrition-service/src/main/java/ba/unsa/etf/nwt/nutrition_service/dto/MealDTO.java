package ba.unsa.etf.nwt.nutrition_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class MealDTO {
    private Long id;

    @NotNull(message = "Meal name is required")
    @Size(min = 3, max = 100, message = "Meal name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Meal date cannot be in the future")
    private Instant date;
}
