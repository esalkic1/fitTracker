package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.workout_service.domain.WorkoutTemplate;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutTemplateDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.WorkoutTemplateServiceException;
import ba.unsa.etf.nwt.workout_service.services.WorkoutTemplateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/workout-template")
public class WorkoutTemplateController {
    private final WorkoutTemplateService workoutTemplateService;

    public WorkoutTemplateController(final WorkoutTemplateService workoutTemplateService) {
        this.workoutTemplateService = workoutTemplateService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllWorkoutTemplates() {
        return ResponseEntity.ok(workoutTemplateService.getAllWorkoutTemplates());
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<?> getWorkoutTemplatesByUserId(@PathVariable final String userId) {
        try {
            return ResponseEntity.ok(workoutTemplateService.getWorkoutTemplatesByUserId(Long.parseLong(userId)));
        } catch (WorkoutTemplateServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getWorkoutTemplateById(@PathVariable final String id) {
        try {
            return ResponseEntity.ok(workoutTemplateService.getWorkoutTemplateById(Long.parseLong(id)));
        } catch (WorkoutTemplateServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createWorkoutTemplate(@Valid @RequestBody final WorkoutTemplateDTO workoutTemplateDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(workoutTemplateService.createWorkoutTemplate(workoutTemplateDTO));
        } catch (WorkoutTemplateServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @PutMapping("{workoutTemplateId}")
    public ResponseEntity<?> updateWorkoutTemplate(@PathVariable final String workoutTemplateId,
                                                   @Valid @RequestBody final WorkoutTemplateDTO workoutTemplateDTO) {
        try {
            return ResponseEntity.ok(workoutTemplateService.updateWorkoutTemplate(Long.parseLong(workoutTemplateId), workoutTemplateDTO));
        } catch (WorkoutTemplateServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteWorkoutTemplate(@PathVariable final String id) {
        try {
            workoutTemplateService.deleteWorkoutTemplate(Long.parseLong(id));
            return ResponseEntity.noContent().build();
        } catch (WorkoutTemplateServiceException e) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.from(e.getErrorType(), e.getMessage())
            );
        }
    }
}