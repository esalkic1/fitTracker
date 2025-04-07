package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.domain.Workout;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.WorkoutServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.WorkoutRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutService {
	private final WorkoutRepository workoutRepository;
	private final UserService userService;
	private final ModelMapper modelMapper;

	public WorkoutService(final WorkoutRepository workoutRepository, final UserService userService, final ModelMapper modelMapper) {
		this.workoutRepository = workoutRepository;
		this.userService = userService;
		this.modelMapper = modelMapper;
	}

	public List<Workout> getAllWorkouts() {
		return workoutRepository.findAll();
	}

	public Workout getWorkoutById(Long id) throws WorkoutServiceException {
		return workoutRepository.findById(id)
				.orElseThrow(() -> new WorkoutServiceException("Could not find workout with id: " + id, ErrorType.ENTITY_NOT_FOUND));
	}

	public WorkoutDTO createWorkout(WorkoutDTO workoutDTO) throws WorkoutServiceException {
		try {
			User user = userService.getUserById(workoutDTO.getUserId());

			Workout workout = modelMapper.map(workoutDTO, Workout.class);
			workout.setUser(user);

			Workout savedWorkout = workoutRepository.save(workout);
			WorkoutDTO result = modelMapper.map(savedWorkout, WorkoutDTO.class);
			result.setUserId(user.getId());

			return result;
		} catch (Exception e) {
			throw new WorkoutServiceException("Failed to create workout: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
		}
	}

	public WorkoutDTO updateWorkout(Long workoutId, WorkoutDTO workoutDTO) throws WorkoutServiceException {
		Workout existingWorkout = workoutRepository.findById(workoutId)
				.orElseThrow(() -> new WorkoutServiceException("Could not find workout with id: " + workoutId, ErrorType.ENTITY_NOT_FOUND));

		try {
			User user = userService.getUserById(workoutDTO.getUserId());

			modelMapper.map(workoutDTO, existingWorkout);
			existingWorkout.setId(workoutId);
			existingWorkout.setUser(user);

			Workout updatedWorkout = workoutRepository.save(existingWorkout);
			WorkoutDTO result = modelMapper.map(updatedWorkout, WorkoutDTO.class);
			result.setUserId(user.getId());

			return result;
		} catch (Exception e) {
			throw new WorkoutServiceException("Failed to update workout: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
		}
	}

	public void deleteWorkout(Long id) throws WorkoutServiceException {
		Workout workout = workoutRepository.findById(id)
				.orElseThrow(() -> new WorkoutServiceException("Could not find workout with id: " + id, ErrorType.ENTITY_NOT_FOUND));
		workoutRepository.delete(workout);
	}
}
