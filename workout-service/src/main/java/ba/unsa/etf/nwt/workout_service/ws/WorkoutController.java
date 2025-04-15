package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.workout_service.domain.Workout;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.WorkoutServiceException;
import ba.unsa.etf.nwt.workout_service.services.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/workout")
public class WorkoutController {
	private final WorkoutService workoutService;

	public WorkoutController(final WorkoutService workoutService) {
		this.workoutService = workoutService;
	}

	@GetMapping("")
	public ResponseEntity<?> getAllWorkouts(){
		return ResponseEntity.ok(workoutService.getAllWorkouts());
	}

	@GetMapping("{id}")
	public ResponseEntity<?> getWorkout(@PathVariable final String id) {
        try {
            return ResponseEntity.ok(workoutService.getWorkoutById(Long.parseLong(id)));
        } catch (WorkoutServiceException e) {
            return ResponseEntity.badRequest().body(
					ErrorResponse.from(e.getErrorType(), e.getMessage())
			);
        }
    }

	@PostMapping("")
	public ResponseEntity<?> createWorkout(@Valid @RequestBody final WorkoutDTO workoutDTO) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(workoutService.createWorkout(workoutDTO));
		} catch (WorkoutServiceException e) {
			return ResponseEntity.badRequest().body(
					ErrorResponse.from(e.getErrorType(), e.getMessage())
			);
		}
	}

	@PutMapping("{workoutId}")
	public ResponseEntity<?> updateWorkout(@PathVariable final String workoutId,
										   @Valid @RequestBody final WorkoutDTO workoutDTO) {
		try {
			return ResponseEntity.ok(workoutService.updateWorkout(Long.parseLong(workoutId), workoutDTO));
		} catch (WorkoutServiceException e) {
			return ResponseEntity.badRequest().body(
					ErrorResponse.from(e.getErrorType(), e.getMessage())
			);
		}
	}

	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteWorkout(@PathVariable final String id) {
		try {
			workoutService.deleteWorkout(Long.parseLong(id));
			return ResponseEntity.noContent().build();
		} catch (WorkoutServiceException e) {
			return ResponseEntity.badRequest().body(
					ErrorResponse.from(e.getErrorType(), e.getMessage())
			);
		}
	}

}
