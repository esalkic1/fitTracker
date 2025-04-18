package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseServiceException;
import ba.unsa.etf.nwt.workout_service.services.ExerciseService;
import com.github.fge.jsonpatch.JsonPatch;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/exercise")
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(final ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllExercises() {
        return ResponseEntity.ok(exerciseService.getAllExercises());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getExercise(@PathVariable final String id) {
        try {
            return ResponseEntity.ok(exerciseService.getExerciseById(Long.parseLong(id)));
        } catch (ExerciseServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createExercise(@Valid @RequestBody final ExerciseDTO exerciseDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(exerciseService.createExercise(exerciseDTO));
        } catch (ExerciseServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @PutMapping("{exerciseId}")
    public ResponseEntity<?> updateExercise(@PathVariable final String exerciseId,
                                            @Valid @RequestBody final ExerciseDTO exerciseDTO) {
        try {
            return ResponseEntity.ok(exerciseService.updateExercise(Long.parseLong(exerciseId), exerciseDTO));
        } catch (ExerciseServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteExercise(@PathVariable final String id) {
        try {
            exerciseService.deleteExercise(Long.parseLong(id));
            return ResponseEntity.noContent().build();
        } catch (ExerciseServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }


    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<?> patchExercise(@PathVariable String id, @RequestBody JsonPatch patch) {
        try {
            ExerciseDTO updated = exerciseService.patchExercise(Long.parseLong(id), patch);
            return ResponseEntity.ok(updated);
        } catch (ExerciseServiceException e) {
            return ResponseEntity.badRequest().body(ErrorResponse.from(e.getErrorType(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Patch failed: " + e.getMessage());
        }
    }

}