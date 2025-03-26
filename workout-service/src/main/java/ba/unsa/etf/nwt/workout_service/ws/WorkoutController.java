package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.workout_service.domain.Workout;
import ba.unsa.etf.nwt.workout_service.services.WorkoutService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/workout")
public class WorkoutController {
	private final WorkoutService workoutService;

	public WorkoutController(final WorkoutService workoutService) {
		this.workoutService = workoutService;
	}

	@PostMapping
	public Workout createWorkout() {
		return workoutService.createWorkout(new Workout());
	}

	@GetMapping("{id}")
	Workout getWorkout(@PathVariable final String id) {
		return workoutService.getWorkoutById(id);
	}

	@GetMapping("health")
	public String health() {
		return "Hello from workout service.";
	}

}
