package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseDetails;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseDetailsServiceException;
import ba.unsa.etf.nwt.workout_service.services.ExerciseDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/exercise-details")
public class ExerciseDetailsController {
    private final ExerciseDetailsService exerciseDetailsService;

    public ExerciseDetailsController(final ExerciseDetailsService exerciseDetailsService) {
        this.exerciseDetailsService = exerciseDetailsService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllExerciseDetails() {
        return ResponseEntity.ok(exerciseDetailsService.getAllExerciseDetails());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getExerciseDetails(@PathVariable final String id) {
        try {
            return ResponseEntity.ok(exerciseDetailsService.getExerciseDetailsById(Long.parseLong(id)));
        } catch (ExerciseDetailsServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @GetMapping("/muscle-group")
    public ResponseEntity<?> getExerciseDetailsByMuscleGroup(@RequestParam("muscleGroup") final String muscleGroup) {
        return ResponseEntity.ok(exerciseDetailsService.getExerciseDetailsByMuscleGroup(muscleGroup));
    }

    @GetMapping("/difficulty-level")
    public ResponseEntity<?> getExerciseDetailsByDifficultyLevel(@RequestParam("difficultyLevel") final String level) {
        return ResponseEntity.ok(exerciseDetailsService.getExerciseDetailsByDifficultyLevel(level));
    }


    @GetMapping("search")
    public ResponseEntity<?> searchExerciseDetails(@RequestParam("name") final String name) {
        return ResponseEntity.ok(exerciseDetailsService.searchExerciseDetailsByName(name));
    }

    @PostMapping("")
    public ResponseEntity<?> createExerciseDetails(@RequestBody final ExerciseDetails exerciseDetails) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(exerciseDetailsService.createExerciseDetails(exerciseDetails));
        } catch (ExerciseDetailsServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateExerciseDetails(@PathVariable final String id,
                                                   @RequestBody final ExerciseDetails exerciseDetails) {
        try {
            return ResponseEntity.ok(exerciseDetailsService.updateExerciseDetails(Long.parseLong(id), exerciseDetails));
        } catch (ExerciseDetailsServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteExerciseDetails(@PathVariable final String id) {
        try {
            exerciseDetailsService.deleteExerciseDetails(Long.parseLong(id));
            return ResponseEntity.noContent().build();
        } catch (ExerciseDetailsServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }
}