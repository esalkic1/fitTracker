package ba.unsa.etf.nwt.workout_service.ws;

import ba.unsa.etf.nwt.error_logging.model.ErrorResponse;
import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.domain.WorkoutTemplate;
import ba.unsa.etf.nwt.workout_service.dto.WorkoutTemplateDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.WorkoutTemplateServiceException;
import ba.unsa.etf.nwt.workout_service.services.WorkoutTemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class WorkoutTemplateControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WorkoutTemplateService workoutTemplateService;

    @InjectMocks
    private WorkoutTemplateController workoutTemplateController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(workoutTemplateController)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetAllWorkoutTemplates() throws Exception {
        WorkoutTemplate template1 = new WorkoutTemplate();
        template1.setId(1L);
        template1.setName("Template 1");
        template1.setDescription("Description 1");

        WorkoutTemplate template2 = new WorkoutTemplate();
        template2.setId(2L);
        template2.setName("Template 2");
        template2.setDescription("Description 2");

        List<WorkoutTemplate> templates = Arrays.asList(template1, template2);

        when(workoutTemplateService.getAllWorkoutTemplates()).thenReturn(templates);

        mockMvc.perform(get("/api/v1/workout-template")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Template 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Template 2")));

        verify(workoutTemplateService, times(1)).getAllWorkoutTemplates();
    }

    @Test
    public void testGetWorkoutTemplatesByUserId_Success() throws Exception {
        WorkoutTemplate template1 = new WorkoutTemplate();
        template1.setId(1L);
        template1.setName("User Template 1");

        User user = new User();
        user.setId(1L);
        template1.setUser(user);

        List<WorkoutTemplate> userTemplates = Arrays.asList(template1);

        when(workoutTemplateService.getWorkoutTemplatesByUserId(1L)).thenReturn(userTemplates);

        mockMvc.perform(get("/api/v1/workout-template/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("User Template 1")));

        verify(workoutTemplateService, times(1)).getWorkoutTemplatesByUserId(1L);
    }

    @Test
    public void testGetWorkoutTemplatesByUserId_Failure() throws Exception {
        when(workoutTemplateService.getWorkoutTemplatesByUserId(anyLong()))
                .thenThrow(new WorkoutTemplateServiceException("User not found", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/workout-template/user/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("User not found")))
                .andExpect(jsonPath("$.type", is(ErrorType.ENTITY_NOT_FOUND.toString())));

        verify(workoutTemplateService, times(1)).getWorkoutTemplatesByUserId(999L);
    }

    @Test
    public void testGetWorkoutTemplateById_Success() throws Exception {
        WorkoutTemplate template = new WorkoutTemplate();
        template.setId(1L);
        template.setName("Test Template");
        template.setDescription("Test Description");

        when(workoutTemplateService.getWorkoutTemplateById(1L)).thenReturn(template);

        mockMvc.perform(get("/api/v1/workout-template/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Template")))
                .andExpect(jsonPath("$.description", is("Test Description")));

        verify(workoutTemplateService, times(1)).getWorkoutTemplateById(1L);
    }

    @Test
    public void testGetWorkoutTemplateById_Failure() throws Exception {
        when(workoutTemplateService.getWorkoutTemplateById(anyLong()))
                .thenThrow(new WorkoutTemplateServiceException("Template not found", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/workout-template/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Template not found")))
                .andExpect(jsonPath("$.type", is(ErrorType.ENTITY_NOT_FOUND.toString())));

        verify(workoutTemplateService, times(1)).getWorkoutTemplateById(999L);
    }

    @Test
    public void testCreateWorkoutTemplate_Success() throws Exception {
        WorkoutTemplateDTO requestDTO = new WorkoutTemplateDTO();
        requestDTO.setName("New Template");
        requestDTO.setDescription("New Description");
        requestDTO.setUserId(1L);

        WorkoutTemplateDTO responseDTO = new WorkoutTemplateDTO();
        responseDTO.setId(1L);
        responseDTO.setName("New Template");
        responseDTO.setDescription("New Description");
        responseDTO.setUserId(1L);

        when(workoutTemplateService.createWorkoutTemplate(any(WorkoutTemplateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/workout-template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New Template")))
                .andExpect(jsonPath("$.description", is("New Description")))
                .andExpect(jsonPath("$.userId", is(1)));

        verify(workoutTemplateService, times(1)).createWorkoutTemplate(any(WorkoutTemplateDTO.class));
    }

    @Test
    public void testCreateWorkoutTemplate_Failure() throws Exception {
        WorkoutTemplateDTO requestDTO = new WorkoutTemplateDTO();
        requestDTO.setName("New Template");
        requestDTO.setUserId(999L);

        when(workoutTemplateService.createWorkoutTemplate(any(WorkoutTemplateDTO.class)))
                .thenThrow(new WorkoutTemplateServiceException("User not found", ErrorType.VALIDATION_FAILED));

        mockMvc.perform(post("/api/v1/workout-template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("User not found")))
                .andExpect(jsonPath("$.type", is(ErrorType.VALIDATION_FAILED.toString())));

        verify(workoutTemplateService, times(1)).createWorkoutTemplate(any(WorkoutTemplateDTO.class));
    }

    @Test
    public void testUpdateWorkoutTemplate_Success() throws Exception {
        WorkoutTemplateDTO requestDTO = new WorkoutTemplateDTO();
        requestDTO.setName("Updated Template");
        requestDTO.setDescription("Updated Description");
        requestDTO.setUserId(1L);

        WorkoutTemplateDTO responseDTO = new WorkoutTemplateDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Updated Template");
        responseDTO.setDescription("Updated Description");
        responseDTO.setUserId(1L);

        when(workoutTemplateService.updateWorkoutTemplate(eq(1L), any(WorkoutTemplateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/workout-template/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Template")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.userId", is(1)));

        verify(workoutTemplateService, times(1)).updateWorkoutTemplate(eq(1L), any(WorkoutTemplateDTO.class));
    }

    @Test
    public void testUpdateWorkoutTemplate_Failure() throws Exception {
        WorkoutTemplateDTO requestDTO = new WorkoutTemplateDTO();
        requestDTO.setName("Updated Template");
        requestDTO.setUserId(1L);

        when(workoutTemplateService.updateWorkoutTemplate(eq(999L), any(WorkoutTemplateDTO.class)))
                .thenThrow(new WorkoutTemplateServiceException("Template not found", ErrorType.ENTITY_NOT_FOUND));

        mockMvc.perform(put("/api/v1/workout-template/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Template not found")))
                .andExpect(jsonPath("$.type", is(ErrorType.ENTITY_NOT_FOUND.toString())));

        verify(workoutTemplateService, times(1)).updateWorkoutTemplate(eq(999L), any(WorkoutTemplateDTO.class));
    }

    @Test
    public void testDeleteWorkoutTemplate_Success() throws Exception {

        mockMvc.perform(delete("/api/v1/workout-template/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(workoutTemplateService, times(1)).deleteWorkoutTemplate(1L);
    }

    @Test
    public void testDeleteWorkoutTemplate_Failure() throws Exception {
        doThrow(new WorkoutTemplateServiceException("Template not found", ErrorType.ENTITY_NOT_FOUND))
                .when(workoutTemplateService).deleteWorkoutTemplate(999L);

        mockMvc.perform(delete("/api/v1/workout-template/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Template not found")))
                .andExpect(jsonPath("$.type", is(ErrorType.ENTITY_NOT_FOUND.toString())));

        verify(workoutTemplateService, times(1)).deleteWorkoutTemplate(999L);
    }
}