package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.domain.Workout;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutDTO;
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
import java.time.LocalDateTime;
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
}