package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.Exercise;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.domain.Workout;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseDTO;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutDTO;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutWithExercisesDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.WorkoutServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseRepository;
import ba.unsa.etf.nwt.workout_service.repositories.WorkoutRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class WorkoutService {
	private final WorkoutRepository workoutRepository;
	private final UserService userService;
	private final ModelMapper modelMapper;

	private final ExerciseRepository exerciseRepository;

	public WorkoutService(final WorkoutRepository workoutRepository, final UserService userService, final ModelMapper modelMapper, final ExerciseRepository exerciseRepository) {
		this.workoutRepository = workoutRepository;
		this.userService = userService;
		this.modelMapper = modelMapper;
		this.exerciseRepository = exerciseRepository;
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

	@Transactional
	public WorkoutDTO createWorkoutWithExercises(WorkoutWithExercisesDTO request) throws WorkoutServiceException {
		try {
			User user = userService.getUserById(request.getWorkout().getUserId());

			Workout workout = modelMapper.map(request.getWorkout(), Workout.class);
			workout.setUser(user);
			Workout savedWorkout = workoutRepository.save(workout);
			for (ExerciseDTO dto : request.getExercises()) {
				Exercise exercise = modelMapper.map(dto, Exercise.class);
				exercise.setWorkout(savedWorkout);
				exerciseRepository.save(exercise);
			}

			return modelMapper.map(savedWorkout, WorkoutDTO.class);

		} catch (Exception e) {
			throw new WorkoutServiceException("Failed to create workout with exercises: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
		}
	}

	public List<WorkoutDTO> getWorkoutsByUserIdAndDateRange(Long userId, Instant from, Instant to) {
		return workoutRepository.findWorkoutsByUserIdAndDateBetween(userId, from, to)
				.stream()
				.map(w -> modelMapper.map(w, WorkoutDTO.class))
				.toList();
	}

}
