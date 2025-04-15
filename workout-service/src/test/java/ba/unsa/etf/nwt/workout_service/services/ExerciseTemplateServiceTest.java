package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.error_logging.model.ErrorType;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseDetails;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseTemplate;
import ba.unsa.etf.nwt.workout_service.domain.WorkoutTemplate;
import ba.unsa.etf.nwt.workout_service.dto.ExerciseTemplateDTO;
import ba.unsa.etf.nwt.workout_service.exceptions.ExerciseTemplateServiceException;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExerciseTemplateServiceTest {

    @Mock
    private ExerciseTemplateRepository exerciseTemplateRepository;

    @Mock
    private ExerciseDetailsService exerciseDetailsService;

    @Mock
    private WorkoutTemplateService workoutTemplateService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ExerciseTemplateService exerciseTemplateService;

    private ExerciseTemplate exerciseTemplate;
    private ExerciseDetails exerciseDetails;
    private WorkoutTemplate workoutTemplate;
    private ExerciseTemplateDTO exerciseTemplateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        exerciseDetails = new ExerciseDetails();
        exerciseDetails.setId(1L);
        exerciseDetails.setName("Push-ups");
        exerciseDetails.setDescription("Basic push-up exercise");

        workoutTemplate = new WorkoutTemplate();
        workoutTemplate.setId(1L);
        workoutTemplate.setName("Upper Body Workout");

        exerciseTemplate = new ExerciseTemplate();
        exerciseTemplate.setId(1L);
        exerciseTemplate.setExerciseDetails(exerciseDetails);
        exerciseTemplate.setWorkoutTemplate(workoutTemplate);

        exerciseTemplateDTO = new ExerciseTemplateDTO();
        exerciseTemplateDTO.setId(1L);
        exerciseTemplateDTO.setExerciseDetailsId(1L);
        exerciseTemplateDTO.setWorkoutTemplateId(1L);

        when(modelMapper.map(any(ExerciseTemplate.class), eq(ExerciseTemplateDTO.class))).thenReturn(exerciseTemplateDTO);
    }

    @Test
    void getAllExerciseTemplates_ShouldReturnAllTemplates() {
        List<ExerciseTemplate> templates = Arrays.asList(exerciseTemplate);
        when(exerciseTemplateRepository.findAll()).thenReturn(templates);

        List<ExerciseTemplate> result = exerciseTemplateService.getAllExerciseTemplates();

        assertEquals(1, result.size());
        assertEquals(exerciseTemplate.getId(), result.get(0).getId());
        verify(exerciseTemplateRepository, times(1)).findAll();
    }

    @Test
    void getExerciseTemplateById_WithValidId_ShouldReturnTemplate() throws ExerciseTemplateServiceException {
        when(exerciseTemplateRepository.findById(1L)).thenReturn(Optional.of(exerciseTemplate));

        ExerciseTemplate result = exerciseTemplateService.getExerciseTemplateById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(exerciseDetails, result.getExerciseDetails());
        assertEquals(workoutTemplate, result.getWorkoutTemplate());
        verify(exerciseTemplateRepository, times(1)).findById(1L);
    }

    @Test
    void getExerciseTemplateById_WithInvalidId_ShouldThrowException() {
        when(exerciseTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        ExerciseTemplateServiceException exception = assertThrows(
                ExerciseTemplateServiceException.class,
                () -> exerciseTemplateService.getExerciseTemplateById(99L)
        );

        assertEquals("Could not find exercise template with id: 99", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());
        verify(exerciseTemplateRepository, times(1)).findById(99L);
    }

    @Test
    void createExerciseTemplate_WithValidData_ShouldCreateAndReturnTemplate() throws Exception {
        when(exerciseDetailsService.getExerciseDetailsById(1L)).thenReturn(exerciseDetails);
        when(workoutTemplateService.getWorkoutTemplateById(1L)).thenReturn(workoutTemplate);
        when(exerciseTemplateRepository.save(any(ExerciseTemplate.class))).thenReturn(exerciseTemplate);

        ExerciseTemplateDTO result = exerciseTemplateService.createExerciseTemplate(exerciseTemplateDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getExerciseDetailsId());
        assertEquals(1L, result.getWorkoutTemplateId());

        verify(exerciseDetailsService, times(1)).getExerciseDetailsById(1L);
        verify(workoutTemplateService, times(1)).getWorkoutTemplateById(1L);
        verify(exerciseTemplateRepository, times(1)).save(any(ExerciseTemplate.class));
    }

    @Test
    void createExerciseTemplate_WithInvalidData_ShouldThrowException() throws Exception {
        when(exerciseDetailsService.getExerciseDetailsById(1L))
                .thenThrow(new RuntimeException("Exercise details not found"));

        ExerciseTemplateServiceException exception = assertThrows(
                ExerciseTemplateServiceException.class,
                () -> exerciseTemplateService.createExerciseTemplate(exerciseTemplateDTO)
        );

        assertTrue(exception.getMessage().contains("Failed to create exercise template"));
        assertEquals(ErrorType.VALIDATION_FAILED, exception.getErrorType());

        verify(exerciseDetailsService, times(1)).getExerciseDetailsById(1L);
        verify(exerciseTemplateRepository, never()).save(any(ExerciseTemplate.class));
    }

    @Test
    void updateExerciseTemplate_WithValidData_ShouldUpdateAndReturnTemplate() throws Exception {
        when(exerciseTemplateRepository.findById(1L)).thenReturn(Optional.of(exerciseTemplate));
        when(exerciseDetailsService.getExerciseDetailsById(1L)).thenReturn(exerciseDetails);
        when(workoutTemplateService.getWorkoutTemplateById(1L)).thenReturn(workoutTemplate);
        when(exerciseTemplateRepository.save(any(ExerciseTemplate.class))).thenReturn(exerciseTemplate);

        ExerciseTemplateDTO result = exerciseTemplateService.updateExerciseTemplate(1L, exerciseTemplateDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getExerciseDetailsId());
        assertEquals(1L, result.getWorkoutTemplateId());

        verify(exerciseTemplateRepository, times(1)).findById(1L);
        verify(exerciseDetailsService, times(1)).getExerciseDetailsById(1L);
        verify(workoutTemplateService, times(1)).getWorkoutTemplateById(1L);
        verify(exerciseTemplateRepository, times(1)).save(any(ExerciseTemplate.class));
    }

    @Test
    void updateExerciseTemplate_WithInvalidId_ShouldThrowException() {
        when(exerciseTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        ExerciseTemplateServiceException exception = assertThrows(
                ExerciseTemplateServiceException.class,
                () -> exerciseTemplateService.updateExerciseTemplate(99L, exerciseTemplateDTO)
        );

        assertEquals("Could not find exercise template with id: 99", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());

        verify(exerciseTemplateRepository, times(1)).findById(99L);
        verify(exerciseTemplateRepository, never()).save(any(ExerciseTemplate.class));
    }

    @Test
    void deleteExerciseTemplate_WithValidId_ShouldDeleteTemplate() throws Exception {
        when(exerciseTemplateRepository.findById(1L)).thenReturn(Optional.of(exerciseTemplate));
        doNothing().when(exerciseTemplateRepository).delete(exerciseTemplate);

        exerciseTemplateService.deleteExerciseTemplate(1L);

        verify(exerciseTemplateRepository, times(1)).findById(1L);
        verify(exerciseTemplateRepository, times(1)).delete(exerciseTemplate);
    }

    @Test
    void deleteExerciseTemplate_WithInvalidId_ShouldThrowException() {
        when(exerciseTemplateRepository.findById(99L)).thenReturn(Optional.empty());

        ExerciseTemplateServiceException exception = assertThrows(
                ExerciseTemplateServiceException.class,
                () -> exerciseTemplateService.deleteExerciseTemplate(99L)
        );

        assertEquals("Could not find exercise template with id: 99", exception.getMessage());
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.getErrorType());

        verify(exerciseTemplateRepository, times(1)).findById(99L);
        verify(exerciseTemplateRepository, never()).delete(any(ExerciseTemplate.class));
    }
}