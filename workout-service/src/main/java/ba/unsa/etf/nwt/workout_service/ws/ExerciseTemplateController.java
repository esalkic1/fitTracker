package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseTemplateDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseTemplateServiceException;
import ba.unsa.etf.nwt.workout_service.services.ExerciseTemplateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/exercise-template")
public class ExerciseTemplateController {
    private final ExerciseTemplateService exerciseTemplateService;

    public ExerciseTemplateController(final ExerciseTemplateService exerciseTemplateService) {
        this.exerciseTemplateService = exerciseTemplateService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllExerciseTemplates() {
        return ResponseEntity.ok(exerciseTemplateService.getAllExerciseTemplates());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getExerciseTemplate(@PathVariable final String id) {
        try {
            return ResponseEntity.ok(exerciseTemplateService.getExerciseTemplateById(Long.parseLong(id)));
        } catch (ExerciseTemplateServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createExerciseTemplate(@Valid @RequestBody final ExerciseTemplateDTO exerciseTemplateDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(exerciseTemplateService.createExerciseTemplate(exerciseTemplateDTO));
        } catch (ExerciseTemplateServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @PutMapping("{exerciseTemplateId}")
    public ResponseEntity<?> updateExerciseTemplate(@PathVariable final String exerciseTemplateId,
                                                    @Valid @RequestBody final ExerciseTemplateDTO exerciseTemplateDTO) {
        try {
            return ResponseEntity.ok(exerciseTemplateService.updateExerciseTemplate(
                    Long.parseLong(exerciseTemplateId), exerciseTemplateDTO));
        } catch (ExerciseTemplateServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteExerciseTemplate(@PathVariable final String id) {
        try {
            exerciseTemplateService.deleteExerciseTemplate(Long.parseLong(id));
            return ResponseEntity.noContent().build();
        } catch (ExerciseTemplateServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }
}