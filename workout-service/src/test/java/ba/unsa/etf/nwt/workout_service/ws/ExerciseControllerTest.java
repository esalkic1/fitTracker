package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.Exercise;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseServiceException;
import ba.unsa.etf.nwt.workout_service.services.ExerciseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ExerciseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private ExerciseController exerciseController;

    private ObjectMapper objectMapper;

    private Exercise exercise1;
    private Exercise exercise2;
    private ExerciseDTO exerciseDTO;
    private ExerciseDTO updatedExerciseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(exerciseController)
                .build();

        objectMapper = new ObjectMapper();

        exercise1 = new Exercise();
        exercise1.setId(1L);
        exercise1.setSets(3);
        exercise1.setReps(12);
        exercise1.setWeight(50.0);

        exercise2 = new Exercise();
        exercise2.setId(2L);
        exercise2.setSets(4);
        exercise2.setReps(10);
        exercise2.setWeight(60.0);

        exerciseDTO = new ExerciseDTO();
        exerciseDTO.setSets(3);
        exerciseDTO.setReps(12);
        exerciseDTO.setWeight(50.0);
        exerciseDTO.setExerciseDetailsId(1L);
        exerciseDTO.setWorkoutId(1L);

        updatedExerciseDTO = new ExerciseDTO();
        updatedExerciseDTO.setSets(4);
        updatedExerciseDTO.setReps(10);
        updatedExerciseDTO.setWeight(55.0);
        updatedExerciseDTO.setExerciseDetailsId(1L);
        updatedExerciseDTO.setWorkoutId(1L);
    }

    @Test
    void getAllExercises_ReturnsListOfExercises() throws Exception {
        List<Exercise> exercises = Arrays.asList(exercise1, exercise2);
        when(exerciseService.getAllExercises()).thenReturn(exercises);

        mockMvc.perform(get("/api/v1/exercise")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].sets", is(3)))
                .andExpect(jsonPath("$[0].reps", is(12)))
                .andExpect(jsonPath("$[0].weight", is(50.0)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].sets", is(4)))
                .andExpect(jsonPath("$[1].reps", is(10)))
                .andExpect(jsonPath("$[1].weight", is(60.0)));

        verify(exerciseService, times(1)).getAllExercises();
    }

    @Test
    void getExerciseById_WhenExerciseExists_ReturnsExercise() throws Exception {
        when(exerciseService.getExerciseById(1L)).thenReturn(exercise1);

        mockMvc.perform(get("/api/v1/exercise/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.sets", is(3)))
                .andExpect(jsonPath("$.reps", is(12)))
                .andExpect(jsonPath("$.weight", is(50.0)));

        verify(exerciseService, times(1)).getExerciseById(1L);
    }

    @Test
    void getExerciseById_WhenExerciseDoesNotExist_ReturnsBadRequest() throws Exception {
        when(exerciseService.getExerciseById(99L)).thenThrow(
                new ExerciseServiceException("Could not find exercise with id: 99", ErrorType.ENTITY_NOT_FOUND)
        );

        mockMvc.perform(get("/api/v1/exercise/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Could not find exercise with id: 99")));

        verify(exerciseService, times(1)).getExerciseById(99L);
    }

    @Test
    void createExercise_WithValidData_ReturnsCreatedExercise() throws Exception {
        ExerciseDTO responseDTO = new ExerciseDTO();
        responseDTO.setId(1L);
        responseDTO.setSets(3);
        responseDTO.setReps(12);
        responseDTO.setWeight(50.0);
        responseDTO.setExerciseDetailsId(1L);
        responseDTO.setWorkoutId(1L);

        when(exerciseService.createExercise(any(ExerciseDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.sets", is(3)))
                .andExpect(jsonPath("$.reps", is(12)))
                .andExpect(jsonPath("$.weight", is(50.0)))
                .andExpect(jsonPath("$.exerciseDetailsId", is(1)))
                .andExpect(jsonPath("$.workoutId", is(1)));

        verify(exerciseService, times(1)).createExercise(any(ExerciseDTO.class));
    }

    @Test
    void createExercise_WithInvalidData_ReturnsBadRequest() throws Exception {
        when(exerciseService.createExercise(any(ExerciseDTO.class))).thenThrow(
                new ExerciseServiceException("Failed to create exercise: Exercise details not found", ErrorType.VALIDATION_FAILED)
        );

        mockMvc.perform(post("/api/v1/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.message", is("Failed to create exercise: Exercise details not found")));

        verify(exerciseService, times(1)).createExercise(any(ExerciseDTO.class));
    }

    @Test
    void updateExercise_WithValidData_ReturnsUpdatedExercise() throws Exception {
        ExerciseDTO responseDTO = new ExerciseDTO();
        responseDTO.setId(1L);
        responseDTO.setSets(4);
        responseDTO.setReps(10);
        responseDTO.setWeight(55.0);
        responseDTO.setExerciseDetailsId(1L);
        responseDTO.setWorkoutId(1L);

        when(exerciseService.updateExercise(eq(1L), any(ExerciseDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/exercise/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedExerciseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.sets", is(4)))
                .andExpect(jsonPath("$.reps", is(10)))
                .andExpect(jsonPath("$.weight", is(55.0)))
                .andExpect(jsonPath("$.exerciseDetailsId", is(1)))
                .andExpect(jsonPath("$.workoutId", is(1)));

        verify(exerciseService, times(1)).updateExercise(eq(1L), any(ExerciseDTO.class));
    }

    @Test
    void updateExercise_WhenExerciseDoesNotExist_ReturnsBadRequest() throws Exception {
        when(exerciseService.updateExercise(eq(99L), any(ExerciseDTO.class))).thenThrow(
                new ExerciseServiceException("Could not find exercise with id: 99", ErrorType.ENTITY_NOT_FOUND)
        );

        mockMvc.perform(put("/api/v1/exercise/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedExerciseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Could not find exercise with id: 99")));

        verify(exerciseService, times(1)).updateExercise(eq(99L), any(ExerciseDTO.class));
    }

    @Test
    void updateExercise_WithInvalidWorkoutId_ReturnsBadRequest() throws Exception {
        when(exerciseService.updateExercise(eq(1L), any(ExerciseDTO.class))).thenThrow(
                new ExerciseServiceException("Workout ID cannot be changed once assigned", ErrorType.VALIDATION_FAILED)
        );

        mockMvc.perform(put("/api/v1/exercise/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedExerciseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.message", is("Workout ID cannot be changed once assigned")));

        verify(exerciseService, times(1)).updateExercise(eq(1L), any(ExerciseDTO.class));
    }

    @Test
    void deleteExercise_WhenExerciseExists_ReturnsNoContent() throws Exception {
        doNothing().when(exerciseService).deleteExercise(1L);

        mockMvc.perform(delete("/api/v1/exercise/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(exerciseService, times(1)).deleteExercise(1L);
    }

    @Test
    void deleteExercise_WhenExerciseDoesNotExist_ReturnsBadRequest() throws Exception {
        doThrow(new ExerciseServiceException("Could not find exercise with id: 99", ErrorType.ENTITY_NOT_FOUND))
                .when(exerciseService).deleteExercise(99L);

        mockMvc.perform(delete("/api/v1/exercise/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Could not find exercise with id: 99")));

        verify(exerciseService, times(1)).deleteExercise(99L);
    }
}