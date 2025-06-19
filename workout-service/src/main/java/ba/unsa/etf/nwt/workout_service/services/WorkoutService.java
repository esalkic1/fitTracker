package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.clients.NutritionClient;
import ba.unsa.etf.nwt.workout_service.domain.Exercise;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.domain.Workout;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseDTO;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutDTO;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutWithExercisesDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.UserServiceException;
import ba.unsa.etf.nwt.workout_service.exceptions.WorkoutServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseRepository;
import ba.unsa.etf.nwt.workout_service.repositories.WorkoutRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class WorkoutService {
	private final WorkoutRepository workoutRepository;
	private final UserService userService;
	private final ModelMapper modelMapper;

	private final ExerciseRepository exerciseRepository;
	private final NutritionClient nutritionClient;

	public WorkoutService(final WorkoutRepository workoutRepository, final UserService userService, final ModelMapper modelMapper, final ExerciseRepository exerciseRepository, final NutritionClient nutritionClient) {
		this.workoutRepository = workoutRepository;
		this.userService = userService;
		this.modelMapper = modelMapper;
		this.exerciseRepository = exerciseRepository;
		this.nutritionClient = nutritionClient;
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
			User user = userService.getUserByUuid(workoutDTO.getUserHandle());

			Workout workout = modelMapper.map(workoutDTO, Workout.class);
			workout.setUser(user);

			Workout savedWorkout = workoutRepository.save(workout);
			WorkoutDTO result = modelMapper.map(savedWorkout, WorkoutDTO.class);
			result.setUserHandle(user.getUuid());

			return result;
		} catch (Exception e) {
			throw new WorkoutServiceException("Failed to create workout: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
		}
	}

	public WorkoutDTO updateWorkout(Long workoutId, WorkoutDTO workoutDTO) throws WorkoutServiceException {
		Workout existingWorkout = workoutRepository.findById(workoutId)
				.orElseThrow(() -> new WorkoutServiceException("Could not find workout with id: " + workoutId, ErrorType.ENTITY_NOT_FOUND));

		try {
			User user = userService.getUserByUuid(workoutDTO.getUserHandle());

			modelMapper.map(workoutDTO, existingWorkout);
			existingWorkout.setId(workoutId);
			existingWorkout.setUser(user);

			Workout updatedWorkout = workoutRepository.save(existingWorkout);
			WorkoutDTO result = modelMapper.map(updatedWorkout, WorkoutDTO.class);
			result.setUserHandle(user.getUuid());

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
			User user = userService.getUserByUuid(request.getWorkout().getUserHandle());
			Workout workout = modelMapper.map(request.getWorkout(), Workout.class);

			// Nutrition communication
			if (isWorkoutIntense(request.getExercises())) {
				boolean hasMeal = nutritionClient.hasRecentMeal(
						request.getWorkout().getUserHandle(),
						workout.getDate()
				);
				if (!hasMeal) {
					throw new WorkoutServiceException("User must log a meal before intense workouts", ErrorType.VALIDATION_FAILED);
				}
			}

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

	public Boolean hadIntenseWorkout(@RequestParam Long userId, @RequestParam Instant date) {
		Instant startOfDay = date.atZone(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
		Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);

		List<Workout> workouts = workoutRepository.findWorkoutsByUserIdAndDateBetween(userId, startOfDay, endOfDay);

		return workouts.stream()
				.anyMatch(workout -> {
					List<ExerciseDTO> exerciseDTOs = workout.getExercises().stream()
							.map(ex -> modelMapper.map(ex, ExerciseDTO.class))
							.toList();
					return isWorkoutIntense(exerciseDTOs);
				});
	}

	private boolean isWorkoutIntense(List<ExerciseDTO> exercises) {
		if (exercises.size() > 3) return true;

		for (ExerciseDTO e : exercises) {
			Double weight = e.getWeight();
			Integer reps = e.getReps();
			Integer sets = e.getSets();

			if (weight != null && weight > 100) return true;
			if (reps != null && reps > 10 &&
					sets != null && sets > 3) return true;
		}

		return false;
	}

	public String getWorkoutIntensityLevel(Long userId, Instant date) {
		Instant startOfDay = date.truncatedTo(ChronoUnit.DAYS);
		Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);

		List<Workout> workouts = workoutRepository.findWorkoutsByUserIdAndDateBetween(userId, startOfDay, endOfDay);

		if (workouts.isEmpty()) return "LIGHT";

		int score = 0;
		for (Workout w : workouts) {
			for (Exercise ex : w.getExercises()) {
				if (ex.getWeight() > 100) score += 3;
				if (ex.getReps() > 10) score += 1;
				if (ex.getSets() > 3) score += 1;
			}
		}

		return switch (score) {
			case 0, 1, 2 -> "LIGHT";
			case 3, 4, 5, 6, 7 -> "MODERATE";
			default -> "INTENSE";
		};
	}

	public List<Workout> getWorkoutsByUserUuid(String uuid) throws WorkoutServiceException, UserServiceException {
		User user = userService.getUserByUuid(UUID.fromString(uuid));

		List<Workout> workouts = workoutRepository.findWorkoutsByUserId(user.getId());

		return workouts;
	}

}
