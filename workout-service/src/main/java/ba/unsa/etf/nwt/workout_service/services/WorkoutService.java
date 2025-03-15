package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.workout_service.domain.Workout;
import ba.unsa.etf.nwt.workout_service.repositories.WorkoutRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkoutService {
	private final WorkoutRepository workoutRepository;

	public WorkoutService(final WorkoutRepository workoutRepository) {
		this.workoutRepository = workoutRepository;
	}

	public Workout createWorkout(final Workout workout) {
		return workoutRepository.save(workout);
	}

	public Workout getWorkoutById(final UUID id) {
		return workoutRepository.findById(id)
				.orElseThrow(EntityNotFoundException::new);
	}
}
