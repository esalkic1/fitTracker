package ba.unsa.etf.nwt.nutrition_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.nutrition_service.clients.WorkoutClient;
import ba.unsa.etf.nwt.nutrition_service.dto.FoodDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.FoodServiceException;
import ba.unsa.etf.nwt.nutrition_service.services.FoodService;
import ba.unsa.etf.nwt.nutrition_service.validators.FoodValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/food")
public class FoodController {
    private final FoodService foodService;
    private final FoodValidator validator;
    private final WorkoutClient workoutClient;

    public FoodController(FoodService foodService, WorkoutClient workoutClient, FoodValidator validator) {
        this.foodService = foodService;
        this.validator = validator;
        this.workoutClient = workoutClient;
    }

    @GetMapping("/ping-workout")
    public String pingWorkout() {
        return workoutClient.ping();
    }

    @GetMapping("")
    public ResponseEntity<?> getAllFoods() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(foodService.getAllFoods());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getFood(@PathVariable Long id) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(foodService.getFood(id));
        } catch (FoodServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @GetMapping("search-by-name")
    public ResponseEntity<?> searchFoods(@RequestParam String name) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(foodService.searchFoodsByName(name));
        } catch (FoodServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @GetMapping("search-by-calories")
    public ResponseEntity<?> searchFoods(
            @RequestParam(name = "min") Integer minCalories,
            @RequestParam(name = "max") Integer maxCalories
    ) {
        try {
            validator.validateCaloriesRange(minCalories, maxCalories);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(foodService.searchFoodsByCalorieRange(minCalories, maxCalories));
        } catch (FoodServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createFood(@RequestBody FoodDTO foodDTO) {
        try {
            validator.validateFoodDTO(foodDTO, "create");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(foodService.createFood(foodDTO));
        } catch (FoodServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateFood(
            @PathVariable Long id,
            @RequestBody FoodDTO foodDTO
    ) {
        try {
            validator.validateFoodDTO(foodDTO, "update");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(foodService.updateFood(id, foodDTO));
        } catch (FoodServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> patchFood(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates
    ) {
        try {
            validator.validatePatch(updates);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(foodService.patchFood(id, updates));
        } catch (FoodServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteFood(@PathVariable Long id) {
        try {
            foodService.deleteFood(id);
            return ResponseEntity
                    .noContent()
                    .build();
        } catch (FoodServiceException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }
}
