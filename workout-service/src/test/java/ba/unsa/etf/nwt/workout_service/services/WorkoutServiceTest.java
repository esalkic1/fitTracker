package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkoutServiceTest {

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WorkoutService workoutService;

    private User testUser;
    private Workout testWorkout;
    private WorkoutDTO testWorkoutDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testWorkout = new Workout();
        testWorkout.setId(1L);
        testWorkout.setUser(testUser);

        testWorkoutDTO = new WorkoutDTO();
        testWorkoutDTO.setUserId(1L);
    }

    @Test
    void getAllWorkouts_ShouldReturnAllWorkouts() {
        List<Workout> workouts = Arrays.asList(testWorkout);
        when(workoutRepository.findAll()).thenReturn(workouts);

        List<Workout> result = workoutService.getAllWorkouts();

        assertEquals(workouts, result);
        verify(workoutRepository).findAll();
    }

    @Test
    void getWorkoutById_WhenWorkoutExists_ShouldReturnWorkout() throws WorkoutServiceException {
        when(workoutRepository.findById(anyLong())).thenReturn(Optional.of(testWorkout));

        Workout result = workoutService.getWorkoutById(1L);

        assertEquals(testWorkout, result);
        verify(workoutRepository).findById(1L);
    }

    @Test
    void getWorkoutById_WhenWorkoutDoesNotExist_ShouldThrowException() {
        when(workoutRepository.findById(anyLong())).thenReturn(Optional.empty());

        WorkoutServiceException exception = assertThrows(WorkoutServiceException.class, () -> {
            workoutService.getWorkoutById(1L);
        });
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(workoutRepository).findById(1L);
    }

    @Test
    void createWorkout_ShouldCreateAndReturnWorkout() throws WorkoutServiceException, UserServiceException {
        when(userService.getUserById(anyLong())).thenReturn(testUser);
        when(modelMapper.map(any(WorkoutDTO.class), eq(Workout.class))).thenReturn(testWorkout);
        when(workoutRepository.save(any(Workout.class))).thenReturn(testWorkout);
        when(modelMapper.map(any(Workout.class), eq(WorkoutDTO.class))).thenReturn(testWorkoutDTO);

        WorkoutDTO result = workoutService.createWorkout(testWorkoutDTO);

        assertNotNull(result);
        assertEquals(testWorkoutDTO, result);
        verify(userService).getUserById(testWorkoutDTO.getUserId());
        verify(modelMapper).map(testWorkoutDTO, Workout.class);
        verify(workoutRepository).save(testWorkout);
        verify(modelMapper).map(testWorkout, WorkoutDTO.class);
    }

    @Test
    void createWorkout_WhenExceptionOccurs_ShouldThrowWorkoutServiceException() throws WorkoutServiceException, UserServiceException {
        when(userService.getUserById(anyLong())).thenThrow(new RuntimeException("User not found"));

        WorkoutServiceException exception = assertThrows(WorkoutServiceException.class, () -> {
            workoutService.createWorkout(testWorkoutDTO);
        });
        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        verify(userService).getUserById(testWorkoutDTO.getUserId());
    }

    @Test
    void updateWorkout_WhenWorkoutExists_ShouldUpdateAndReturnWorkout() throws WorkoutServiceException, UserServiceException {
        when(workoutRepository.findById(anyLong())).thenReturn(Optional.of(testWorkout));
        when(userService.getUserById(anyLong())).thenReturn(testUser);
        when(workoutRepository.save(any(Workout.class))).thenReturn(testWorkout);

        doAnswer(invocation -> {
            Workout workout = invocation.getArgument(1);
            return null;
        }).when(modelMapper).map(any(WorkoutDTO.class), any(Workout.class));

        doReturn(testWorkoutDTO).when(modelMapper).map(any(Workout.class), eq(WorkoutDTO.class));

        WorkoutDTO result = workoutService.updateWorkout(1L, testWorkoutDTO);

        assertNotNull(result);
        assertEquals(testWorkoutDTO, result);
        verify(workoutRepository).findById(1L);
        verify(userService).getUserById(testWorkoutDTO.getUserId());
        verify(workoutRepository).save(testWorkout);
        verify(modelMapper).map(testWorkout, WorkoutDTO.class);
    }

    @Test
    void updateWorkout_WhenWorkoutDoesNotExist_ShouldThrowException() {
        when(workoutRepository.findById(anyLong())).thenReturn(Optional.empty());

        WorkoutServiceException exception = assertThrows(WorkoutServiceException.class, () -> {
            workoutService.updateWorkout(1L, testWorkoutDTO);
        });
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(workoutRepository).findById(1L);
        verifyNoMoreInteractions(userService, modelMapper, workoutRepository);
    }

    @Test
    void deleteWorkout_WhenWorkoutExists_ShouldDeleteWorkout() throws WorkoutServiceException {
        when(workoutRepository.findById(anyLong())).thenReturn(Optional.of(testWorkout));

        workoutService.deleteWorkout(1L);

        verify(workoutRepository).findById(1L);
        verify(workoutRepository).delete(testWorkout);
    }

    @Test
    void deleteWorkout_WhenWorkoutDoesNotExist_ShouldThrowException() {
        when(workoutRepository.findById(anyLong())).thenReturn(Optional.empty());

        WorkoutServiceException exception = assertThrows(WorkoutServiceException.class, () -> {
            workoutService.deleteWorkout(1L);
        });
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(workoutRepository).findById(1L);
        verifyNoMoreInteractions(workoutRepository);
    }

    @Mock
    private ExerciseRepository exerciseRepository;

    @Test
    void createWorkoutWithExercises_ShouldCreateWorkoutWithExercises() throws WorkoutServiceException, UserServiceException {
        User user = new User();
        user.setId(1L);

        WorkoutDTO workoutDTO = new WorkoutDTO();
        workoutDTO.setUserId(1L);

        ExerciseDTO exerciseDTO1 = new ExerciseDTO();

        ExerciseDTO exerciseDTO2 = new ExerciseDTO();

        WorkoutWithExercisesDTO requestDTO = new WorkoutWithExercisesDTO();
        requestDTO.setWorkout(workoutDTO);
        requestDTO.setExercises(Arrays.asList(exerciseDTO1, exerciseDTO2));

        Workout workout = new Workout();
        workout.setId(1L);

        Exercise exercise1 = new Exercise();
        Exercise exercise2 = new Exercise();

        when(userService.getUserById(1L)).thenReturn(user);
        when(modelMapper.map(workoutDTO, Workout.class)).thenReturn(workout);
        when(workoutRepository.save(workout)).thenReturn(workout);
        when(modelMapper.map(exerciseDTO1, Exercise.class)).thenReturn(exercise1);
        when(modelMapper.map(exerciseDTO2, Exercise.class)).thenReturn(exercise2);
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(new Exercise());
        when(modelMapper.map(workout, WorkoutDTO.class)).thenReturn(workoutDTO);

        WorkoutDTO result = workoutService.createWorkoutWithExercises(requestDTO);

        assertNotNull(result);
        verify(userService).getUserById(1L);
        verify(modelMapper).map(workoutDTO, Workout.class);
        verify(workoutRepository).save(workout);
        verify(modelMapper, times(2)).map(any(ExerciseDTO.class), eq(Exercise.class));
        verify(exerciseRepository, times(2)).save(any(Exercise.class));
        verify(modelMapper).map(workout, WorkoutDTO.class);
    }

    @Test
    void createWorkoutWithExercises_WhenExceptionOccurs_ShouldThrowWorkoutServiceException() throws UserServiceException {
        WorkoutDTO workoutDTO = new WorkoutDTO();
        workoutDTO.setUserId(1L);

        WorkoutWithExercisesDTO requestDTO = new WorkoutWithExercisesDTO();
        requestDTO.setWorkout(workoutDTO);
        requestDTO.setExercises(Arrays.asList(new ExerciseDTO()));

        when(userService.getUserById(1L)).thenThrow(new RuntimeException("User not found"));

        WorkoutServiceException exception = assertThrows(WorkoutServiceException.class, () -> {
            workoutService.createWorkoutWithExercises(requestDTO);
        });

        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());
        verify(userService).getUserById(1L);
        verifyNoInteractions(workoutRepository, exerciseRepository);
    }

    @Test
    void getWorkoutsByUserIdAndDateRange_ShouldReturnWorkouts() {
        Long userId = 1L;
        Instant from = Instant.parse("2023-01-01T00:00:00Z");
        Instant to = Instant.parse("2023-12-31T23:59:59Z");

        Workout workout1 = new Workout();
        workout1.setId(1L);

        Workout workout2 = new Workout();
        workout2.setId(2L);

        List<Workout> workouts = Arrays.asList(workout1, workout2);

        WorkoutDTO workoutDTO1 = new WorkoutDTO();
        workoutDTO1.setId(1L);

        WorkoutDTO workoutDTO2 = new WorkoutDTO();
        workoutDTO2.setId(2L);

        when(workoutRepository.findWorkoutsByUserIdAndDateBetween(userId, from, to)).thenReturn(workouts);
        when(modelMapper.map(workout1, WorkoutDTO.class)).thenReturn(workoutDTO1);
        when(modelMapper.map(workout2, WorkoutDTO.class)).thenReturn(workoutDTO2);

        List<WorkoutDTO> result = workoutService.getWorkoutsByUserIdAndDateRange(userId, from, to);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(workoutDTO1, result.get(0));
        assertEquals(workoutDTO2, result.get(1));

        verify(workoutRepository).findWorkoutsByUserIdAndDateBetween(userId, from, to);
        verify(modelMapper, times(2)).map(any(Workout.class), eq(WorkoutDTO.class));
    }

    @Test
    void getWorkoutsByUserIdAndDateRange_WhenNoWorkoutsFound_ShouldReturnEmptyList() {
        Long userId = 1L;
        Instant from = Instant.parse("2023-01-01T00:00:00Z");
        Instant to = Instant.parse("2023-12-31T23:59:59Z");

        when(workoutRepository.findWorkoutsByUserIdAndDateBetween(userId, from, to)).thenReturn(Collections.emptyList());

        List<WorkoutDTO> result = workoutService.getWorkoutsByUserIdAndDateRange(userId, from, to);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(workoutRepository).findWorkoutsByUserIdAndDateBetween(userId, from, to);
        verifyNoMoreInteractions(modelMapper);
    }
}