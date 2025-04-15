package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.Exercise;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseDetails;
import ba.unsa.etf.nwt.workout_service.domain.Workout;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseDetailsService exerciseDetailsService;
    private final WorkoutService workoutService;
    private final ModelMapper modelMapper;

    public ExerciseService(
            final ExerciseRepository exerciseRepository,
            final ExerciseDetailsService exerciseDetailsService,
            final WorkoutService workoutService,
            final ModelMapper modelMapper) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseDetailsService = exerciseDetailsService;
        this.workoutService = workoutService;
        this.modelMapper = modelMapper;
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public Exercise getExerciseById(Long id) throws ExerciseServiceException {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseServiceException("Could not find exercise with id: " + id, ErrorType.ENTITY_NOT_FOUND));
    }


    public ExerciseDTO createExercise(ExerciseDTO exerciseDTO) throws ExerciseServiceException {
        try {
            ExerciseDetails exerciseDetails = exerciseDetailsService.getExerciseDetailsById(exerciseDTO.getExerciseDetailsId());
            Workout workout = workoutService.getWorkoutById(exerciseDTO.getWorkoutId());

            Exercise exercise = modelMapper.map(exerciseDTO, Exercise.class);
            exercise.setExerciseDetails(exerciseDetails);
            exercise.setWorkout(workout);

            Exercise savedExercise = exerciseRepository.save(exercise);
            ExerciseDTO result = modelMapper.map(savedExercise, ExerciseDTO.class);
            result.setExerciseDetailsId(exerciseDetails.getId());
            result.setWorkoutId(workout.getId());

            return result;
        } catch (Exception e) {
            throw new ExerciseServiceException("Failed to create exercise: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public ExerciseDTO updateExercise(Long exerciseId, ExerciseDTO exerciseDTO) throws ExerciseServiceException {
        Exercise existingExercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseServiceException("Could not find exercise with id: " + exerciseId, ErrorType.ENTITY_NOT_FOUND));

        try {
            ExerciseDetails exerciseDetails = exerciseDetailsService.getExerciseDetailsById(exerciseDTO.getExerciseDetailsId());
            Workout workout = workoutService.getWorkoutById(exerciseDTO.getWorkoutId());

            if (!existingExercise.getWorkout().getId().equals(workout.getId())) {
                throw new ExerciseServiceException("Workout ID cannot be changed once assigned", ErrorType.VALIDATION_FAILED);
            }

            modelMapper.map(exerciseDTO, existingExercise);
            existingExercise.setId(exerciseId);
            existingExercise.setExerciseDetails(exerciseDetails);
            existingExercise.setWorkout(workout);

            Exercise updatedExercise = exerciseRepository.save(existingExercise);
            ExerciseDTO result = modelMapper.map(updatedExercise, ExerciseDTO.class);
            result.setExerciseDetailsId(exerciseDetails.getId());
            result.setWorkoutId(workout.getId());

            return result;
        } catch (Exception e) {
            throw new ExerciseServiceException("Failed to update exercise: " + e.getMessage(), ErrorType.VALIDATION_FAILED);
        }
    }

    public void deleteExercise(Long id) throws ExerciseServiceException {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseServiceException("Could not find exercise with id: " + id, ErrorType.ENTITY_NOT_FOUND));

        exerciseRepository.delete(exercise);
    }
}