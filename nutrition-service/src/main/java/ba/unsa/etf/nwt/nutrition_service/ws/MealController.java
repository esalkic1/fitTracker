package ba.unsa.etf.nwt.nutrition_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.nutrition_service.dto.MealDTO;
import ba.unsa.etf.nwt.nutrition_service.dto.MealWithFoodDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.FoodServiceException;
import ba.unsa.etf.nwt.nutrition_service.exceptions.MealServiceException;
import ba.unsa.etf.nwt.nutrition_service.services.MealService;
import ba.unsa.etf.nwt.nutrition_service.validators.MealValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/meal")
public class MealController {
    private final MealService mealService;
    private final MealValidator mealValidator;

    public MealController(MealService mealService, MealValidator mealValidator) {
        this.mealService = mealService;
        this.mealValidator = mealValidator;
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
    public ResponseEntity<?> createMeal(@RequestBody MealDTO mealDTO) {
        try {
            mealValidator.validateMealDTO(mealDTO, "create");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mealService.createMeal(mealDTO));
        } catch (MealServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @PostMapping("/with-food")
    public ResponseEntity<?> createMealWithFoods(@RequestBody MealWithFoodDTO mealWithFoodDTO) {
        try {
            mealValidator.validateMealWithFoodDTO(mealWithFoodDTO, "create");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(mealService.createMealWithFoods(mealWithFoodDTO));
        } catch (MealServiceException | FoodServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateFood(
            @PathVariable Long id,
            @RequestBody MealDTO mealDTO
    ) {
        try {
            mealValidator.validateMealDTO(mealDTO, "update");
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
