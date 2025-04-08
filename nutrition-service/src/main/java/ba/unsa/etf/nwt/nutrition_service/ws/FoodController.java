package ba.unsa.etf.nwt.nutrition_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.nutrition_service.dto.FoodDTO;
import ba.unsa.etf.nwt.nutrition_service.exceptions.FoodServiceException;
import ba.unsa.etf.nwt.nutrition_service.services.FoodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/food")
public class FoodController {
    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
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
    public ResponseEntity<?> createFood(@Valid @RequestBody FoodDTO foodDTO) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(foodService.createFood(foodDTO));
        } catch (FoodServiceException e) {
            System.out.println(e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateFood(
            @PathVariable Long id,
            @Valid @RequestBody FoodDTO foodDTO
    ) {
        try {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(foodService.updateFood(id, foodDTO));
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
