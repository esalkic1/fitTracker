package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseTemplate;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseTemplateDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseTemplateServiceException;
import ba.unsa.etf.nwt.workout_service.services.ExerciseTemplateService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ExerciseTemplateControllerTest {

    @Mock
    private ExerciseTemplateService exerciseTemplateService;

    @InjectMocks
    private ExerciseTemplateController exerciseTemplateController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ExerciseTemplate exerciseTemplate;
    private ExerciseTemplateDTO exerciseTemplateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(exerciseTemplateController)
                .build();
        objectMapper = new ObjectMapper();

        exerciseTemplate = new ExerciseTemplate();
        exerciseTemplate.setId(1L);

        exerciseTemplateDTO = new ExerciseTemplateDTO();
        exerciseTemplateDTO.setId(1L);
        exerciseTemplateDTO.setExerciseDetailsId(1L);
        exerciseTemplateDTO.setWorkoutTemplateId(1L);
    }

    @Test
    void getAllExerciseTemplates_ShouldReturnAllTemplates() throws Exception {
        List<ExerciseTemplate> templates = Arrays.asList(exerciseTemplate);
        when(exerciseTemplateService.getAllExerciseTemplates()).thenReturn(templates);

        mockMvc.perform(get("/api/v1/exercise-template")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(exerciseTemplateService, times(1)).getAllExerciseTemplates();
    }

    @Test
    void getExerciseTemplate_WithValidId_ShouldReturnTemplate() throws Exception {
        when(exerciseTemplateService.getExerciseTemplateById(1L)).thenReturn(exerciseTemplate);

        mockMvc.perform(get("/api/v1/exercise-template/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(exerciseTemplateService, times(1)).getExerciseTemplateById(1L);
    }

    @Test
    void getExerciseTemplate_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        when(exerciseTemplateService.getExerciseTemplateById(99L))
                .thenThrow(new ExerciseTemplateServiceException("Could not find exercise template with id: 99", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/exercise-template/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Could not find exercise template with id: 99"));

        verify(exerciseTemplateService, times(1)).getExerciseTemplateById(99L);
    }

    @Test
    void createExerciseTemplate_WithValidData_ShouldCreateAndReturnTemplate() throws Exception {
        when(exerciseTemplateService.createExerciseTemplate(any(ExerciseTemplateDTO.class))).thenReturn(exerciseTemplateDTO);

        mockMvc.perform(post("/api/v1/exercise-template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseTemplateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.exerciseDetailsId").value(1))
                .andExpect(jsonPath("$.workoutTemplateId").value(1));

        verify(exerciseTemplateService, times(1)).createExerciseTemplate(any(ExerciseTemplateDTO.class));
    }

    @Test
    void createExerciseTemplate_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        when(exerciseTemplateService.createExerciseTemplate(any(ExerciseTemplateDTO.class)))
                .thenThrow(new ExerciseTemplateServiceException("Failed to create exercise template", ErrorType.VALIDATION_FAILED));

        mockMvc.perform(post("/api/v1/exercise-template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseTemplateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Failed to create exercise template"));

        verify(exerciseTemplateService, times(1)).createExerciseTemplate(any(ExerciseTemplateDTO.class));
    }

    @Test
    void updateExerciseTemplate_WithValidData_ShouldUpdateAndReturnTemplate() throws Exception {
        when(exerciseTemplateService.updateExerciseTemplate(eq(1L), any(ExerciseTemplateDTO.class))).thenReturn(exerciseTemplateDTO);

        mockMvc.perform(put("/api/v1/exercise-template/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseTemplateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.exerciseDetailsId").value(1))
                .andExpect(jsonPath("$.workoutTemplateId").value(1));

        verify(exerciseTemplateService, times(1)).updateExerciseTemplate(eq(1L), any(ExerciseTemplateDTO.class));
    }

    @Test
    void updateExerciseTemplate_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        when(exerciseTemplateService.updateExerciseTemplate(eq(99L), any(ExerciseTemplateDTO.class)))
                .thenThrow(new ExerciseTemplateServiceException("Could not find exercise template with id: 99", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(put("/api/v1/exercise-template/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseTemplateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Could not find exercise template with id: 99"));

        verify(exerciseTemplateService, times(1)).updateExerciseTemplate(eq(99L), any(ExerciseTemplateDTO.class));
    }

    @Test
    void deleteExerciseTemplate_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(exerciseTemplateService).deleteExerciseTemplate(1L);

        mockMvc.perform(delete("/api/v1/exercise-template/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(exerciseTemplateService, times(1)).deleteExerciseTemplate(1L);
    }

    @Test
    void deleteExerciseTemplate_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        doThrow(new ExerciseTemplateServiceException("Could not find exercise template with id: 99", ErrorType.ENTITY_NOT_FOUND))
                .when(exerciseTemplateService).deleteExerciseTemplate(99L);

        mockMvc.perform(delete("/api/v1/exercise-template/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Could not find exercise template with id: 99"));

        verify(exerciseTemplateService, times(1)).deleteExerciseTemplate(99L);
    }
}