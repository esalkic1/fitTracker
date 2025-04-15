package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseMetadata;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseMetadataDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseMetadataServiceException;
import ba.unsa.etf.nwt.workout_service.services.ExerciseMetadataService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/exercise-metadata")
public class ExerciseMetadataController {
    private final ExerciseMetadataService exerciseMetadataService;

    public ExerciseMetadataController(final ExerciseMetadataService exerciseMetadataService) {
        this.exerciseMetadataService = exerciseMetadataService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllExerciseMetadata() {
        return ResponseEntity.ok(exerciseMetadataService.getAllExerciseMetadata());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getExerciseMetadataById(@PathVariable final String id) {
        try {
            return ResponseEntity.ok(exerciseMetadataService.getExerciseMetadataById(Long.parseLong(id)));
        } catch (ExerciseMetadataServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<?> getExerciseMetadataByExercise(@PathVariable final String exerciseId) {
        try {
            return ResponseEntity.ok(exerciseMetadataService.getExerciseMetadataByExerciseId(Long.parseLong(exerciseId)));
        } catch (ExerciseMetadataServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createExerciseMetadata(@Valid @RequestBody final ExerciseMetadataDTO exerciseMetadataDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(exerciseMetadataService.createExerciseMetadata(exerciseMetadataDTO));
        } catch (ExerciseMetadataServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateExerciseMetadata(@PathVariable final String id,
                                                    @Valid @RequestBody final ExerciseMetadataDTO exerciseMetadataDTO) {
        try {
            return ResponseEntity.ok(exerciseMetadataService.updateExerciseMetadata(Long.parseLong(id), exerciseMetadataDTO));
        } catch (ExerciseMetadataServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteExerciseMetadata(@PathVariable final String id) {
        try {
            exerciseMetadataService.deleteExerciseMetadata(Long.parseLong(id));
            return ResponseEntity.noContent().build();
        } catch (ExerciseMetadataServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }
}