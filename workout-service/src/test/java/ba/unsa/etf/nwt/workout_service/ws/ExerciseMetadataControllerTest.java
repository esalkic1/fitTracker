package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.Exercise;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseMetadata;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseMetadataDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseMetadataServiceException;
import ba.unsa.etf.nwt.workout_service.services.ExerciseMetadataService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ExerciseMetadataControllerTest {

    @Mock
    private ExerciseMetadataService exerciseMetadataService;

    @InjectMocks
    private ExerciseMetadataController exerciseMetadataController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Exercise exercise;
    private ExerciseMetadata exerciseMetadata;
    private ExerciseMetadataDTO exerciseMetadataDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(exerciseMetadataController)
                .build();
        objectMapper = new ObjectMapper();

        exercise = new Exercise();
        exercise.setId(1L);

        exerciseMetadata = new ExerciseMetadata();
        exerciseMetadata.setId(1L);
        exerciseMetadata.setExercise(exercise);
        exerciseMetadata.setAdditionalNotes("Keep your back straight");

        exerciseMetadataDTO = new ExerciseMetadataDTO();
        exerciseMetadataDTO.setExerciseId(1L);
        exerciseMetadataDTO.setAdditionalNotes("Keep your back straight");
    }

    @Test
    void getAllExerciseMetadata_ShouldReturnAllMetadata() throws Exception {
        List<ExerciseMetadata> metadataList = Arrays.asList(exerciseMetadata);
        when(exerciseMetadataService.getAllExerciseMetadata()).thenReturn(metadataList);

        mockMvc.perform(get("/api/v1/exercise-metadata")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(exerciseMetadataService, times(1)).getAllExerciseMetadata();
    }

    @Test
    void getExerciseMetadataById_WhenMetadataExists_ShouldReturnMetadata() throws Exception {
        when(exerciseMetadataService.getExerciseMetadataById(1L)).thenReturn(exerciseMetadata);

        mockMvc.perform(get("/api/v1/exercise-metadata/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.additionalNotes", is("Keep your back straight")));

        verify(exerciseMetadataService, times(1)).getExerciseMetadataById(1L);
    }

    @Test
    void getExerciseMetadataById_WhenMetadataDoesNotExist_ShouldReturnBadRequest() throws Exception {
        when(exerciseMetadataService.getExerciseMetadataById(1L))
                .thenThrow(new ExerciseMetadataServiceException("Could not find exercise metadata with id: 1", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/exercise-metadata/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Could not find exercise metadata with id: 1")))
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")));

        verify(exerciseMetadataService, times(1)).getExerciseMetadataById(1L);
    }

    @Test
    void getExerciseMetadataByExercise_WhenMetadataExists_ShouldReturnMetadata() throws Exception {
        when(exerciseMetadataService.getExerciseMetadataByExerciseId(1L)).thenReturn(exerciseMetadata);

        mockMvc.perform(get("/api/v1/exercise-metadata/exercise/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.additionalNotes", is("Keep your back straight")));

        verify(exerciseMetadataService, times(1)).getExerciseMetadataByExerciseId(1L);
    }

    @Test
    void getExerciseMetadataByExercise_WhenMetadataDoesNotExist_ShouldReturnBadRequest() throws Exception {
        when(exerciseMetadataService.getExerciseMetadataByExerciseId(1L))
                .thenThrow(new ExerciseMetadataServiceException("No metadata found for exercise with id: 1", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/exercise-metadata/exercise/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("No metadata found for exercise with id: 1")))
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")));

        verify(exerciseMetadataService, times(1)).getExerciseMetadataByExerciseId(1L);
    }

    @Test
    void createExerciseMetadata_WhenValidData_ShouldCreateAndReturnMetadata() throws Exception {
        when(exerciseMetadataService.createExerciseMetadata(any(ExerciseMetadataDTO.class))).thenReturn(exerciseMetadataDTO);

        mockMvc.perform(post("/api/v1/exercise-metadata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseMetadataDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exerciseId", is(1)))
                .andExpect(jsonPath("$.additionalNotes", is("Keep your back straight")));

        verify(exerciseMetadataService, times(1)).createExerciseMetadata(any(ExerciseMetadataDTO.class));
    }

    @Test
    void createExerciseMetadata_WhenInvalidData_ShouldReturnBadRequest() throws Exception {
        when(exerciseMetadataService.createExerciseMetadata(any(ExerciseMetadataDTO.class)))
                .thenThrow(new ExerciseMetadataServiceException("Failed to create exercise metadata", ErrorType.VALIDATION_FAILED));

        mockMvc.perform(post("/api/v1/exercise-metadata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseMetadataDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Failed to create exercise metadata")))
                .andExpect(jsonPath("$.type", is("VALIDATION_FAILED")));

        verify(exerciseMetadataService, times(1)).createExerciseMetadata(any(ExerciseMetadataDTO.class));
    }

    @Test
    void updateExerciseMetadata_WhenValidData_ShouldUpdateAndReturnMetadata() throws Exception {
        when(exerciseMetadataService.updateExerciseMetadata(eq(1L), any(ExerciseMetadataDTO.class))).thenReturn(exerciseMetadataDTO);

        mockMvc.perform(put("/api/v1/exercise-metadata/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseMetadataDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseId", is(1)))
                .andExpect(jsonPath("$.additionalNotes", is("Keep your back straight")));

        verify(exerciseMetadataService, times(1)).updateExerciseMetadata(eq(1L), any(ExerciseMetadataDTO.class));
    }

    @Test
    void updateExerciseMetadata_WhenMetadataDoesNotExist_ShouldReturnBadRequest() throws Exception {
        when(exerciseMetadataService.updateExerciseMetadata(eq(1L), any(ExerciseMetadataDTO.class)))
                .thenThrow(new ExerciseMetadataServiceException("Could not find exercise metadata with id: 1", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(put("/api/v1/exercise-metadata/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseMetadataDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Could not find exercise metadata with id: 1")))
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")));

        verify(exerciseMetadataService, times(1)).updateExerciseMetadata(eq(1L), any(ExerciseMetadataDTO.class));
    }

    @Test
    void deleteExerciseMetadata_WhenMetadataExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(exerciseMetadataService).deleteExerciseMetadata(1L);

        mockMvc.perform(delete("/api/v1/exercise-metadata/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(exerciseMetadataService, times(1)).deleteExerciseMetadata(1L);
    }

    @Test
    void deleteExerciseMetadata_WhenMetadataDoesNotExist_ShouldReturnBadRequest() throws Exception {
        doThrow(new ExerciseMetadataServiceException("Could not find exercise metadata with id: 1", ErrorType.ENTITY_NOT_FOUND))
                .when(exerciseMetadataService).deleteExerciseMetadata(1L);

        mockMvc.perform(delete("/api/v1/exercise-metadata/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Could not find exercise metadata with id: 1")))
                .andExpect(jsonPath("$.type", is("ENTITY_NOT_FOUND")));

        verify(exerciseMetadataService, times(1)).deleteExerciseMetadata(1L);
    }
}