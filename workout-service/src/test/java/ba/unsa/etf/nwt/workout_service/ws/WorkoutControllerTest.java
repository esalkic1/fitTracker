package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.Workout;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseDTO;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutDTO;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutWithExercisesDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.WorkoutServiceException;
import ba.unsa.etf.nwt.workout_service.services.WorkoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class WorkoutControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WorkoutService workoutService;

    @InjectMocks
    private WorkoutController workoutController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(workoutController)
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    public void testGetAllWorkouts() throws Exception {
        Workout workout1 = new Workout();
        workout1.setId(1L);
        workout1.setDate(Instant.now());

        Workout workout2 = new Workout();
        workout2.setId(2L);
        workout1.setDate(Instant.now());

        List<Workout> workouts = Arrays.asList(workout1, workout2);

        when(workoutService.getAllWorkouts()).thenReturn(workouts);

        mockMvc.perform(get("/api/v1/workout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(workoutService, times(1)).getAllWorkouts();
    }

    @Test
    public void testGetWorkoutById_Success() throws Exception {
        Workout workout = new Workout();
        workout.setId(1L);
        workout.setDate(Instant.now());

        when(workoutService.getWorkoutById(1L)).thenReturn(workout);

        mockMvc.perform(get("/api/v1/workout/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(workoutService, times(1)).getWorkoutById(1L);
    }

    @Test
    public void testGetWorkoutById_Failure() throws Exception {
        when(workoutService.getWorkoutById(anyLong()))
                .thenThrow(new WorkoutServiceException("Workout not found", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/workout/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Workout not found")))
                .andExpect(jsonPath("$.type", is(ErrorType.ENTITY_NOT_FOUND.toString())));

        verify(workoutService, times(1)).getWorkoutById(999L);
    }

    @Test
    public void testCreateWorkout_Success() throws Exception {
        WorkoutDTO requestDTO = new WorkoutDTO();
        requestDTO.setUserId(1L);
        requestDTO.setDate(Instant.now());

        WorkoutDTO responseDTO = new WorkoutDTO();
        responseDTO.setId(1L);
        responseDTO.setUserId(1L);
        responseDTO.setDate(Instant.now());

        when(workoutService.createWorkout(any(WorkoutDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/workout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));

        verify(workoutService, times(1)).createWorkout(any(WorkoutDTO.class));
    }

    @Test
    public void testCreateWorkout_Failure() throws Exception {
        WorkoutDTO requestDTO = new WorkoutDTO();
        requestDTO.setUserId(999L);
        requestDTO.setDate(Instant.now());

        when(workoutService.createWorkout(any(WorkoutDTO.class)))
                .thenThrow(new WorkoutServiceException("User not found", ErrorType.VALIDATION_FAILED));

        mockMvc.perform(post("/api/v1/workout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("User not found")))
                .andExpect(jsonPath("$.type", is(ErrorType.VALIDATION_FAILED.toString())));

        verify(workoutService, times(1)).createWorkout(any(WorkoutDTO.class));
    }

    @Test
    public void testUpdateWorkout_Success() throws Exception {
        WorkoutDTO requestDTO = new WorkoutDTO();
        requestDTO.setUserId(1L);
        requestDTO.setDate(Instant.now());

        WorkoutDTO responseDTO = new WorkoutDTO();
        responseDTO.setId(1L);
        responseDTO.setUserId(1L);
        responseDTO.setDate(Instant.now());

        when(workoutService.updateWorkout(eq(1L), any(WorkoutDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/workout/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));

        verify(workoutService, times(1)).updateWorkout(eq(1L), any(WorkoutDTO.class));
    }

    @Test
    public void testUpdateWorkout_Failure() throws Exception {
        WorkoutDTO requestDTO = new WorkoutDTO();
        requestDTO.setUserId(1L);
        requestDTO.setDate(Instant.now());

        when(workoutService.updateWorkout(eq(999L), any(WorkoutDTO.class)))
                .thenThrow(new WorkoutServiceException("Workout not found", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(put("/api/v1/workout/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Workout not found")))
                .andExpect(jsonPath("$.type", is(ErrorType.ENTITY_NOT_FOUND.toString())));

        verify(workoutService, times(1)).updateWorkout(eq(999L), any(WorkoutDTO.class));
    }

    @Test
    public void testDeleteWorkout_Success() throws Exception {

        mockMvc.perform(delete("/api/v1/workout/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(workoutService, times(1)).deleteWorkout(1L);
    }

    @Test
    public void testDeleteWorkout_Failure() throws Exception {
        doThrow(new WorkoutServiceException("Workout not found", ErrorType.ENTITY_NOT_FOUND))
                .when(workoutService).deleteWorkout(999L);

        mockMvc.perform(delete("/api/v1/workout/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Workout not found")))
                .andExpect(jsonPath("$.type", is(ErrorType.ENTITY_NOT_FOUND.toString())));

        verify(workoutService, times(1)).deleteWorkout(999L);
    }

    @Test
    public void testCreateWorkoutWithExercises_Success() throws Exception {
        WorkoutDTO workoutDTO = new WorkoutDTO();
        workoutDTO.setUserId(1L);
        workoutDTO.setDate(Instant.now());

        ExerciseDTO exerciseDTO1 = new ExerciseDTO();
        exerciseDTO1.setSets(3);
        exerciseDTO1.setReps(10);
        exerciseDTO1.setWeight(0.0);
        exerciseDTO1.setExerciseDetailsId(1L);
        exerciseDTO1.setWorkoutId(1L);

        ExerciseDTO exerciseDTO2 = new ExerciseDTO();
        exerciseDTO2.setSets(4);
        exerciseDTO2.setReps(15);
        exerciseDTO2.setWeight(20.0);
        exerciseDTO2.setExerciseDetailsId(2L);
        exerciseDTO2.setWorkoutId(1L);

        WorkoutWithExercisesDTO requestDTO = new WorkoutWithExercisesDTO();
        requestDTO.setWorkout(workoutDTO);
        requestDTO.setExercises(Arrays.asList(exerciseDTO1, exerciseDTO2));

        WorkoutDTO responseDTO = new WorkoutDTO();
        responseDTO.setId(1L);
        responseDTO.setUserId(1L);
        responseDTO.setDate(Instant.now());

        when(workoutService.createWorkoutWithExercises(any(WorkoutWithExercisesDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/workout/with-exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(1)));

        verify(workoutService, times(1)).createWorkoutWithExercises(any(WorkoutWithExercisesDTO.class));
    }

    @Test
    public void testCreateWorkoutWithExercises_Failure() throws Exception {
        WorkoutDTO workoutDTO = new WorkoutDTO();
        workoutDTO.setUserId(999L);
        workoutDTO.setDate(Instant.now());

        ExerciseDTO exerciseDTO = new ExerciseDTO();
        exerciseDTO.setSets(3);
        exerciseDTO.setReps(10);
        exerciseDTO.setWeight(0.0);
        exerciseDTO.setExerciseDetailsId(1L);
        exerciseDTO.setWorkoutId(1L);

        WorkoutWithExercisesDTO requestDTO = new WorkoutWithExercisesDTO();
        requestDTO.setWorkout(workoutDTO);
        requestDTO.setExercises(Arrays.asList(exerciseDTO));

        when(workoutService.createWorkoutWithExercises(any(WorkoutWithExercisesDTO.class)))
                .thenThrow(new WorkoutServiceException("Failed to create workout with exercises", ErrorType.VALIDATION_FAILED));

        mockMvc.perform(post("/api/v1/workout/with-exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Failed to create workout with exercises")))
                .andExpect(jsonPath("$.type", is(ErrorType.VALIDATION_FAILED.toString())));

        verify(workoutService, times(1)).createWorkoutWithExercises(any(WorkoutWithExercisesDTO.class));
    }

    @Test
    public void testGetWorkoutsByUserIdAndDateRange_Success() throws Exception {
        Long userId = 1L;
        String from = "2023-01-01T00:00:00Z";
        String to = "2023-12-31T23:59:59Z";

        WorkoutDTO workout1 = new WorkoutDTO();
        workout1.setId(1L);
        workout1.setUserId(userId);
        workout1.setDate(Instant.parse("2023-02-15T10:00:00Z"));

        WorkoutDTO workout2 = new WorkoutDTO();
        workout2.setId(2L);
        workout2.setUserId(userId);
        workout2.setDate(Instant.parse("2023-03-10T15:30:00Z"));

        List<WorkoutDTO> workouts = Arrays.asList(workout1, workout2);

        when(workoutService.getWorkoutsByUserIdAndDateRange(
                eq(userId),
                eq(Instant.parse(from)),
                eq(Instant.parse(to))
        )).thenReturn(workouts);

        mockMvc.perform(get("/api/v1/workout/by-user-and-date")
                        .param("userId", userId.toString())
                        .param("from", from)
                        .param("to", to)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(workoutService, times(1)).getWorkoutsByUserIdAndDateRange(
                eq(userId),
                eq(Instant.parse(from)),
                eq(Instant.parse(to))
        );
    }

    @Test
    public void testGetWorkoutsByUserIdAndDateRange_InvalidDateFormat() throws Exception {
        Long userId = 1L;
        String from = "invalid-date";
        String to = "2023-12-31T23:59:59Z";

        mockMvc.perform(get("/api/v1/workout/by-user-and-date")
                        .param("userId", userId.toString())
                        .param("from", from)
                        .param("to", to)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid input")));

        verify(workoutService, never()).getWorkoutsByUserIdAndDateRange(anyLong(), any(Instant.class), any(Instant.class));
    }
}