package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseDetails;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseDetailsServiceException;
import ba.unsa.etf.nwt.workout_service.services.ExerciseDetailsService;
import ba.unsa.etf.nwt.workout_service.services.ExerciseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ExerciseDetailsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExerciseDetailsService exerciseDetailsService;

    @InjectMocks
    private ExerciseDetailsController exerciseDetailsController;

    private ObjectMapper objectMapper;

    private ExerciseDetails testExerciseDetails;
    private List<ExerciseDetails> exerciseDetailsList;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(exerciseDetailsController)
                .build();

        objectMapper = new ObjectMapper();

        testExerciseDetails = new ExerciseDetails();
        testExerciseDetails.setId(1L);
        testExerciseDetails.setName("Bench Press");
        testExerciseDetails.setDescription("Chest exercise");
        testExerciseDetails.setMuscleGroup("Chest");
        testExerciseDetails.setEquipment("Barbell");
        testExerciseDetails.setDifficultyLevel("Intermediate");

        ExerciseDetails details2 = new ExerciseDetails();
        details2.setId(2L);
        details2.setName("Squat");
        details2.setDescription("Leg exercise");
        details2.setMuscleGroup("Legs");
        details2.setEquipment("Barbell");
        details2.setDifficultyLevel("Advanced");

        exerciseDetailsList = Arrays.asList(testExerciseDetails, details2);
    }

    @Test
    public void testGetAllExerciseDetails() throws Exception {
        when(exerciseDetailsService.getAllExerciseDetails()).thenReturn(exerciseDetailsList);

        mockMvc.perform(get("/api/v1/exercise-details")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Bench Press")))
                .andExpect(jsonPath("$[1].name", is("Squat")));
    }

    @Test
    public void testGetExerciseDetailsById_Success() throws Exception {
        when(exerciseDetailsService.getExerciseDetailsById(1L)).thenReturn(testExerciseDetails);

        mockMvc.perform(get("/api/v1/exercise-details/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Bench Press")));
    }

    @Test
    public void testGetExerciseDetailsById_NotFound() throws Exception {
        when(exerciseDetailsService.getExerciseDetailsById(anyLong())).thenThrow(
                new ExerciseDetailsServiceException("Could not find exercise details with id: 999", ErrorType.ENTITY_NOT_FOUND)
        );

        mockMvc.perform(get("/api/v1/exercise-details/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")));
    }

    @Test
    public void testGetExerciseDetailsByMuscleGroup() throws Exception {
        List<ExerciseDetails> chestExercises = Arrays.asList(testExerciseDetails);
        when(exerciseDetailsService.getExerciseDetailsByMuscleGroup("Chest")).thenReturn(chestExercises);

        mockMvc.perform(get("/api/v1/exercise-details/muscle-group")
                        .param("muscleGroup", "Chest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].muscleGroup", is("Chest")));
    }

    @Test
    public void testGetExerciseDetailsByDifficultyLevel() throws Exception {
        ExerciseDetails advancedExercise = exerciseDetailsList.get(1);
        List<ExerciseDetails> advancedExercises = Arrays.asList(advancedExercise);
        when(exerciseDetailsService.getExerciseDetailsByDifficultyLevel("Advanced")).thenReturn(advancedExercises);

        mockMvc.perform(get("/api/v1/exercise-details/difficulty-level")
                        .param("difficultyLevel", "Advanced")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].difficultyLevel", is("Advanced")));
    }

    @Test
    public void testSearchExerciseDetailsByName() throws Exception {
        List<ExerciseDetails> benchPressExercises = Arrays.asList(testExerciseDetails);
        when(exerciseDetailsService.searchExerciseDetailsByName("Bench Press")).thenReturn(benchPressExercises);

        mockMvc.perform(get("/api/v1/exercise-details/search")
                        .param("name", "Bench Press")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Bench Press")));
    }

    @Test
    public void testCreateExerciseDetails_Success() throws Exception {
        when(exerciseDetailsService.createExerciseDetails(any(ExerciseDetails.class))).thenReturn(testExerciseDetails);

        mockMvc.perform(post("/api/v1/exercise-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExerciseDetails)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Bench Press")));
    }

    @Test
    public void testCreateExerciseDetails_ValidationError() throws Exception {
        when(exerciseDetailsService.createExerciseDetails(any(ExerciseDetails.class))).thenThrow(
                new ExerciseDetailsServiceException("Failed to create exercise details", ErrorType.VALIDATION_FAILED)
        );

        mockMvc.perform(post("/api/v1/exercise-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExerciseDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("VALIDATION_FAILED")));
    }

    @Test
    public void testUpdateExerciseDetails_Success() throws Exception {
        when(exerciseDetailsService.updateExerciseDetails(anyLong(), any(ExerciseDetails.class))).thenReturn(testExerciseDetails);

        mockMvc.perform(put("/api/v1/exercise-details/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExerciseDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Bench Press")));
    }

    @Test
    public void testUpdateExerciseDetails_NotFound() throws Exception {
        when(exerciseDetailsService.updateExerciseDetails(anyLong(), any(ExerciseDetails.class))).thenThrow(
                new ExerciseDetailsServiceException("Could not find exercise details with id: 999", ErrorType.ENTITY_NOT_FOUND)
        );

        mockMvc.perform(put("/api/v1/exercise-details/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExerciseDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")));
    }

    @Test
    public void testDeleteExerciseDetails_Success() throws Exception {
        doNothing().when(exerciseDetailsService).deleteExerciseDetails(1L);

        mockMvc.perform(delete("/api/v1/exercise-details/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteExerciseDetails_NotFound() throws Exception {
        doThrow(new ExerciseDetailsServiceException("Could not find exercise details with id: 999", ErrorType.ENTITY_NOT_FOUND))
                .when(exerciseDetailsService).deleteExerciseDetails(999L);

        mockMvc.perform(delete("/api/v1/exercise-details/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")));
    }

    @Test
    public void testPatchExerciseDetails_Success() throws Exception {
        String patchString = "[{\"op\": \"replace\", \"path\": \"/name\", \"value\": \"Modified Bench Press\"}]";
        JsonPatch jsonPatch = new ObjectMapper().readValue(patchString, JsonPatch.class);

        ExerciseDetails patchedExerciseDetails = new ExerciseDetails();
        patchedExerciseDetails.setId(1L);
        patchedExerciseDetails.setName("Modified Bench Press");
        patchedExerciseDetails.setDescription("Chest exercise");
        patchedExerciseDetails.setMuscleGroup("Chest");
        patchedExerciseDetails.setEquipment("Barbell");
        patchedExerciseDetails.setDifficultyLevel("Intermediate");

        when(exerciseDetailsService.patchExerciseDetails(anyLong(), any(JsonPatch.class)))
                .thenReturn(patchedExerciseDetails);

        mockMvc.perform(patch("/api/v1/exercise-details/1")
                        .contentType("application/json-patch+json")
                        .content(patchString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Modified Bench Press")));
    }

    @Test
    public void testPatchExerciseDetails_NotFound() throws Exception {
        String patchString = "[{\"op\": \"replace\", \"path\": \"/name\", \"value\": \"Modified Exercise\"}]";

        when(exerciseDetailsService.patchExerciseDetails(anyLong(), any(JsonPatch.class)))
                .thenThrow(new ExerciseDetailsServiceException(
                        "Could not find exercise details with id: 999",
                        ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(patch("/api/v1/exercise-details/999")
                        .contentType("application/json-patch+json")
                        .content(patchString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")));
    }

    @Test
    public void testPatchExerciseDetails_ValidationError() throws Exception {
        String patchString = "[{\"op\": \"replace\", \"path\": \"/difficultyLevel\", \"value\": \"\"}]";
        JsonPatch jsonPatch = new ObjectMapper().readValue(patchString, JsonPatch.class);

        when(exerciseDetailsService.patchExerciseDetails(anyLong(), any(JsonPatch.class)))
                .thenThrow(new ExerciseDetailsServiceException(
                        "Failed to apply patch: Validation failed",
                        ErrorType.VALIDATION_FAILED));

        mockMvc.perform(patch("/api/v1/exercise-details/1")
                        .contentType("application/json-patch+json")
                        .content(patchString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type", is("VALIDATION_FAILED")));
    }
}
