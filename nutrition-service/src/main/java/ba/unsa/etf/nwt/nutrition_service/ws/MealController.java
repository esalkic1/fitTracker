package ba.unsa.etf.nwt.nutrition_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.nutrition_service.dto.MealDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.MealServiceException;
import ba.unsa.etf.nwt.nutrition_service.services.MealService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/meal")
public class MealController {
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllMeals() {
        return ResponseEntity.ok(mealService.getAllMeals());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getMeal(@PathVariable Long id) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(mealService.getMeal(id));
        } catch (MealServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @GetMapping("search")
    public ResponseEntity<?> searchMeals(@RequestParam String name) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(mealService.searchMealsByName(name));
        } catch (MealServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createMeal(@Valid @RequestBody MealDTO mealDTO) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mealService.createMeal(mealDTO));
        } catch (MealServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateFood(
            @PathVariable Long id,
            @Valid @RequestBody MealDTO mealDTO
    ) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(mealService.updateMeal(id, mealDTO));
        } catch (MealServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteMeal(@PathVariable Long id) {
        try {
            mealService.deleteMeal(id);
            return ResponseEntity
                    .noContent()
                    .build();
        } catch (MealServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }
}
