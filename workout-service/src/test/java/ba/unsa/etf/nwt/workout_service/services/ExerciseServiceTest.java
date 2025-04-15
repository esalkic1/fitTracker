package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.Exercise;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseDetails;
import ba.unsa.etf.nwt.workout_service.domain.Workout;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ExerciseDetailsService exerciseDetailsService;

    @Mock
    private WorkoutService workoutService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ExerciseService exerciseService;

    private Exercise exercise;
    private ExerciseDetails exerciseDetails;
    private Workout workout;
    private ExerciseDTO exerciseDTO;

    @BeforeEach
    void setUp() {
        exerciseDetails = new ExerciseDetails();
        exerciseDetails.setId(1L);
        exerciseDetails.setName("Push-up");

        workout = new Workout();
        workout.setId(1L);

        exercise = new Exercise();
        exercise.setId(1L);
        exercise.setReps(10);
        exercise.setSets(3);
        exercise.setExerciseDetails(exerciseDetails);
        exercise.setWorkout(workout);

        exerciseDTO = new ExerciseDTO();
        exerciseDTO.setReps(10);
        exerciseDTO.setSets(3);
        exerciseDTO.setExerciseDetailsId(1L);
        exerciseDTO.setWorkoutId(1L);
    }

    @Test
    void getAllExercises_ShouldReturnAllExercises() {
        List<Exercise> exerciseList = Arrays.asList(exercise);
        when(exerciseRepository.findAll()).thenReturn(exerciseList);

        List<Exercise> result = exerciseService.getAllExercises();

        assertEquals(1, result.size());
        assertEquals(exercise, result.get(0));
        verify(exerciseRepository, times(1)).findAll();
    }

    @Test
    void getExerciseById_ExistingId_ShouldReturnExercise() throws ExerciseServiceException {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));

        Exercise result = exerciseService.getExerciseById(1L);

        assertNotNull(result);
        assertEquals(exercise, result);
        verify(exerciseRepository, times(1)).findById(1L);
    }

    @Test
    void getExerciseById_NonExistingId_ShouldThrowException() {
        when(exerciseRepository.findById(999L)).thenReturn(Optional.empty());

        ExerciseServiceException exception = assertThrows(ExerciseServiceException.class, () -> {
            exerciseService.getExerciseById(999L);
        });

        assertEquals("Could not find exercise with id: 999", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(exerciseRepository, times(1)).findById(999L);
    }

    @Test
    void createExercise_ValidData_ShouldCreateExercise() throws Exception {
        when(exerciseDetailsService.getExerciseDetailsById(1L)).thenReturn(exerciseDetails);
        when(workoutService.getWorkoutById(1L)).thenReturn(workout);

        Exercise mappedExercise = new Exercise();
        doReturn(mappedExercise).when(modelMapper).map(any(ExerciseDTO.class), eq(Exercise.class));

        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

        ExerciseDTO mappedDTO = new ExerciseDTO();
        mappedDTO.setExerciseDetailsId(1L);
        mappedDTO.setWorkoutId(1L);
        doReturn(mappedDTO).when(modelMapper).map(any(Exercise.class), eq(ExerciseDTO.class));

        ExerciseDTO result = exerciseService.createExercise(exerciseDTO);

        assertNotNull(result);
        assertEquals(1L, result.getExerciseDetailsId());
        assertEquals(1L, result.getWorkoutId());
        verify(exerciseDetailsService, times(1)).getExerciseDetailsById(1L);
        verify(workoutService, times(1)).getWorkoutById(1L);
        verify(exerciseRepository, times(1)).save(any(Exercise.class));
    }

    @Test
    void createExercise_InvalidData_ShouldThrowException() throws Exception {
        when(exerciseDetailsService.getExerciseDetailsById(1L)).thenThrow(new RuntimeException("Exercise details not found"));

        ExerciseServiceException exception = assertThrows(ExerciseServiceException.class, () -> {
            exerciseService.createExercise(exerciseDTO);
        });

        assertEquals("Failed to create exercise: Exercise details not found", exception.getMessage());
        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
    }

    @Test
    void updateExercise_ValidData_ShouldUpdateExercise() throws Exception {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(exerciseDetailsService.getExerciseDetailsById(1L)).thenReturn(exerciseDetails);
        when(workoutService.getWorkoutById(1L)).thenReturn(workout);

        doAnswer(invocation -> {
            Exercise existingExercise = invocation.getArgument(1);
            existingExercise.setReps(exerciseDTO.getReps());
            existingExercise.setSets(exerciseDTO.getSets());
            return null;
        }).when(modelMapper).map(any(ExerciseDTO.class), any(Exercise.class));

        when(exerciseRepository.save(any(Exercise.class))).thenReturn(exercise);

        ExerciseDTO resultDTO = new ExerciseDTO();
        resultDTO.setExerciseDetailsId(1L);
        resultDTO.setWorkoutId(1L);
        doReturn(resultDTO).when(modelMapper).map(any(Exercise.class), eq(ExerciseDTO.class));

        ExerciseDTO result = exerciseService.updateExercise(1L, exerciseDTO);

        assertNotNull(result);
        assertEquals(1L, result.getExerciseDetailsId());
        assertEquals(1L, result.getWorkoutId());
        verify(exerciseRepository, times(1)).findById(1L);
        verify(exerciseDetailsService, times(1)).getExerciseDetailsById(1L);
        verify(workoutService, times(1)).getWorkoutById(1L);
        verify(exerciseRepository, times(1)).save(any(Exercise.class));
    }

    @Test
    void updateExercise_NonExistingId_ShouldThrowException() {
        when(exerciseRepository.findById(999L)).thenReturn(Optional.empty());

        ExerciseServiceException exception = assertThrows(ExerciseServiceException.class, () -> {
            exerciseService.updateExercise(999L, exerciseDTO);
        });

        assertEquals("Could not find exercise with id: 999", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(exerciseRepository, times(1)).findById(999L);
    }

    @Test
    void updateExercise_DifferentWorkoutId_ShouldThrowException() throws Exception {
        ExerciseDTO differentWorkoutDTO = new ExerciseDTO();
        differentWorkoutDTO.setExerciseDetailsId(1L);
        differentWorkoutDTO.setWorkoutId(2L);

        Workout differentWorkout = new Workout();
        differentWorkout.setId(2L);

        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        when(exerciseDetailsService.getExerciseDetailsById(1L)).thenReturn(exerciseDetails);
        when(workoutService.getWorkoutById(2L)).thenReturn(differentWorkout);

        ExerciseServiceException exception = assertThrows(ExerciseServiceException.class, () -> {
            exerciseService.updateExercise(1L, differentWorkoutDTO);
        });

        assertEquals("Failed to update exercise: Workout ID cannot be changed once assigned", exception.getMessage());
        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
    }

    @Test
    void deleteExercise_ExistingId_ShouldDeleteExercise() throws ExerciseServiceException {
        when(exerciseRepository.findById(1L)).thenReturn(Optional.of(exercise));
        doNothing().when(exerciseRepository).delete(exercise);

        exerciseService.deleteExercise(1L);

        verify(exerciseRepository, times(1)).findById(1L);
        verify(exerciseRepository, times(1)).delete(exercise);
    }

    @Test
    void deleteExercise_NonExistingId_ShouldThrowException() {
        when(exerciseRepository.findById(999L)).thenReturn(Optional.empty());

        ExerciseServiceException exception = assertThrows(ExerciseServiceException.class, () -> {
            exerciseService.deleteExercise(999L);
        });

        assertEquals("Could not find exercise with id: 999", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(exerciseRepository, times(1)).findById(999L);
        verify(exerciseRepository, never()).delete(any());
    }
}